package contamination;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;
import contamination.Contamination.Status;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class SimulationTests {

	double[] quality = {0.5,0.6,0.8};
	int[] high = {2,3,4};
	int[] low = {1,2,3};
	
	@Test
	public void testSimulationNoContamination1() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/simulation/";
		String variantFile = folder + "nocont_noise0.vcf";

		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		for (double qual : quality) {
			for (int h : high) {
				for (int l : low) {
					String output = folder + "nocont_noise0_" + qual + "_" + h + "_" + l+".txt";
					Contamination contamination = new Contamination();
					contamination.setSettingHgQuality(qual);
					contamination.setSettingAmountHigh(h);
					contamination.setSettingAmountLow(l);
					contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);
				}
			}
		}

	}
	
	@Test
	public void testSimulationNoContamination2() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/simulation/";
		String variantFile = folder + "nocont_noise5.vcf";

		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		for (double qual : quality) {
			for (int h : high) {
				for (int l : low) {
					String output = folder + "nocont_noise5_" + qual + "_" + h + "_" + l+".txt";
					Contamination contamination = new Contamination();
					contamination.setSettingHgQuality(qual);
					contamination.setSettingAmountHigh(h);
					contamination.setSettingAmountLow(l);
					contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);
				}
			}
		}

	}
	
}
