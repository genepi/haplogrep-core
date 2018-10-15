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

		String[] columnsWrite = { "SampleID", "Contamination", "MajorHG", "MajorLevel", "MajorSNPs", "MajorHetVariants", "MinorHG", "MinorLevel", "MinorSNPs",
				"MinorHetVariants", "MeanCoverage", "HG_Distance" };
		contaminationWriter.setColumns(columnsWrite);

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {

				countEntries++;
				int distanceHG = 0;
				Status status;

				TestSample majorSample = haplogrepSamples.get(i);
				TestSample minorSample = haplogrepSamples.get(i + 1);

				ArrayList<Polymorphism> foundMajor = majorSample.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMajor = majorSample.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();
				ArrayList<Polymorphism> foundMinor = minorSample.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMinor = minorSample.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();

				int notFoundMajor = countNotFound(foundMajor, expectedMajor);
				int notFoundMinor = countNotFound(foundMinor, expectedMinor);

				ContaminationEntry centry = new ContaminationEntry();
				centry.setSampleId(majorSample.getSampleID().split("_maj")[0]);

				Sample currentSample = mutationSamples.get(centry.getSampleId());
				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();
				double meanCoverageSample = currentSample.getTotalCoverage() / currentSample.getAmountVariants();

				centry.setMajorId(majorSample.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMajor);
				centry.setMinorId(minorSample.getTopResult().getHaplogroup().toString());
				centry.setMajorRemaining(notFoundMinor);

				int homoplasmiesMajor = countHomoplasmies(currentSample, foundMajor);
				int heteroplasmiesMajor = foundMajor.size() - homoplasmiesMajor;

				int homoplasmiesMinor = countHomoplasmies(currentSample, foundMinor);
				int heteroplasmiesMinor = foundMinor.size() - homoplasmiesMinor;

				double meanHeteroplasmyMajor = calcMeanHeteroplasmy(currentSample, foundMajor, true);
				double meanheteroplasmyMinor = calcMeanHeteroplasmy(currentSample, foundMinor, false);

				if (!centry.getMajorId().equals(centry.getMinorId())) {

					distanceHG = calcDistance(centry, phylotree);

					if ((heteroplasmiesMajor > 2 || heteroplasmiesMinor > 2) && (distanceHG > 1 || distanceHG == -1)) {
						countContaminated++;
						status = Status.HG_Conflict_High;
					} else if ((heteroplasmiesMinor > 1) || distanceHG > 1) {
						countPossibleContaminated++;
						status = Status.HG_Conflict_Low;
					} else {
						status = Status.None;
					}
				} else {
					countNone++;
					status = Status.None;
				}

				/*
				 * if (meanCoverageSample < requiredCoverage) { countCovLow++; status =
				 * Status.Low_Coverage; }
				 */

				contaminationWriter.setString(0, centry.getSampleId());
				contaminationWriter.setString(1, status.toString());
				contaminationWriter.setString(2, centry.getMajorId());
				contaminationWriter.setString(3, formatter.format(meanHeteroplasmyMajor));
				contaminationWriter.setString(4, homoplasmiesMajor + "/" + sampleHomoplasmies);
				contaminationWriter.setInteger(5, heteroplasmiesMajor);
				contaminationWriter.setString(6, centry.getMinorId());
				contaminationWriter.setString(7, formatter.format(meanheteroplasmyMinor));
				contaminationWriter.setString(8, homoplasmiesMinor + "/" + sampleHomoplasmies);
				contaminationWriter.setInteger(9, heteroplasmiesMinor);
				contaminationWriter.setDouble(10, meanCoverageSample);
				contaminationWriter.setInteger(11, distanceHG);
				contaminationWriter.next();
			}

			System.out.println("Total amount of samples: " + countEntries);
			System.out.println("Major haplogroup conflicts: " + countContaminated);
			System.out.println("Minor haplogroup conflicts: " + countPossibleContaminated);
			System.out.println("Coverage too low for contamination check (<" + requiredCoverage + "x): " + countCovLow);
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

	private double calcMeanHeteroplasmy(Sample currentSample, ArrayList<Polymorphism> found, boolean majorComponent) {

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
	
	private int countHeteroplasmies(Sample currentSample, ArrayList<Polymorphism> found) {

		int count = 0;

		for (Polymorphism split : found) {

			Variant variant = currentSample.getPositions().get(split.toString());

			if (variant != null && variant.getType() == 2) {
				count++;
			}

		}
		return count;
	}

}