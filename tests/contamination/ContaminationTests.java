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

public class ContaminationTests {

	@Test
	public void testHighChipMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-chip-mix/";
		String variantFile = folder + "high-chip-mix-1000G.vcf";
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

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(26, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testHighFreeMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-free-mix/";
		String variantFile = folder + "high-free-mix-1000G.vcf";
		String output = folder + "chip-mix-report.txt";

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		Contamination contamination = new Contamination();
		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(7, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testNoContamination1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/no-contamination/";
		String variantFile = folder + "no-contamination-1000G.vcf";
		String output = folder + "no-contamination-report.txt";

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationserverSamples = reader.load(new File(variantFile), false);

		Contamination contamination = new Contamination();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationserverSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testPossibleSwap() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/possible-swap/";
		String variantFile = folder + "possible-swap-1000G.vcf";
		String output = folder + "possible-swap-report.txt";

		VcfImporter reader = new VcfImporter();
		HashMap<String, Sample> mutationserverSamples = reader.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationserverSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000G_BAQ.vcf";
		String out = folder + "1000g-report.txt";

		VcfImporter reader2 = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader2.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), out);

		CsvTableReader readerOut = new CsvTableReader(out, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				countHigh++;
			}

			if (readerOut.getString("Contamination").equals(Status.LOW.name())) {
				countLow++;
			}
		}

		FileUtil.deleteFile(out);

		assertEquals(121, countHigh);
		assertEquals(28, countLow);

	}

	@Test
	public void testNoBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000G_NOBAQ.vcf";
		String out = folder + "1000G_NOBAQ_report.txt";
		
		VcfImporter reader2 = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader2.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), out);

		CsvTableReader readerOut = new CsvTableReader(out, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				countHigh++;
			}

			if (readerOut.getString("Contamination").equals(Status.LOW.name())) {
				countLow++;
			}
		}

		FileUtil.deleteFile(out);

		assertEquals(116, countHigh);
		assertEquals(28, countLow);

	}

}
