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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import contamination.objects.ContaminationEntry;
import contamination.objects.HSDEntry;
import core.Haplogroup;
import genepi.io.table.TableReaderFactory;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class ContaminationChecker {

	public int calcContamination(String inHG2, String variantFile, String outfile, double threshold) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countCovLow = 0;
		int countTooCovLow = 0;

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

					double vaf = readVariants.getDouble(HeaderNames.VariantLevel.colname());
					ID = readVariants.getString(HeaderNames.SampleId.colname());
					String key = ID + "-" + readVariants.getString(HeaderNames.Position.colname()) + readVariants.getString(HeaderNames.VariantBase.colname());
					double value = readVariants.getDouble(HeaderNames.VariantLevel.colname());

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

			//printMap(homoplasmiesMeta);
			
			//printMap(homoplasmies);
			
			printMapDouble(heteroLevels);
			//printMapDouble(coverageMap);

			replaceSpecialCharacter(inHG2);

			CsvTableReader readTableHaploGrep = new CsvTableReader(inHG2, '\t', true);
			NumberFormat formatter = new DecimalFormat("#0.000");

			ArrayList<ContaminationEntry> contArray = new ArrayList<ContaminationEntry>();
			FileWriter fw = new FileWriter(new File(outfile));
			fw.write(
					"SampleID\tContamination\tMajorHG\tMajorLevel\tMajorSNPs\tMajorHGvariants\tMinorHG\tMinorLevel\tMinorSNPs\tMinorHGvariants\tVerifyScore\tmeanCovVar\tHG_Distance");
			fw.write(System.lineSeparator());

			try {
				while (readTableHaploGrep.next()) {
					ContaminationEntry centry = new ContaminationEntry();
					countEntries++;
					String id = readTableHaploGrep.getString("SampleID"); // ID
					double verifyScore = 0;

					double weight = readTableHaploGrep.getDouble("Overall_Rank"); // Rank
					System.out.println("weight " + weight);
					// centry.setMajorId(readTableHaploGrep.getString("weight")); //Major

					centry.setSampleId(id.split("_maj")[0]);
					centry.setMajorId(readTableHaploGrep.getString("Haplogroup")); // Major

					String notfound = readTableHaploGrep.getString("Not_Found_Polys");
					centry.setMajorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
					String majorfound = readTableHaploGrep.getString("Found_Polys");
					double meanMajor = getMeanScores(centry.getSampleId(), majorfound, heteroLevels);
					int[] countHomoplMajor = countHomoplasmies(centry.getSampleId(), majorfound, homoplasmies, homoplasmiesMeta);
					double meanCov = getMeanCoverage(id.split("_maj")[0], coverageMap);

					// check second pair entry
					readTableHaploGrep.next();
					centry.setMinorId(readTableHaploGrep.getString("Haplogroup")); // Minor
					notfound = readTableHaploGrep.getString("Not_Found_Polys");
					centry.setMinorRemaining(notfound.length() - notfound.replaceAll(" ", "").length());
					String minorfound = readTableHaploGrep.getString("Found_Polys");
					double meanMinor = getMeanScores(centry.getSampleId(), minorfound, heteroLevels);
					int[] countHomoplMinor = countHomoplasmies(centry.getSampleId(), minorfound, homoplasmies, homoplasmiesMeta);

					int majMutfound = majorfound.length() - majorfound.replaceAll(" ", "").length();
					int minMutfound = minorfound.length() - minorfound.replaceAll(" ", "").length();

					String homoplMajor = countHomoplMajor[0] + "/" + countHomoplMajor[1];
					String homoplMinor = countHomoplMinor[0] + "/" + countHomoplMinor[1];

					if (meanCov < 200) {
						System.out.println(meanCov);
						countCovLow++;
					}

					int distanceHG = 0;
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
							fw.write(centry.getSampleId() + "\tHG_conflict\t" + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t" + homoplMajor
									+ "\t" + (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor) + "\t"
									+ homoplMinor + "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore + "\t" + meanCov + "\t" + distanceHG
									+ "\n");
						} else if ((minMutfound - countHomoplMinor[0]) > 1) {// (notfound.length()
																				// -
																				// notfound.replaceAll("
																				// ",
																				// "").length()>1){
							countPossibleContaminated++;
							fw.write(centry.getSampleId() + "\tHG_conflict_low\t" + centry.getMajorId() + "\t"

									+ formatter.format(meanMajor) + "\t" + homoplMajor + "\t" + (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId()
									+ "\t" + formatter.format(meanMinor) + "\t" + homoplMinor + "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore
									+ "\t" + meanCov + "\t" + distanceHG + "\n");
						} else if (distanceHG > 1) {
							countPossibleContaminated++;
							fw.write(centry.getSampleId() + "\tHG_conflict_low\t" + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t"
									+ homoplMajor + "\t" + (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor)
									+ "\t" + homoplMinor + "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore + "\t" + meanCov + "\t" + distanceHG
									+ "\n");
						} else { // NONE
							fw.write(centry.getSampleId() + "\tNone\t" + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t" + homoplMajor + "\t"
									+ (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor) + "\t" + homoplMinor
									+ "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore + "\t" + meanCov + "\t" + distanceHG + "\n");
						}

					}

					else if (meanCov < 200) {
						countTooCovLow++;
						fw.write(centry.getSampleId() + "\tLow_Coverage\t" + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t" + homoplMajor
								+ "\t" + (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor) + "\t"
								+ homoplMinor + "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore + "\t" + meanCov + "\t" + distanceHG + "\n");
					} else { // NONE
						fw.write(centry.getSampleId() + "\tNone\t" + centry.getMajorId() + "\t" + formatter.format(meanMajor) + "\t" + homoplMajor + "\t"
								+ (majMutfound - countHomoplMajor[0]) + "\t" + centry.getMinorId() + "\t" + formatter.format(meanMinor) + "\t" + homoplMinor
								+ "\t" + (minMutfound - countHomoplMinor[0]) + "\t" + verifyScore + "\t" + meanCov + "\t" + distanceHG + "\n");

					}
				}
				readTableHaploGrep.close();
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
			System.out.println("Coverage     low  (<200x) : " + countCovLow);

			fw.close();

		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
			return -1;
		}
		// Everything fine
		return 0;
	}

	private double getMean(Vector vecov) {
		int help = 0;
		for (int i = 0; i < vecov.size(); i++) {
			help += Integer.parseInt(vecov.get(i) + "");
		}
		return help / vecov.size();
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
					/*
					 * sum2+=Math.pow(value, 2); stdev = Math.sqrt(i*sum2 - Math.pow(sum1, 2))/i;
					 * System.out.println(stdev);
					 */
					i++;
				}
			}
		}

		if (i > 0) {
			return sum1 / i;
		} else
			return 0;
	}

	public enum HeaderNames {
		SampleId("SampleID"), Position("Pos"), Reference("Ref"), VariantBase("Variant"), VariantLevel("Variant-Level"), Coverage("Coverage-Total");
		private String ColName;

		HeaderNames(String colname) {
			this.ColName = colname;
		}

		public String colname() {
			return ColName;
		}

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

	public void printMap(TreeMap<String, ArrayList<HSDEntry>> map) {
		for (Map.Entry<String, ArrayList<HSDEntry>> entry : map.entrySet()) {
			for (HSDEntry ent : entry.getValue()) {
				System.out.println(ent.getString());
			}
		}
	}

	public void printMap(HashMap<String, Integer> map) {
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}

	public void printMapDouble(HashMap<String, Double> map) {
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
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
		result[0] = common; // Maps.difference(hmap, helpMap).entriesInCommon().size();
		// System.out.println(System.currentTimeMillis()-start);

		if (hmapSize.containsKey(sampleId))
			result[1] = hmapSize.get(sampleId);
		else
			result[1] = 0;
		return result;
	}

}