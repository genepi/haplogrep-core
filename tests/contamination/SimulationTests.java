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
				return name.toLowerCase().endsWith("5.vcf.gz");
			}
		});

		CsvTableWriter writer = new CsvTableWriter("/home/seb/Desktop/out.txt");
		String[] columns = {"Samples", "Noise", "Sensitivity", "Specificty", "Precision", "QualityFilter", "AmountHighFilter", "AmountLowFilter"};
		writer.setColumns(columns);
	
		for (File file : files) {

			System.out.println("file is " + file.getAbsolutePath());

			VariantSplitter splitter = new VariantSplitter();

			VcfImporter reader = new VcfImporter();

			HashMap<String, Sample> mutationServerSamples = reader.load(new File(file.getPath()), false);

			ArrayList<String> profiles = splitter.split(mutationServerSamples);

			HaplogroupClassifier classifier = new HaplogroupClassifier();
			SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

			for (double qual : quality) {
				for (int h : high) {
					for (int l : low) {
						int noise = Integer.valueOf(file.getName().substring(0, file.getName().length() - 7));
						String output = folder + noise + "_" + qual + "_" + h + "_" + l + ".txt";
						Contamination contamination = new Contamination();
						contamination.setSettingHgQuality(qual);
						contamination.setSettingAmountHigh(h);
						contamination.setSettingAmountLow(l);
						contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

						CsvTableReader readerOut = new CsvTableReader(output, '\t');

						int truePositives = 0;
						int falsePositives = 0;
						int trueNegatives = 0;
						int falseNegative = 0;
						int count = 0;
						while (readerOut.next()) {
							count++;
							String id = readerOut.getString("SampleID");
							String status = readerOut.getString("Contamination");

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

						double sens = (double) truePositives / (truePositives + falseNegative);
						double spec = (double) trueNegatives / (trueNegatives + falsePositives);
						double prec = (double) truePositives / (truePositives + falsePositives);
						writer.setDouble(0, count);
						writer.setInteger(1, noise);
						writer.setDouble(2, sens);
						writer.setDouble(3, spec);
						writer.setDouble(4, prec);
						writer.setDouble(5, qual);
						writer.setInteger(6, h);
						writer.setInteger(7, l);
						writer.next();
					}
				}
			}
		}
		writer.close();
	}

}

