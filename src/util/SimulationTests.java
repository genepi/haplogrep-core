package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import contamination.ContaminationDetection;
import contamination.HaplogroupClassifier;
import contamination.VariantSplitter;
import contamination.objects.ContaminationObject;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class SimulationTests {

	public static void testSimulation() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "/home/seb/Desktop/simulation/input";

		File[] folders = new File(folder).listFiles();
		
		Arrays.sort(folders);

		System.out.println("Found sub folders " + folders.length);

		for (File topFolder : folders) {

			File[] files = topFolder.listFiles();
			
			String[] metrics = { "kulczynski", "hamming", "jaccard" };

			for (String metric : metrics) {

				CsvTableWriter writer = new CsvTableWriter(
						"/home/seb/Desktop/simulation/output/" + "simulation_" + topFolder.getName() + "_" + metric + ".txt");

				String[] columns = { "Setup", "Samples", "MeanDistance", "Sensitivity", "Specificty", "Precision", "HaplogrepFilter", "HetFilter", "Group", "TP","TN","FP","FN"};

				writer.setColumns(columns);

					for (File file : files) {

						System.out.println("file is " + file.getAbsolutePath());
						long start = System.currentTimeMillis();
						VariantSplitter splitter = new VariantSplitter();

						VcfImporter reader = new VcfImporter();

						HashMap<String, Sample> mutationServerSamples = reader.load(new File(file.getPath()), false);

						System.out.println("load " + (System.currentTimeMillis() - start) / 1000);
						start = System.currentTimeMillis();

						ArrayList<String> profiles = splitter.split(mutationServerSamples);

						System.out.println("split " + (System.currentTimeMillis() - start) / 1000);
						start = System.currentTimeMillis();

						HaplogroupClassifier classifier = new HaplogroupClassifier();
						SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles, metric);

						System.out.println("classify " + (System.currentTimeMillis() - start) / 1000);
						start = System.currentTimeMillis();

						int setup = 0;
						for (double qual = 0.5; qual <= 1.0; qual += 0.1) {
							for (int h = 0; h <= 20; h++) {
								// for (int w = 0; w <= 10; w++) {
								setup++;
								int noise = Integer.valueOf(file.getName().substring(0, file.getName().length() - 7));
								String output = folder + noise + "_" + qual + "_" + h + ".txt";
								ContaminationDetection contamination = new ContaminationDetection();
								contamination.setSettingHgQuality(qual);
								contamination.setSettingAmountHigh(h);
								// contamination.setSettingAmountLow((h - w) < 0 ? 0 : (h - w));
								contamination.setSettingAmountLow(0);
								
								ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
								
								contamination.writeFile(list, output);

								CsvTableReader readerOut = new CsvTableReader(output, '\t');

								int truePositives = 0;
								int falsePositives = 0;
								int trueNegatives = 0;
								int falseNegative = 0;
								int count = 0;
								int distance = 0;
								while (readerOut.next()) {
									count++;
									String id = readerOut.getString("SampleID");
									String status = readerOut.getString("Contamination");
									distance += readerOut.getInteger("HG_Distance");

									String[] idSplits = id.split("_");
									boolean cont;
									if (idSplits[0].equals(idSplits[1])) {
										cont = false;
									} else {
										cont = true;
									}

									if ((status.equals("HIGH") || status.equals("LOW")) && cont) {
										truePositives++;
									} else if (status.equals("NONE") && !cont) {
										trueNegatives++;
									} else if ((status.equals("HIGH") || status.equals("LOW")) && !cont) {
										falsePositives++;
									} else if (status.equals("NONE") && cont) {
										falseNegative++;
									}

								}
								new File(output).delete();

								double sens = 0.0;
								double spec = 0.0;
								double prec = 0.0;

								if ((truePositives + falseNegative) > 0) {
									sens = (double) truePositives / (truePositives + falseNegative);
								}
								if ((trueNegatives + falsePositives) > 0) {
									spec = (double) trueNegatives / (trueNegatives + falsePositives);
								}
								if ((truePositives + falsePositives) > 0) {
									prec = (double) truePositives / (truePositives + falsePositives);
								}
								double averageDistance = (double) distance / count;

								writer.setDouble(0, setup);
								writer.setDouble(1, count);
								writer.setDouble(2, averageDistance);
								writer.setDouble(3, sens);
								writer.setDouble(4, spec);
								writer.setDouble(5, prec);
								writer.setDouble(6, qual);
								writer.setInteger(7, h);
								writer.setString(8, file.getName().substring(0, file.getName().indexOf(".")));
								writer.setInteger(9, truePositives);
								writer.setInteger(10, trueNegatives);
								writer.setInteger(11, falsePositives);
								writer.setInteger(12, falseNegative);
								writer.next();
								// }
							}
						}
					}
					writer.close();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			testSimulation();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
