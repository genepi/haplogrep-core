package contamination;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import contamination.objects.ContaminationEntry;
import contamination.objects.Variant;
import contamination.objects.Sample;
import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class Contamination {
	
	enum Status 
	{ 
		HG_Conflict_High, HG_Conflict_Low, Low_Coverage, None; 
	}

	// TODO add threshold variable
	public int calcContamination(HashMap<String, Sample> mutationSamples, ArrayList<TestSample> haplogrepSamples, String out) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countCovLow = 0;

		Collections.sort((List<TestSample>) haplogrepSamples);
		
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		CsvTableWriter contaminationWriter = new CsvTableWriter(out, '\t');

		String[] columnsWrite = { "SampleID", "Contamination", "MajorHG", "MajorLevel", "MajorSNPs", "MajorHGvariants", "MinorHG", "MinorLevel", "MinorSNPs",
				"MinorHGvariants", "MeanCoverage", "HG_Distance"};
		contaminationWriter.setColumns(columnsWrite);

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {
				
				countEntries++;
				int distanceHG = 0;
				Status status;

				TestSample sampleMajor = haplogrepSamples.get(i);
				TestSample sampleMinor = haplogrepSamples.get(i + 1);
				
				ArrayList<Polymorphism> foundMajor = sampleMajor.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMajor = sampleMajor.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();
				ArrayList<Polymorphism> foundMinor = sampleMinor.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMinor = sampleMinor.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();

				int notFoundMajor = countNotFound(foundMajor, expectedMajor);
				int notFoundMinor = countNotFound(foundMinor, expectedMinor);

				ContaminationEntry centry = new ContaminationEntry();
				centry.setSampleId(sampleMajor.getSampleID().split("_maj")[0]);
				
				Sample currentSample = mutationSamples.get(centry.getSampleId());
				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();
				double meanCov = currentSample.getTotalCoverage() / currentSample.getAmountVariants();
				
				centry.setMajorId(sampleMajor.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMajor);
				centry.setMinorId(sampleMinor.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMinor);

				int countMajorHomoplasmies = countHomoplasmies(currentSample, foundMajor);
				int countMinorHomoplasmies = countHomoplasmies(currentSample, foundMinor);

				double meanMajor = getMeanScores(currentSample, foundMajor);
				double meanMinor = getMeanScores(currentSample, foundMinor);

				String amountHomoplasmyMajor = countMajorHomoplasmies + "/" + sampleHomoplasmies;
				String homoplHomoplasmyMinor = countMinorHomoplasmies + "/" + sampleHomoplasmies;

				if (!centry.getMajorId().equals(centry.getMinorId())) {

					distanceHG = calcDistance(centry, phylotree);

					if ( ((foundMajor.size() - countMajorHomoplasmies) > 2 || (foundMinor.size() - countMinorHomoplasmies) > 2)
							&& (distanceHG > 1 || distanceHG == -1)) {
						countContaminated++;
						status = Status.HG_Conflict_High;
					} else if (((foundMinor.size() - countMinorHomoplasmies) > 1) || distanceHG > 1) {
						countPossibleContaminated++;
						status =  Status.HG_Conflict_Low;
					} else {
						status = Status.None;
					}
				} else if (meanCov < 200) {
					countCovLow++;
					status = Status.Low_Coverage;
				} else {
					status = Status.None;
				}

				contaminationWriter.setString(0, centry.getSampleId());
				contaminationWriter.setString(1, status.toString());
				contaminationWriter.setString(2, centry.getMajorId());
				contaminationWriter.setString(3, formatter.format(meanMajor));
				contaminationWriter.setString(4, amountHomoplasmyMajor);
				contaminationWriter.setInteger(5, foundMajor.size() - countMajorHomoplasmies);
				contaminationWriter.setString(6, centry.getMinorId());
				contaminationWriter.setString(7, formatter.format(meanMinor));
				contaminationWriter.setString(8, homoplHomoplasmyMinor);
				contaminationWriter.setInteger(9, foundMinor.size() - countMinorHomoplasmies);
				contaminationWriter.setDouble(10, meanCov);
				contaminationWriter.setInteger(11, distanceHG);

				contaminationWriter.next();
			}

			System.out.println("Haplogroup based conflicts: " + countContaminated + " of " + countEntries);
			System.out.println("Minor haplogroup conflicts: " + countPossibleContaminated);
			System.out.println("Coverage low (<200x) : " + countCovLow);

			contaminationWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	private int calcDistance(ContaminationEntry centry, Phylotree phylotree) {
		int distanceHG;
		Haplogroup hgMajor = new Haplogroup(centry.getMajorId());
		Haplogroup hgMinor = new Haplogroup(centry.getMinorId());
		if (hgMajor.isSuperHaplogroup(phylotree, hgMinor)) {
			distanceHG = hgMajor.distanceToSuperHaplogroup(phylotree, hgMinor);
		} else if (hgMinor.isSuperHaplogroup(phylotree, hgMajor)) {
			distanceHG = hgMinor.distanceToSuperHaplogroup(phylotree, hgMajor);
		} else {
			distanceHG = hgMajor.distanceToSuperHaplogroup(phylotree, hgMinor);
		}
		return distanceHG;
	}

	private int countNotFound(ArrayList<Polymorphism> found, ArrayList<Polymorphism> expected) {
		int notFoundMajor = 0;
		for (Polymorphism currentPoly : expected) {
			if (!found.contains(currentPoly))
				notFoundMajor++;
		}
		return notFoundMajor;
	}

	private double getMeanScores(Sample currentSample, ArrayList<Polymorphism> found) {

		double sum = 0.0;
		int count = 0;

		for (Polymorphism split : found) {

			Variant variant = currentSample.getPositions().get(split.toString());

			if (variant != null && variant.getType() == 2) {
				sum += variant.getLevel();
				count++;
			}

		}
		return sum / count;
	}

	private int countHomoplasmies(Sample currentSample, ArrayList<Polymorphism> found) {

		int count = 0;

		for (Polymorphism split : found) {

			Variant variant = currentSample.getPositions().get(split.toString());

			if (variant != null && variant.getType() == 1) {
				count++;
			}

		}
		return count;
	}

}