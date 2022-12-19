package classify;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Polymorphism;
import core.Reference;
import core.SampleFile;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;
import importer.FastaImporter;
import importer.HsdImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;

public class ClassifyHaplogroupsTest {

	private static Phylotree phylotree = null;

	private static Reference reference = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException {

		reference = new Reference("test-data/reference/rcrs/rCRS.fasta");
		
		HashSet<String> hotspots = new HashSet<>(Arrays.asList("315.1C", "309.1C", "309.1CC", "523d","524d","524.1AC","524.1ACAC","3107d","16182C","16183C","16193.1C","16193.1CC","16519C"));

		phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", reference, hotspots);

	}

	@Test
	public void exportTophit() throws Exception {
		String file = "test-data/hsd/h100.hsd";
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");

		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples, ref);

		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		newSampleFile.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(newSampleFile.getTestSamples(), ref, "test.txt", true);

		assertEquals(1, countLines("test.txt") - 1);

	}

	@Test
	public void exportSeveralHits() throws Exception {
		String file = "test-data/hsd/h100.hsd";
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");

		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples, ref);

		// create top 10 hits
		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		newSampleFile.updateClassificationResults(phylotree, newRanker);

		// export top 5 hits
		ExportUtils.createReport(newSampleFile.getTestSamples(), ref, "test.txt", true, 5);

		assertEquals(5, countLines("test.txt") - 1);

	}

	public static long countLines(String fileName) {

		long lines = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			while (reader.readLine() != null) {
				lines++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;

	}

	@Test
	public void testNomenclatureRules() throws Exception {

		String file = "test-data/fasta/InsertionTest3.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		// create top 10 hits
		SampleFile samplesFasta = new SampleFile(samples,ref);
		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		samplesFasta.updateClassificationResults(phylotree, newRanker);
		TestSample a = samplesFasta.getTestSamples().get(0);

		HashSet<String> set = new HashSet<String>();

		for (Polymorphism input : a.getSample().getPolymorphisms()) {
			set.add(input.toString());
		}
		Assert.assertTrue(set.contains("309.1CCT"));
		Assert.assertFalse(set.contains("309.1C"));
		Assert.assertFalse(set.contains("309.2C"));
		Assert.assertFalse(set.contains("315.1C"));

		//set rules for fasta
		samplesFasta.applyNomenclatureRules(phylotree,"test-data/rules/rules.csv");

		set = new HashSet<String>();
		for (Polymorphism input : a.getSample().getPolymorphisms()) {
			set.add(input.toString());
		}

		Assert.assertFalse(set.contains("309.1CCT"));
		Assert.assertTrue(set.contains("309.1C"));
		Assert.assertTrue(set.contains("309.2C"));
		Assert.assertTrue(set.contains("315.1C"));

	}
	
	@Test
	public void testH100MaskedFirstPart() throws Exception {
		String file = "test-data/fasta/H100_masked_0_8500.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		String[] splits = samples.get(0).split("\t");
		
		assertEquals("8500-16569;", splits[1]);

		// for later
		SampleFile samplesFasta = new SampleFile(samples,ref);
		KulczynskiRanking newRanker = new KulczynskiRanking(1);
		samplesFasta.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(samplesFasta.getTestSamples(), ref, "test.txt", true, 1);

	}
	
	@Test
	public void testH100MaskedSecondPart() throws Exception {
		String file = "test-data/fasta/H100_masked_8500_16569.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		String[] splits = samples.get(0).split("\t");
		assertEquals("1-8499;", splits[1]);
		
		// for later
		SampleFile samplesFasta = new SampleFile(samples,ref);
		KulczynskiRanking newRanker = new KulczynskiRanking(1);
		samplesFasta.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(samplesFasta.getTestSamples(), ref, "test.txt", true, 1);

	}
	
	@Test
	public void testH100MaskedMiddlePart() throws Exception {
		String file = "test-data/fasta/H100_masked_3000_13000.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		String[] splits = samples.get(0).split("\t");

		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}
		
		assertEquals("13000-16569;1-2999;", splits[1]);

		// for later
		SampleFile samplesFasta = new SampleFile(samples,ref);
		KulczynskiRanking newRanker = new KulczynskiRanking(1);
		samplesFasta.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(samplesFasta.getTestSamples(), ref, "test.txt", true, 1);

	}
	
	@Test
	public void testH1003Times() throws Exception {
		String file = "test-data/fasta/H100_3times.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		String[] splits = samples.get(0).split("\t");

		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}
		
		assertEquals("1-16569;", splits[1]);

		// for later
		SampleFile samplesFasta = new SampleFile(samples,ref);
		KulczynskiRanking newRanker = new KulczynskiRanking(1);
		samplesFasta.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(samplesFasta.getTestSamples(), ref, "test.txt", true, 1);

	}
	

}
