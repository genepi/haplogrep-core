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

	@Test
	public void testSimulation() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/simulation/";
		String variantFile = folder + "simulation.vcf";
		String output = folder + "chip-mix-report.txt";

		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				countHigh++;

			} else if (readerOut.getString("Contamination").equals(Status.LOW.name())) {
				countLow++;

			}
		}

	}
}
