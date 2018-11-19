package contamination;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class SimulationTests {

	double[] quality = { 0.5, 0.6, 0.7, 0.8 };
	int[] high = { 2, 3, 4, 5 };
	int[] low = { 1, 2, 3, 4 };

	@Test
	public void testSimulation() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/simulation/";

		File[] files = new File(folder).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".vcf.gz");
			}
		});

		if (files.length > 0) {
			for (File file : files) {

				CsvTableWriter writer = new CsvTableWriter("/home/seb/Desktop/" + file.getName() + ".txt");

				String[] columns = { "Setup", "Samples", "Noise", "AverageDistance", "Sensitivity", "Specificty", "Precision", "QualityFilter",
						"AmountHighFilter", "AmountLowFilter" };

				writer.setColumns(columns);

				System.out.println("file is " + file.getAbsolutePath());

				VariantSplitter splitter = new VariantSplitter();

				VcfImporter reader = new VcfImporter();

				HashMap<String, Sample> mutationServerSamples = reader.load(new File(file.getPath()), false);

				ArrayList<String> profiles = splitter.split(mutationServerSamples);

				HaplogroupClassifier classifier = new HaplogroupClassifier();
				SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);
				int setup = 0;
				// for (double qual = 0.5; qual <= 0.5; qual += 0.05) {
				for (int h = 10; h < 15; h++) {
					for (int w = 0; w <= 5; w++) {
						setup++;
						int noise = Integer.valueOf(file.getName().substring(0, file.getName().length() - 7));
						String output = folder + noise + "_0.5" + "_" + h + "_" + w + ".txt";
						Contamination contamination = new Contamination();
						contamination.setSettingHgQuality(0.5);
						contamination.setSettingAmountHigh(h);
						contamination.setSettingAmountLow((h - w) < 0 ? 0 : (h - w));
						contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

						CsvTableReader readerOut = new CsvTableReader(output, '\t');

						int truePositives = 0;
						int falsePositives = 0;
						int trueNegatives = 0;
						int falseNegative = 0;
						int count = 0;
						int distance = 0;
						int countCont = 0;
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
								countCont++;
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
						// new File(output).delete();

						double sens = 0.0;
						double spec = 0.0;
						double prec = 0.0;

						if ((truePositives  + falseNegative) > 0) {
							sens = (double) truePositives / (truePositives + falseNegative);
						}
						if ((trueNegatives + falsePositives) > 0) {
							spec = (double) trueNegatives / (trueNegatives + falsePositives);
						}
						if ((truePositives  + falsePositives) > 0) {
							prec = (double) truePositives / (truePositives + falsePositives);
						}
						double averageDistance = (double) distance / count;

						System.out.println("falsePositives " + falsePositives);
						System.out.println("sens " + sens);
						System.out.println("spec " + spec);
						System.out.println("prec " + prec);

						writer.setDouble(0, setup);
						writer.setDouble(1, count);
						writer.setInteger(2, noise);
						writer.setDouble(3, averageDistance);
						writer.setDouble(4, sens);
						writer.setDouble(5, spec);
						writer.setDouble(6, prec);
						writer.setDouble(7, 0.5);
						writer.setInteger(8, h);
						writer.setInteger(9, w);
						writer.next();
					}
				}
				// }
				writer.close();
			}

		}
	}

}
