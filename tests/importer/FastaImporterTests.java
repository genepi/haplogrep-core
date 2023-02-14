package importer;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import core.Polymorphism;
import core.Reference;
import core.SampleFile;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import qualityAssurance.rules.CheckForTooManyN;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;

public class FastaImporterTests {

	@Test
	public void testRcrs() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";

		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());
		
		// check length of polymorphisms array
		SampleFile sampleFile = new SampleFile(samples, ref);
		assertEquals(0, sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().size());
	}

	@Test
	public void testRsrs() throws Exception {
		String file = "test-data/fasta/rsrs.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
	public void testParseSampleWithNRanges() throws Exception {
		String file = "test-data/fasta/test3.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		String range = splits[1];
		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("8271-8279d"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("470R"));
		assertEquals(true, set.contains("499R"));
		assertEquals(true, set.contains("5068Y"));

	}

	@Test
	public void testNomenclatureRules() throws Exception {
		String file = "test-data/fasta/KF450918.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		assertEquals(1, samples.size());

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		SampleFile sampleFile = new SampleFile(samples, ref);

		HashSet<String> hotspots = new HashSet<>(Arrays.asList("315.1C", "309.1C", "309.1CC", "523d", "524d", "524.1AC", "524.1ACAC", "3107d", "16182C",
				"16183C", "16193.1C", "16193.1CC", "16519C"));

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", ref, hotspots);

		assertEquals(101, sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().size());

		HashSet<String> setBefore = new HashSet<String>();

		for (int i = 0; i < sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().size(); i++) {
			setBefore.add(sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().get(i) + "");
		}

		sampleFile.applyNomenclatureRules(phylotree, "test-data/rules/rules.csv");

		HashSet<String> setAfter = new HashSet<String>();

		for (int i = 0; i < sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().size(); i++) {
			setAfter.add(sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().get(i) + "");
		}

		HashSet<String> setAfterDiffs = new HashSet<String>(setAfter);
		HashSet<String> setBeforeDiffs = new HashSet<String>(setBefore);

		setAfterDiffs.removeAll(setBefore);
		setBeforeDiffs.removeAll(setAfter);
		
		//System.out.println(setAfterDiffs.toString());
		//System.out.println(setBeforeDiffs.toString());

		assertEquals(8, setAfterDiffs.size());
		assertEquals(9, setBeforeDiffs.size());


		//differences due to rule 3107C 3106d -> 3107d)
		assertEquals(true, setAfter.contains("3107d"));
		assertEquals(false, setAfter.contains("3106d"));
		assertEquals(false, setAfter.contains("3107C"));

		assertEquals(true,setBefore.contains("3106d"));
		assertEquals(true,setBefore.contains("3107C"));


		assertEquals(100, sampleFile.getTestSamples().get(0).getSample().getPolymorphisms().size());

	}

}