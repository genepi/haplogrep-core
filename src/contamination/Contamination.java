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
		HIGH, LOW, NONE;
	}

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

		String[] columnsWrite = { "SampleID", "Contamination", "SampleHomoplasmies", "SampleHeteroplasmies","SampleMeanHeteroplasmyLevel", "SampleMeanCoverage", "MajorHG", "MajorHGQuality", "MajorHomoplasmies",
				"MajorHeteroplasmies", "MajorMeanHetLevel", "MinorHG", "MinorHGQuality", "MinorHomoplasmies", "MinorHeteroplasmies", "MinorMeanHetLevel","HG_Distance" };
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
				double hgQualityMajor = majorSample.getTopResult().getDistance();
				double hgQualityMinor = minorSample.getTopResult().getDistance();

				Sample currentSample = mutationSamples.get(centry.getSampleId());

				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();
				int sampleHeteroplasmies = currentSample.getAmountHeteroplasmies();

				double meanCoverageSample = currentSample.getTotalCoverage() / currentSample.getAmountVariants();
				double meanHetLevelSample = currentSample.getCountHeteroplasmyLevel() / currentSample.getAmountHeteroplasmies();

				centry.setMajorHg(majorSample.getTopResult().getHaplogroup().toString());
				centry.setMajorNotFound(notFoundMajor);

				centry.setMinorHg(minorSample.getTopResult().getHaplogroup().toString());
				centry.setMinorNotFound(notFoundMinor);

				int homoplasmiesMajor = countHomoplasmies(currentSample, foundMajor);
				int homoplasmiesMinor = countHomoplasmies(currentSample, foundMinor);

				// TODO talk to hansi: foundMinor from Haplogrep also include back mutations,
				// gives a wrong result back!
				// int heteroplasmiesMajor = foundMajor.size() - homoplasmiesMajor;
				// int heteroplasmiesMinor = foundMinor.size() - homoplasmiesMinor;

				int heteroplasmiesMajor = countHeteroplasmies(currentSample, foundMajor);
				int heteroplasmiesMinor = countHeteroplasmies(currentSample, foundMinor);

				double meanHeteroplasmyMajor = calcMeanHeteroplasmy(currentSample, foundMajor, true);
				double meanHeteroplasmyMinor = calcMeanHeteroplasmy(currentSample, foundMinor, false);

				if (!centry.getMajorHg().equals(centry.getMinorHg())) {

					distanceHG = calcDistance(centry, phylotree);

					if ((heteroplasmiesMajor > 2 || heteroplasmiesMinor > 2) && (distanceHG > 1 || distanceHG == -1)) {
						countContaminated++;
						status = Status.HIGH;
					} else if ((heteroplasmiesMinor > 1) || distanceHG > 1) {
						countPossibleContaminated++;
						status = Status.LOW;
					} else {
						countNone++;
						status = Status.NONE;
					}
				} else {
					countNone++;
					status = Status.NONE;
				}

				contaminationWriter.setString(0, centry.getSampleId());
				contaminationWriter.setString(1, status.toString());
				contaminationWriter.setInteger(2, sampleHomoplasmies);
				contaminationWriter.setInteger(3, sampleHeteroplasmies);
				contaminationWriter.setString(4, formatter.format(meanHetLevelSample));
				contaminationWriter.setDouble(5, meanCoverageSample);
				contaminationWriter.setString(6, centry.getMajorHg());
				contaminationWriter.setString(7, formatter.format(hgQualityMajor));
				contaminationWriter.setInteger(8, homoplasmiesMajor);
				contaminationWriter.setInteger(9, heteroplasmiesMajor);
				contaminationWriter.setString(10, formatter.format(meanHeteroplasmyMajor));
				contaminationWriter.setString(11, centry.getMinorHg());
				contaminationWriter.setString(12, formatter.format(hgQualityMinor));
				contaminationWriter.setInteger(13, homoplasmiesMinor);
				contaminationWriter.setInteger(14, heteroplasmiesMinor);
				contaminationWriter.setString(15, formatter.format(meanHeteroplasmyMinor));
				contaminationWriter.setInteger(16, distanceHG);
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

		Haplogroup hgMajor = new Haplogroup(centry.getMajorHg());

		Haplogroup hgMinor = new Haplogroup(centry.getMinorHg());

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