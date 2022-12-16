package general;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jdom.JDOMException;
import org.junit.Test;

import core.Polymorphism;
import core.Reference;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import importer.FastaImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;

public class FastaTest {

	@Test
	public void rcrsTest() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());
	}

	@Test
	public void rsrsTest() throws Exception {
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
	public void rCrsWithRsrsReferenceTest() throws Exception {
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
	public void parseSampleWithDeletions() throws Exception {
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
	public void parseSampleWithInsertionsDeletions() throws Exception {
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
	public void parseSampleWithInsertionsDeletionsShuffle() throws Exception {
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
	public void parseSampleWithInsertionsDeletionsShuffle2() throws Exception {
		String file = "test-data/fasta/InsertionTest3.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		System.out.println(set.toString());
		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));
	}

	@Test
	public void test_generateFasta()
			throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException {
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", ref, null);

		ArrayList<TestSample> testArray = new ArrayList<TestSample>();

		ArrayList<Polymorphism> polys = new ArrayList<Polymorphism>();

		polys.add(new Polymorphism(ref, "263G"));
		polys.add(new Polymorphism(ref, "8860G"));
		polys.add(new Polymorphism(ref, "16311C"));
		polys.add(new Polymorphism(ref, "73G"));
		SampleRanges sampleRanges = new SampleRanges();
		sampleRanges.addCompleteRange(ref);
		TestSample testSample = new TestSample("TestSample_full", ref, polys, sampleRanges);
		testArray.add(testSample);

		SampleRanges fragmentRanges = new SampleRanges();
		fragmentRanges.addCustomRange(10, 100);
		fragmentRanges.addCustomRange(102, 110);
		fragmentRanges.addCustomRange(200, 2000);
		fragmentRanges.addCustomRange(3000, 16000);
		fragmentRanges.addCustomRange(16200, 16569);
		testArray.add(new TestSample("TestSample_Ranges_N1", ref, polys, fragmentRanges));

		fragmentRanges = new SampleRanges();
		fragmentRanges.addCustomRange(11, 20);
		fragmentRanges.addCustomRange(22, 55);
		fragmentRanges.addCustomRange(58, 500);
		testArray.add(new TestSample("TestSample_Ranges_N2", ref, polys, fragmentRanges));

		fragmentRanges = new SampleRanges();
		fragmentRanges.addCustomRange(12, 20);
		fragmentRanges.addCustomRange(23, 55);
		fragmentRanges.addCustomRange(59, 500);
		fragmentRanges.addCustomRange(50, 5500);
		fragmentRanges.addCustomRange(5555, 16555);
		testArray.add(new TestSample("TestSample_Ranges_N3", ref, polys, fragmentRanges));

		ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		ExportUtils.generateFasta(testArray, ref, "test-data/fasta/testSamples_ranges.fasta");

		String file = "test-data/fasta/testSamples_ranges.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		TestSample ts1 = TestSample.parse(samples.get(0), ref);
		TestSample ts2 = TestSample.parse(samples.get(1), ref);
		TestSample ts3 = TestSample.parse(samples.get(2), ref);
		TestSample ts4 = TestSample.parse(samples.get(3), ref);

		System.out.println(samples.get(0));
		System.out.println(samples.get(1));
		System.out.println(samples.get(2) + " \t " + ts3.getSample().getSampleRanges().toString());
		System.out.println(samples.get(3) + " \t " + ts4.getSample().getSampleRanges().toString());

		// System.out.println(ts1.getSample().getSampleRanges());

		assertEquals(ts1.getSample().getSampleRanges().toString(), "1-3106 ; 3108-16569 ;");
		// assertEquals(ts2.getSample().getSampleRanges().toString(), "1-100 ;
		// 200-2000 ; 5000-16000 ; 16200-16569");
		assertEquals(ts3.getSample().getSampleRanges().toString(), "11-20 ; 22-55 ; 58-500 ;");
		assertEquals(ts3.getSample().getSampleRanges().toString(), "11-20 ; 22-55 ; 58-500 ;");
	}

	@Test
	public void test_generateFastaMSA()
			throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException {

		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", ref);

		ArrayList<TestSample> testArray = new ArrayList<TestSample>();

		ArrayList<Polymorphism> polys = new ArrayList<Polymorphism>();

		polys.add(new Polymorphism(ref, "263G"));
		polys.add(new Polymorphism(ref, "8860G"));
		polys.add(new Polymorphism(ref, "16311C"));
		polys.add(new Polymorphism(ref, "73G"));
		// SampleRanges sampleRanges = new SampleRanges();
		// sampleRanges.addCompleteRange();
		// TestSample testSample = new
		// TestSample("TestSample_full1",polys,sampleRanges);
		// testArray.add(testSample);

		SampleRanges fragmentRanges = new SampleRanges();
		fragmentRanges.addCustomRange(10, 100);
		fragmentRanges.addCustomRange(102, 110);
		fragmentRanges.addCustomRange(200, 2000);
		fragmentRanges.addCustomRange(3000, 16000);
		fragmentRanges.addCustomRange(16200, 16569);
		testArray.add(new TestSample("TestSample_Ranges_N1", ref, polys, fragmentRanges));
		/*
		 * fragmentRanges = new SampleRanges();
		 * fragmentRanges.addCustomRange(11, 20);
		 * fragmentRanges.addCustomRange(22, 55);
		 * fragmentRanges.addCustomRange(58, 500); testArray.add(new
		 * TestSample("TestSample_Ranges_N2", polys, fragmentRanges));
		 * 
		 * fragmentRanges = new SampleRanges();
		 * fragmentRanges.addCustomRange(12, 20);
		 * fragmentRanges.addCustomRange(22, 55);
		 * fragmentRanges.addCustomRange(58, 500);
		 * fragmentRanges.addCustomRange(50, 5500);
		 * fragmentRanges.addCustomRange(5555, 16555); testArray.add(new
		 * TestSample("TestSample_Ranges_N3", polys, fragmentRanges));
		 */

		for (int j = 0; j < testArray.size(); j++) {
			testArray.get(j).updateSearchResults(phylotree, new KulczynskiRanking());
			System.out.println(testArray.get(j).getSampleID() + " " + testArray.get(j).getDetectedHaplogroup());
		}

		ref = new Reference("test-data/reference/rcrs/rCRS.fasta");

		ExportUtils.generateFastaMSA(testArray, ref, "test-data/fasta/testSamples_ranges.fasta");

		String file = "test-data/fasta/testSamples_ranges_MSA.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		TestSample ts1 = TestSample.parse(samples.get(0), ref);
		// TestSample ts2 = TestSample.parse(samples.get(1));
		// TestSample ts3 = TestSample.parse(samples.get(2));
		// TestSample ts4 = TestSample.parse(samples.get(3));

		System.out.println(samples.get(0));
		// System.out.println(samples.get(1));
		// System.out.println(samples.get(2));
		// System.out.println(samples.get(3));

		// System.out.println(ts1.getSample().getSampleRanges());

		// assertEquals(ts1.getSample().getSampleRanges().toString(), "1-3106 ;
		// 3108-16569 ;");
		//assertEquals(ts1.getSample().getSampleRanges().toString(), "1-100 ; 200-2000 ; 5000-16000 ; 16200-16569");
		// assertEquals(ts3.getSample().getSampleRanges().toString(), "10-20 ;
		// 22-55 ; 58-500 ;");
		// assertEquals(ts3.getSample().getSampleRanges().toString(), "10-20 ;
		// 22-55 ; 58-500 ;");
	}

	/*
	 * @Test public void parsePhylotree17() throws Exception { String file =
	 * "test-data/fasta/Phylotree17hgs.zip"; String fileTemp =
	 * "test-data/fasta/temp.fasta";
	 * 
	 * // Phylotree phylotree = // PhylotreeManager.getInstance().getPhylotree(
	 * "data/phylotree/phylotree17.xml","data/weights/weights17.txt"); Phylotree
	 * phylotree = PhylotreeManager.getInstance().getPhylotree(
	 * "data/phylotree/phylotree17_rsrs.xml",
	 * "data/weights/weights17_rsrs.txt");
	 * 
	 * FileInputStream fileInputStream = new FileInputStream(file);
	 * BufferedInputStream bufferedInputStream = new
	 * BufferedInputStream(fileInputStream); ZipInputStream zin = new
	 * ZipInputStream(bufferedInputStream); ZipEntry ze = null; Map<String,
	 * String> differences = new HashMap<String, String>(); while ((ze =
	 * zin.getNextEntry()) != null) { String expectedHG = ze.getName();
	 * OutputStream out = new FileOutputStream(fileTemp); byte[] buffer = new
	 * byte[20000]; int len; while ((len = zin.read(buffer)) != -1) {
	 * out.write(buffer, 0, len); } out.close(); FastaImporter impFasta = new
	 * FastaImporter();
	 * 
	 * ArrayList<String> samples = impFasta.load(new File(fileTemp),
	 * References.RSRS);
	 * 
	 * List<RankedResult> result =
	 * phylotree.search(TestSample.parse(samples.get(0)), new HammingRanking());
	 * String expected = samples.get(0).split("\t")[0]; String resulting =
	 * result.get(0).getHaplogroup().toString();
	 * 
	 * if (!expected.equals(resulting)) { differences.put(expected, resulting);
	 * }
	 * 
	 * } /// System.out.println(differences.size()); for (Map.Entry<String,
	 * String> entry : differences.entrySet()) { //
	 * System.out.println("Expected = " + entry.getKey() + ", Resulting = " + //
	 * entry.getValue()); } zin.close();
	 * 
	 * }
	 */
}
