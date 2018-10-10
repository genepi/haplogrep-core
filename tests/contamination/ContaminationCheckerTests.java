package contamination;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import contamination.Contamination.Status;
import contamination.objects.Sample;
import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.results.RankedResult;

public class ContaminationCheckerTests {

	@Test
	public void testHighChipMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/high-chip-mix/high-chip-mix-1000G.txt";

		VariantSplitter splitter = new VariantSplitter();

		MutationServerReader reader = new MutationServerReader(variantFile);

		HashMap<String, Sample> mutationServerSamples = reader.parse();

		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		String output = "test-data/contamination/baq-mapQ30/high-chip-mix/report.txt";
		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_High.name())) {
				count++;

			}
		}

		assertEquals(26, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testHighFreeMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/high-free-mix/high-free-mix-1000G.txt";

		Contamination contamination = new Contamination();

		MutationServerReader reader = new MutationServerReader(variantFile);

		HashMap<String, Sample> mutationServerSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		String output = "test-data/contamination/baq-mapQ30/high-free-mix/report.txt";

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_High.name())) {
				count++;

			}
		}

		assertEquals(7, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testNoContamination1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/no-contamination/no-contamination-1000G.txt";

		Contamination contamination = new Contamination();

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationserverSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		String output = "test-data/contamination/baq-mapQ30/no-contamination/report.txt";
		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_High.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testPossibleSwap() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/possible-swap/possible-swap-1000G.txt";

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationserverSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		String output = "test-data/contamination/baq-mapQ30/possible-swap/report.txt";
		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_High.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void test1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/1000G-All-Samples/1000G.txt";

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationserverSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		String out = "test-data/contamination/baq-mapQ30/1000G-All-Samples/report.txt";
		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), out);

		CsvTableReader readerOut = new CsvTableReader(out, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_High.name())) {
				countHigh++;

			}
			if (readerOut.getString("Contamination").equals(Status.HG_Conflict_Low.name())) {
				countLow++;

			}
		}

		// hansi had 120 and 45 but type comes now from mutation server
		assertEquals(126, countHigh);
		assertEquals(33, countLow);

		FileUtil.deleteFile(out);

	}

	public static void createFakeReport(List<TestSample> sampleCollection, File out) throws IOException {

		StringBuffer result = new StringBuffer();

		Collections.sort((List<TestSample>) sampleCollection);

		result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\tNot_Found_Polys\tFound_Polys\tRemaining_Polys\tAAC_In_Remainings\t Input_Sample\n");

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID() + "\t");

				for (RankedResult currentResult : sample.getResults()) {

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i) + ";";
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i) + ";";
						}
					}
					result.append(resultRange);

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					result.append("\t");

					ArrayList<Polymorphism> found = currentResult.getSearchResult().getDetailedResult().getFoundPolys();

					ArrayList<Polymorphism> expected = currentResult.getSearchResult().getDetailedResult().getExpectedPolys();

					Collections.sort(found);

					Collections.sort(expected);

					for (Polymorphism currentPoly : expected) {
						if (!found.contains(currentPoly))
							result.append(" " + currentPoly);
					}

					result.append("\t");

					for (Polymorphism currentPoly : found) {
						result.append(" " + currentPoly);

					}

					result.append("\t");
					ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
					Collections.sort(allChecked);

					for (Polymorphism currentPoly : allChecked) {
						result.append(" " + currentPoly);
					}

					result.append("\t");

					ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
					Collections.sort(aac);

					result.append("\t");

					ArrayList<Polymorphism> input = sample.getSample().getPolymorphisms();

					Collections.sort(input);

					for (Polymorphism currentPoly : input) {
						result.append(" " + currentPoly);
					}
					result.append("\n");

				}
			}
		}

		FileWriter fileWriter = new FileWriter(out);

		fileWriter.write(result.toString().replace("\t ", "\t"));

		fileWriter.close();

	}

}
