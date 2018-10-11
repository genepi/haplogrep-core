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

	enum Status {
		HG_Conflict_High, HG_Conflict_Low, Low_Coverage, None;
	}

	// TODO add threshold variable
	public int calcContamination(HashMap<String, Sample> mutationSamples, ArrayList<TestSample> haplogrepSamples, String out) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countCovLow = 0;
		int countNone = 0;

		int requiredCoverage = 200;

		Collections.sort((List<TestSample>) haplogrepSamples);

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		CsvTableWriter contaminationWriter = new CsvTableWriter(out, '\t');

		String[] columnsWrite = { "SampleID", "Contamination", "MajorHG", "MajorLevel", "MajorSNPs", "MajorHGvariants", "MinorHG", "MinorLevel", "MinorSNPs",
				"MinorHGvariants", "MeanCoverage", "HG_Distance" };
		contaminationWriter.setColumns(columnsWrite);

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {

				countEntries++;
				int distanceHG = 0;
				Status status;

				TestSample sampleMajor = haplogrepSamples.get(i);
				TestSample sampleMinor = haplogrepSamples.get(i + 1);

				ArrayList<Polymorphism> foundMajorHg = sampleMajor.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMajorHg = sampleMajor.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();
				ArrayList<Polymorphism> foundMinorHg = sampleMinor.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMinorHg = sampleMinor.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();

				int notFoundMajorHg = countNotFound(foundMajorHg, expectedMajorHg);
				int notFoundMinorHg = countNotFound(foundMinorHg, expectedMinorHg);

				ContaminationEntry centry = new ContaminationEntry();
				centry.setSampleId(sampleMajor.getSampleID().split("_maj")[0]);

				Sample currentSample = mutationSamples.get(centry.getSampleId());
				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();
				double meanCoverageSample = currentSample.getTotalCoverage() / currentSample.getAmountVariants();

				centry.setMajorId(sampleMajor.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMajorHg);
				centry.setMinorId(sampleMinor.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMinorHg);

				int countMajorHomoplasmiesInSample = countHomoplasmies(currentSample, foundMajorHg);
				int countMinorHomoplasmiesInSample = countHomoplasmies(currentSample, foundMinorHg);

				double meanHeteroplasmyMajor = getMeanHeteroplasmy(currentSample, foundMajorHg, true);
				double meanheteroplasmyMinor = getMeanHeteroplasmy(currentSample, foundMinorHg, false);

				if (!centry.getMajorId().equals(centry.getMinorId())) {

					distanceHG = calcDistance(centry, phylotree);

					if (((foundMajorHg.size() - countMajorHomoplasmiesInSample) > 2 || (foundMinorHg.size() - countMinorHomoplasmiesInSample) > 2)
							&& (distanceHG > 1 || distanceHG == -1)) {
						countContaminated++;
						status = Status.HG_Conflict_High;
					} else if (((foundMinorHg.size() - countMinorHomoplasmiesInSample) > 1) || distanceHG > 1) {
						countPossibleContaminated++;
						status = Status.HG_Conflict_Low;
					} else {
						status = Status.None;
					}
				} else if (meanCoverageSample < requiredCoverage) {
					countCovLow++;
					status = Status.Low_Coverage;
				} else {
					countNone++;
					status = Status.None;
				}

				contaminationWriter.setString(0, centry.getSampleId());
				contaminationWriter.setString(1, status.toString());
				contaminationWriter.setString(2, centry.getMajorId());
				contaminationWriter.setString(3, formatter.format(meanHeteroplasmyMajor));
				contaminationWriter.setString(4, countMajorHomoplasmiesInSample + "/" + sampleHomoplasmies);
				contaminationWriter.setInteger(5, foundMajorHg.size() - countMajorHomoplasmiesInSample);
				contaminationWriter.setString(6, centry.getMinorId());
				contaminationWriter.setString(7, formatter.format(meanheteroplasmyMinor));
				contaminationWriter.setString(8, countMinorHomoplasmiesInSample + "/" + sampleHomoplasmies);
				contaminationWriter.setInteger(9, foundMinorHg.size() - countMinorHomoplasmiesInSample);
				contaminationWriter.setDouble(10, meanCoverageSample);
				contaminationWriter.setInteger(11, distanceHG);

				contaminationWriter.next();
			}

			System.out.println("Total amount of samples: " + countEntries);
			System.out.println("Coverage too low for contamination check (<" + requiredCoverage + "x): " + countCovLow);
			System.out.println("Major haplogroup conflicts: " + countContaminated);
			System.out.println("Minor haplogroup conflicts: " + countPossibleContaminated);
			System.out.println("No conflicts: " + countNone);
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
		int count = 0;
		for (Polymorphism currentPoly : expected) {
			if (!found.contains(currentPoly))
				count++;
		}
		return count;
	}

	private double getMeanHeteroplasmy(Sample currentSample, ArrayList<Polymorphism> found, boolean majorComponent) {

		double sum = 0.0;
		double count = 0;

		for (Polymorphism split : found) {
			Variant variant = currentSample.getPositions().get(split.toString());

			if (variant != null && variant.getType() == 2) {
				if (majorComponent) {
					sum += variant.getMajorLevel();
				} else {

					sum += variant.getMinorLevel();
				}
				count++;
			}

		}
		if (count > 0) {
			return sum / count;
		} else {
			return 0.0;
		}
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