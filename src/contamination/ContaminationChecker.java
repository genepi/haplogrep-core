package contamination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import contamination.objects.ContaminationEntry;
import contamination.objects.Variant;
import contamination.objects.Sample;
import core.Haplogroup;
import genepi.io.table.TableReaderFactory;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class ContaminationChecker {

	// TODO add threshold variable
	public int calcContaminationSeb(HashMap<String, Sample> samples, String inHG2, String out) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countCovLow = 0;

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		CsvTableWriter contaminationWriter = new CsvTableWriter(out, '\t');
		
		String[] columnsWrite = { "SampleID", "Contamination", "MajorHG", "MajorLevel", "MajorSNPs", "MajorHGvariants", "MinorHG", "MinorLevel", "MinorSNPs",
				"MinorHGvariants", "MeanCoverage", "HG_Distance" };
		contaminationWriter.setColumns(columnsWrite);
		
		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			replaceSpecialCharacter(inHG2);

			CsvTableReader haplogrepTable = new CsvTableReader(inHG2, '\t', true);

			ArrayList<ContaminationEntry> contArray = new ArrayList<ContaminationEntry>();

			while (haplogrepTable.next()) {

				ContaminationEntry centry = new ContaminationEntry();

				countEntries++;

				String id = haplogrepTable.getString("SampleID"); // ID

				centry.setSampleId(id.split("_maj")[0]);
				centry.setMajorId(haplogrepTable.getString("Haplogroup")); // Major
				String notfound = haplogrepTable.getString("Not_Found_Polys");
				centry.setMajorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
				String majorfound = haplogrepTable.getString("Found_Polys");

				Sample currentSample = samples.get(centry.getSampleId());

				double meanMajor = getMeanScores(currentSample, majorfound);
				int countMajorHomoplasmies = countHomoplasmies(currentSample, majorfound);
				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();

				double meanCov = currentSample.getTotalCoverage() / currentSample.getAmountVariants();

				// check second pair entry
				haplogrepTable.next();

				centry.setMinorId(haplogrepTable.getString("Haplogroup"));
				notfound = haplogrepTable.getString("Not_Found_Polys");
				centry.setMinorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
				String minorfound = haplogrepTable.getString("Found_Polys");

				double meanMinor = getMeanScores(currentSample, minorfound);

				int countMinorHomoplasmies = countHomoplasmies(currentSample, minorfound);

				int majMutfound = majorfound.length() - majorfound.replaceAll(" ", "").length();
				int minMutfound = minorfound.length() - minorfound.replaceAll(" ", "").length();

				String homoplMajor = countMajorHomoplasmies + "/" + sampleHomoplasmies;
				String homoplMinor = countMinorHomoplasmies + "/" + sampleHomoplasmies;

				int distanceHG = 0;

				String status;

				// check if Haplogroup names are different:
				if (!centry.getMajorId().equals(centry.getMinorId())) {
					contArray.add(centry);

					Haplogroup haplogrMajor = new Haplogroup(centry.getMajorId());
					Haplogroup haplogrMinor = new Haplogroup(centry.getMinorId());

					if (haplogrMajor.isSuperHaplogroup(phylotree, haplogrMinor)) {
						distanceHG = haplogrMajor.distanceToSuperHaplogroup(phylotree, haplogrMinor);
					} else if (haplogrMinor.isSuperHaplogroup(phylotree, haplogrMajor)) {
						distanceHG = haplogrMinor.distanceToSuperHaplogroup(phylotree, haplogrMajor);
					} else {
						distanceHG = haplogrMajor.distanceToSuperHaplogroup(phylotree, haplogrMinor);
					}

					// check if one of the haplogroups is defined by at least 2 heteroplasmic
					// variants and haplogroup with different snps found (distance -1 not related
					// HGs)

					if (((majMutfound - countMajorHomoplasmies) > 2 || (minMutfound - countMinorHomoplasmies) > 2) && (distanceHG > 1 || distanceHG == -1)) {
						countContaminated++;
						status = "HG_conflict";
					} else if (((minMutfound - countMinorHomoplasmies) > 1) || distanceHG > 1) {
						countPossibleContaminated++;
						status = "HG_conflict_low";
					} else {
						status = "None";
					}
				} else if (meanCov < 200) {
					countCovLow++;
					status = "Low_Coverage";
				} else {
					status = "None";
				}

				contaminationWriter.setString(0, centry.getSampleId());
				contaminationWriter.setString(1, status);
				contaminationWriter.setString(2, centry.getMajorId());
				contaminationWriter.setString(3, formatter.format(meanMajor));
				contaminationWriter.setString(4, homoplMajor);
				contaminationWriter.setInteger(5, majMutfound - countMajorHomoplasmies);
				contaminationWriter.setString(6, centry.getMinorId());
				contaminationWriter.setString(7, formatter.format(meanMinor));
				contaminationWriter.setString(8, homoplMinor);
				contaminationWriter.setInteger(9, minMutfound - countMinorHomoplasmies);
				contaminationWriter.setDouble(10, meanCov);
				contaminationWriter.setInteger(11, distanceHG);

				contaminationWriter.next();
			}

			haplogrepTable.close();

			System.out.println("Haplogroup based conflicts: " + countContaminated + " of " + countEntries);
			System.out.println("Minor haplogroup conflicts: " + countPossibleContaminated);
			System.out.println("Coverage low (<200x) : " + countCovLow);

			contaminationWriter.close();

		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
			return -1;
		}
		// Everything fine
		return 0;
	}

	public int calcContamination(String inHG2, String variantFile, String outfile, double threshold) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countCovLow = 0;

		String ID = "";
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		try {

			ITableReader readVariants = TableReaderFactory.getReader(variantFile);

			// hahslevels contains key = sampleid+"-"+pos+variant. e.g.: HG00096-152C
			HashMap<String, Double> heteroLevels = new HashMap<String, Double>();
			HashMap<String, Integer> homoplasmies = new HashMap<String, Integer>();
			HashMap<String, Integer> homoplasmiesMeta = new HashMap<String, Integer>();
			HashMap<String, ArrayList<Integer>> coverageMap = new HashMap<String, ArrayList<Integer>>();

			try {
				while (readVariants.next()) {

					double vaf = readVariants.getDouble("Variant-Level");
					ID = readVariants.getString("SampleID");
					String key = ID + "-" + readVariants.getString("Pos") + readVariants.getString("Variant");
					double value = readVariants.getDouble("Variant-Level");

					int cov = -1;

					if (vaf < 1 - threshold) {
						heteroLevels.put(key, value);
					} else {
						homoplasmies.put(key, 1);

						if (homoplasmiesMeta.containsKey(ID)) {
							homoplasmiesMeta.put(ID, homoplasmiesMeta.get(ID) + 1);
						} else {
							homoplasmiesMeta.put(ID, 1);
						}
					}
					if (coverageMap.get(ID) == null) {
						coverageMap.put(ID, new ArrayList<Integer>());
					}
					coverageMap.get(ID).add(cov);
				}

				readVariants.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			replaceSpecialCharacter(inHG2);

			CsvTableReader haplogrepTable = new CsvTableReader(inHG2, '\t', true);
			NumberFormat formatter = new DecimalFormat("#0.000");

			ArrayList<ContaminationEntry> contArray = new ArrayList<ContaminationEntry>();
			FileWriter fw = new FileWriter(new File(outfile));
			fw.write(
					"SampleID\tContamination\tMajorHG\tMajorLevel\tMajorSNPs\tMajorHGvariants\tMinorHG\tMinorLevel\tMinorSNPs\tMinorHGvariants\tVerifyScore\tmeanCovVar\tHG_Distance");
			fw.write(System.lineSeparator());

			try {
				while (haplogrepTable.next()) {

					ContaminationEntry centry = new ContaminationEntry();

					countEntries++;
					String id = haplogrepTable.getString("SampleID"); // ID

					double weight = haplogrepTable.getDouble("Overall_Rank"); // Rank
					System.out.println("weight " + weight);
					// centry.setMajorId(readTableHaploGrep.getString("weight")); //Major

					centry.setSampleId(id.split("_maj")[0]);
					centry.setMajorId(haplogrepTable.getString("Haplogroup")); // Major

					String notfound = haplogrepTable.getString("Not_Found_Polys");
					centry.setMajorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
					String majorfound = haplogrepTable.getString("Found_Polys");
					double meanMajor = getMeanScores(centry.getSampleId(), majorfound, heteroLevels);
					int[] countHomoplMajor = countHomoplasmies(centry.getSampleId(), majorfound, homoplasmies, homoplasmiesMeta);
					double meanCov = getMeanCoverage(id.split("_maj")[0], coverageMap);

					// check second pair entry
					haplogrepTable.next();
					centry.setMinorId(haplogrepTable.getString("Haplogroup")); // Minor
					notfound = haplogrepTable.getString("Not_Found_Polys");
					centry.setMinorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
					String minorfound = haplogrepTable.getString("Found_Polys");
					double meanMinor = getMeanScores(centry.getSampleId(), minorfound, heteroLevels);
					int[] countHomoplMinor = countHomoplasmies(centry.getSampleId(), minorfound, homoplasmies, homoplasmiesMeta);

					int majMutfound = majorfound.length() - majorfound.replaceAll(" ", "").length();
					int minMutfound = minorfound.length() - minorfound.replaceAll(" ", "").length();

					String homoplMajor = countHomoplMajor[0] + "/" + countHomoplMajor[1];
					String homoplMinor = countHomoplMinor[0] + "/" + countHomoplMinor[1];

					int distanceHG = 0;
					String status;
					// check if Haplogroup names are different:
					if (!centry.getMajorId().equals(centry.getMinorId())) {
						contArray.add(centry);

						Haplogroup haplogrMajor = new Haplogroup(centry.getMajorId());
						Haplogroup haplogrMinor = new Haplogroup(centry.getMinorId());

						if (haplogrMajor.isSuperHaplogroup(phylotree, haplogrMinor)) {
							distanceHG = haplogrMajor.distanceToSuperHaplogroup(phylotree, haplogrMinor);
						} else if (haplogrMinor.isSuperHaplogroup(phylotree, haplogrMajor)) {
							distanceHG = haplogrMinor.distanceToSuperHaplogroup(phylotree, haplogrMajor);
						} else {
							distanceHG = haplogrMajor.distanceToSuperHaplogroup(phylotree, haplogrMinor);
						}

						// check if one of the haplogroups is defined by at least 2 heteroplasmic
						// variants and haplogroup with different snps found (distance -1 not related
						// HGs)

						if (((majMutfound - countHomoplMajor[0]) > 2 || (minMutfound - countHomoplMinor[0]) > 2) && (countHomoplMajor[1] == countHomoplMinor[1])
								&& (distanceHG > 1 || distanceHG == -1)) {
							countContaminated++;
							status = "\tHG_conflict\t";
						} else if (((minMutfound - countHomoplMinor[0]) > 1) || distanceHG > 1) {
							countPossibleContaminated++;
							status = "\tHG_conflict_low\t";
						} else {
							status = "\tNone\t";
						}
					} else if (meanCov < 200) {
						countCovLow++;
						status = "\tLow_Coverage\t";
					} else {
						status = "\tNone\t";
					}

					fw.write(centry.getSampleId() + status + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t" + homoplMajor + "\t"
							+ (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor) + "\t" + homoplMinor + "\t"
							+ (minMutfound - countHomoplMinor[0]) + "\t" + meanCov + "\t" + distanceHG + "\n");
				}

				haplogrepTable.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (countEntries == 1) {
				System.out.println("Sample: " + ID);
				// System.out.println("Mean Variant Coverage: " + getMean(vecov));
			} else {
				System.out.println("Samples: " + countEntries);
			}

			System.out.println("Haplogroup based conflicts: " + countContaminated + " of " + countEntries);
			System.out.println("Minor haplogroup conflicts: " + countPossibleContaminated);
			System.out.println("Coverage low (<200x) : " + countCovLow);

			fw.close();

		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
			return -1;
		}
		// Everything fine
		return 0;
	}

	private double getMeanScores(Sample currentSample, String found) {

		double sum = 0.0;
		int count = 0;

		String[] splitsFound = found.split(" ");

		for (String split : splitsFound) {

			Variant variant = currentSample.getPositions().get(split);

			if (variant != null && variant.getType() == 2) {
				sum += variant.getLevel();
				count++;
			}

		}
		return sum / count;
	}

	private double getMeanScores(String sampleId, String found, HashMap<String, Double> hmap) {

		double sum1 = 0;
		double sum2 = 0;
		double stdev = 0;

		int i = 0;

		StringTokenizer st = new StringTokenizer(found, " ");
		while (st.hasMoreTokens()) {
			String variant = st.nextToken();

			if (hmap.containsKey(sampleId + "-" + variant)) {

				double value = hmap.get(sampleId + "-" + variant);
				if (value < 0.99) {
					sum1 += value;
					i++;
				}
			}
		}

		if (i > 0) {
			return sum1 / i;
		} else
			return 0;
	}

	private double getMeanCoverage(String sampleId, HashMap<String, ArrayList<Integer>> covMap) {
		ArrayList<Integer> entries = covMap.get(sampleId);
		int sum = 0;

		for (int i = 0; i < entries.size(); i++) {
			sum += entries.get(i);
		}

		return sum / entries.size();
	}

	/**
	 * From
	 * https://stackoverflow.com/questions/3940997/files-java-replacing-characters
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void replaceSpecialCharacter(String file) {

		File tempFile = new File(file);
		try {
			tempFile = File.createTempFile("buffer", ".tmp");

			FileWriter fw = new FileWriter(tempFile);

			Reader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			while (br.ready()) {
				fw.write(br.readLine().replaceAll("\"", "''") + "\n");
			}

			fw.close();
			br.close();
			fr.close();

			tempFile.renameTo(new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int countHomoplasmies(Sample currentSample, String majorfound) {

		int count = 0;
		String[] majorSplits = majorfound.split(" ");

		for (String split : majorSplits) {

			Variant variant = currentSample.getPositions().get(split);

			if (variant != null && variant.getType() == 1) {
				count++;
			}

		}
		return count;
	}

	private int[] countHomoplasmies(String sampleId, String found, HashMap<String, Integer> hmap, HashMap<String, Integer> hmapSize) {

		int[] result = new int[2]; // 0 = homoplasmies in haplogroup found
		// 1 = all homoplasmies in this sample

		if (hmapSize.size() == 0) {
			result[0] = 0;
			result[1] = 0;
			return result;
		}
		HashMap<String, Integer> helpMap = new HashMap<>();

		StringTokenizer st = new StringTokenizer(found, " ");
		int common = 0;
		while (st.hasMoreTokens()) {
			String variant = st.nextToken();
			String key = sampleId + "-" + variant;
			helpMap.put(key, 1);
			if (hmap.containsKey(key))
				common++;
		}
		long start = System.currentTimeMillis();
		result[0] = common;

		if (hmapSize.containsKey(sampleId))
			result[1] = hmapSize.get(sampleId);
		else
			result[1] = 0;
		return result;
	}

}