package contamination;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

	private int settingAmountHigh = 3;
	private int settingAmountLow = 2;
	private double settingHgQuality = 0.5;

	public int detect(HashMap<String, Sample> mutationSamples, ArrayList<TestSample> haplogrepSamples, String out) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countNone = 0;

		Collections.sort((List<TestSample>) haplogrepSamples);

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		CsvTableWriter contaminationWriter = new CsvTableWriter(out, '\t');

		String[] columnsWrite = { "SampleID", "Contamination", "SampleHomoplasmies", "SampleHeteroplasmies", "SampleMeanCoverage", "HgMajor", "HgQualityMajor",
				"HgMinor", "HgQualityMinor", "HomoplasmiesMajor", "HomoplasmiesMinor", "HeteroplasmiesMajor", "HeteroplasmiesMinor", "MeanHetLevelMajor",
				"MeanHetLevelMinor", "HG_Distance", "DiffSnpsMajorMinor", "DiffSnpsMinorMajor", "HeteroplasmyLevelTotal" };
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

				int meanCoverageSample = (int) currentSample.getSumCoverage() / currentSample.getAmountVariants();
				double meanHetLevelSample = currentSample.getSumHeteroplasmyLevel() / currentSample.getAmountHeteroplasmies();

				centry.setMajorHg(majorSample.getTopResult().getHaplogroup().toString());
				centry.setMajorNotFound(notFoundMajor);

				centry.setMinorHg(minorSample.getTopResult().getHaplogroup().toString());
				centry.setMinorNotFound(notFoundMinor);

				int homoplasmiesMajor = countHomoplasmies(currentSample, foundMajor);
				int homoplasmiesMinor = countHomoplasmies(currentSample, foundMinor);

				int heteroplasmiesMajor = countHeteroplasmiesMajor(currentSample, foundMajor);
				int heteroplasmiesMinor = countHeteroplasmiesMinor(currentSample, foundMinor);

				double meanHeteroplasmyMajor = calcMeanHeteroplasmy(currentSample, foundMajor, true);
				double meanHeteroplasmyMinor = calcMeanHeteroplasmy(currentSample, foundMinor, false);

				ArrayList<Polymorphism> diffMajorMinor = calculateHaplogroupDifference(expectedMajor, expectedMinor);
				ArrayList<Polymorphism> diffMinorMajor = calculateHaplogroupDifference(expectedMinor, expectedMajor);

				if (!centry.getMajorHg().equals(centry.getMinorHg())) {

					distanceHG = calcDistance(centry, phylotree);

					if ((heteroplasmiesMajor >= settingAmountHigh || heteroplasmiesMinor >= settingAmountHigh) && distanceHG >= settingAmountHigh
							&& hgQualityMajor > settingHgQuality && hgQualityMinor > settingHgQuality) {
						countContaminated++;
						status = Status.HIGH;
						// TODO check mutation rate if heteroplasmies > 5
					} /*
						 * else if ((heteroplasmiesMinor >= settingAmountLow || distanceHG >=
						 * settingAmountLow) && hgQualityMajor > settingHgQuality && hgQualityMinor >
						 * settingHgQuality) { countPossibleContaminated++; status = Status.LOW; }
						 */ else {
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
				contaminationWriter.setInteger(4, meanCoverageSample);
				contaminationWriter.setString(5, centry.getMajorHg());
				contaminationWriter.setString(6, formatter.format(hgQualityMajor));
				contaminationWriter.setString(7, centry.getMinorHg());
				contaminationWriter.setString(8, formatter.format(hgQualityMinor));
				contaminationWriter.setInteger(9, homoplasmiesMajor);
				contaminationWriter.setInteger(10, homoplasmiesMinor);
				contaminationWriter.setInteger(11, heteroplasmiesMajor);
				contaminationWriter.setInteger(12, heteroplasmiesMinor);
				contaminationWriter.setString(13, formatter.format(meanHeteroplasmyMajor));
				contaminationWriter.setString(14, formatter.format(meanHeteroplasmyMinor));
				contaminationWriter.setInteger(15, distanceHG);
				contaminationWriter.setInteger(16, diffMajorMinor.size());
				contaminationWriter.setInteger(17, diffMinorMajor.size());
				contaminationWriter.setString(18, formatter.format(meanHeteroplasmyMajor + meanHeteroplasmyMinor));
				contaminationWriter.next();
			}

			System.out.println("Total amount of samples:" + "\t" + countEntries);
			System.out.println(out + "\t" + countEntries + "\t" + countContaminated + "\t" + countPossibleContaminated + "\t" + countNone);
			contaminationWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	private int calcDistance(ContaminationEntry centry, Phylotree phylotree) {

		Haplogroup hgMajor = new Haplogroup(centry.getMajorHg());

		Haplogroup hgMinor = new Haplogroup(centry.getMinorHg());

		return phylotree.getDistanceBetweenHaplogroups(hgMajor, hgMinor);
	}

	private int calcDistanceOld(ContaminationEntry centry, Phylotree phylotree) {

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

	private ArrayList<Polymorphism> calculateHaplogroupDifference(ArrayList<Polymorphism> list1, ArrayList<Polymorphism> list2) {

		ArrayList<Polymorphism> newList = new ArrayList<Polymorphism>(list1);

		newList.removeAll(list2);

		return newList;
	}

	private double calcMeanHeteroplasmy(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep, boolean majorComponent) {

		double sum = 0.0;
		double count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());
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

	private int countHomoplasmies(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {

		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && (variant.getType() == 1 || variant.getType() == 4)) {
				count++;
			}

		}
		return count;
	}

	private int countHeteroplasmiesMajor(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {
		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && variant.getType() == 2 && (variant.getRef() != variant.getMajor())) {
				count++;
			}

		}
		return count;
	}

	private int countHeteroplasmiesMinor(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {
		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && variant.getType() == 2 && (variant.getRef() != variant.getMinor())) {
				count++;
			}

		}
		return count;
	}

	public int getSettingAmountHigh() {
		return settingAmountHigh;
	}

	public void setSettingAmountHigh(int settingAmountHigh) {
		this.settingAmountHigh = settingAmountHigh;
	}

	public int getSettingAmountLow() {
		return settingAmountLow;
	}

	public void setSettingAmountLow(int settingAmountLow) {
		this.settingAmountLow = settingAmountLow;
	}

	public double getSettingHgQuality() {
		return settingHgQuality;
	}

	public void setSettingHgQuality(double settingHgQuality) {
		this.settingHgQuality = settingHgQuality;
	}

	public static String readInReference(String file) {
		StringBuilder stringBuilder = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				if (!line.startsWith(">"))
					stringBuilder.append(line);

			}

			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}

}