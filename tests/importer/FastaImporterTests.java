package importer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import core.SampleFile;
import core.TestSample;
import exceptions.parse.HsdFileException;
import importer.FastaImporter;
import importer.FastaImporter.References;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;

public class FastaImporterTests {

	@Test
	public void testRcrs() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());
	}

	@Test
	public void testRsrs() throws Exception {
		String file = "test-data/fasta/rsrs.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());

	}

	@Test
	public void testRCrsWithRsrsReference() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		// exactly 52 differences between rsrs and rCRS
		assertEquals(52, (splits.length) - 3);

	}

	@Test
	public void testParseSampleWithDeletions() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");

		boolean deletion = false;
		for (int i = 3; i < splits.length; i++) {
			if (splits[i].equals("523-524d")) {
				deletion = true;
			}
			actual.append(splits[i] + ",");
		}

		assertEquals(true, deletion);

	}

	@Test
	public void testParseSampleWithInsertionsDeletions() throws Exception {
		String file = "test-data/fasta/InsertionTest.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));
	}

	// copied first two lines of fasta (including 309.1C etc to end of line)
	@Test
	public void testParseSampleWithInsertionsDeletionsShuffle() throws Exception {
		String file = "test-data/fasta/InsertionTest2.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));

	}

	// random shuffle
	@Test
	public void testParseSampleWithInsertionsDeletionsShuffleRandom() throws Exception {
		String file = "test-data/fasta/InsertionTest3.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));

	}

	@Test
	public void testFastaExportImportInterface() throws Exception {
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		FastaImporter impFasta = new FastaImporter();
		
		String tempFile = "test-data/tmp.fasta";
		
		// read in file
		String file = "test-data/fasta/H100.fasta";
		ArrayList<String> lines = impFasta.load(new File(file), References.RCRS);
		
		SampleFile samples = new SampleFile(lines);
		
		//classify
		runClassification(samples);
 
		String[] splits = lines.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set1.add(splits[i]);
		}

		ExportUtils.generateFasta(samples.getTestSamples(), tempFile);
		
		// read in export file
		lines = impFasta.load(new File(tempFile), References.RCRS);
		samples = new SampleFile(lines);
		splits = lines.get(0).split("\t");
		
		for (int i = 3; i < splits.length; i++) {
			set2.add(splits[i]);
		}

		runClassification(samples);
		ExportUtils.generateFasta(samples.getTestSamples(), "test-data/tmp2.fasta");

		assertEquals(set1, set2);

		FileUtils.delete(new File("test-data/tmp.fasta"));
		FileUtils.delete(new File("test-data/tmp2.fasta"));

	}

	public static void runClassification(SampleFile newSampleFile) throws HsdFileException {
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		KulczynskiRanking newRanker = new KulczynskiRanking(1);
		newSampleFile.updateClassificationResults(phylotree, newRanker);
	}

}
