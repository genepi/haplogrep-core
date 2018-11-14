package contamination;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import contamination.objects.Sample;
import core.SampleFile;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class SimulationTests {

	double[] quality = { 0.5, 0.6, 0.8 };
	int[] high = { 2, 3, 4 };
	int[] low = { 1, 2, 3 };

	//@Test
	/*public void testSimulation() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/simulation/";

		File[] files = new File(folder).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".vcf.gz");
			}
		});

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
						String output = folder + file.getName() + "_" + qual + "_" + h + "_" + l + ".txt";
						Contamination contamination = new Contamination();
						contamination.setSettingHgQuality(qual);
						contamination.setSettingAmountHigh(h);
						contamination.setSettingAmountLow(l);
						contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);
					}
				}
			}
		}
	}*/

}
