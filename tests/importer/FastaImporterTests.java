package importer;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.junit.Test;

import core.Reference;
import importer.FastaImporter;

public class FastaImporterTests {

	@Test
	public void testRcrs() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i]);
		}
		//1 = 3107N
		assertEquals("3107N", actual.toString());
	}

	@Test
	public void testRsrs() throws Exception {
		String file = "test-data/fasta/rsrs.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadRSRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i]+ " ");
		}
		//includes 523-524N and 3107N		

		assertEquals("523N 524N 3107N ", actual.toString());

	}

	@Test
	public void testRCrsWithRsrsReference() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadRSRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		System.out.println(samples.get(0));
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i].trim() + " ");
		}
		System.out.println("" + actual);

		// exactly 52 differences between rsrs and rCRS + one additional on 3107N!!
		assertEquals("73A 146T 152T 195T 247G 263A 523A 524C 750A 769G 825T 1018G 1438A 2706A 2758G 2885T 3107N 3594C 4104A 4312C 4769A 7028C 7146A 7256C 7521G 8468C 8655C 8701A 8860A 9540T 10398A 10664C 10688G 10810T 10873T 10915T 11719G 11914G 12705C 13105A 13276A 13506C 13650C 14766C 15326A 16129G 16187C 16189T 16223C 16230A 16278C 16311T 16519T ", actual.toString() );

	}

	@Test
	public void testParseSampleWithDeletions() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");

		boolean deletion = false;
		for (int i = 3; i < splits.length; i++) {
			if (splits[i].equals("514-515d")) {
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
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();
		
		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}
		System.out.println(set);

		assertEquals(true, set.contains("16182.1C"));
		//assertEquals(true, set.contains("309.1CCT")); //BWA
		assertEquals(true, set.contains("302.1CC")); //MINIMAP2
		assertEquals(true, set.contains("310.1C")); //MINIMAP2
		//assertEquals(true, set.contains("3106-3106d")); //BWA
		assertEquals(true, set.contains("3107-3107d")); //MINIMAP2		
		assertEquals(true, set.contains("8270-8277d"));
	}
	
	// copied first two lines of fasta (including 309.1C etc to end of line)
	@Test
	public void testParseSampleWithInsertionsDeletionsShuffle() throws Exception {
		String file = "test-data/fasta/InsertionTest2.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();
		
		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}
		System.out.println(set);

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("302.1CC"));
		assertEquals(true, set.contains("3107-3107d"));
		assertEquals(true, set.contains("8270-8277d"));

	}
	
	// random shuffle
	@Test
	public void testParseSampleWithInsertionsDeletionsShuffleRandom() throws Exception {
		String file = "test-data/fasta/InsertionTest3.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();
		
		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
			}
		
		System.out.println(set);

		assertEquals(true, set.contains("16182.1C"));
		//assertEquals(true, set.contains("309.1CCT")); //BWA
		assertEquals(true, set.contains("302.1CC")); //MINIMAP2
		assertEquals(true, set.contains("310.1C")); //MINIMAP2
		//assertEquals(true, set.contains("3106-3106d")); //BWA
		assertEquals(true, set.contains("3107-3107d")); //MINIMAP2
		assertEquals(true, set.contains("8270-8277d"));

	}
	
	// random shuffle
	@Test
	public void testTrickyNs() throws Exception {
		String file = "test-data/fasta/ANI152.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();
		
	
		assertEquals(splits[1].trim(),"49-164;238-376;454-456;459-830;868-1005;1085-1527;1533-1694;1870-1917;1919-1943;1989-2038;2079-2214;2301-2389;2391-2738;2754-2846;2864-2926;2957-3105;3107-3117;3150-3264;3275-3323;3335-3476;3524-3603;3609-3796;3868-4035;4112-4225;4349-4572;4605-4725;4759-4851;4901-5038;5093-5327;5347-5474;5531-5809;5819-5955;5993-6041;6131-6190;6196-6564;6684-6753;6804-6855;6857-6874;6876-7013;7015-7135;7162-7256;7302-7356;7372-7515;7647-8037;8052-8319;8383-8441;8521-8634;8769-9017;9059-9102;9113-9264;9266-9346;9373-9661;9686-9686;9688-9689;9691-9829;9877-9972;10153-10226;10251-10328;10656-10784;10925-11029;11100-11217;11225-11352;11362-11545;11560-11797;11821-11984;11990-12035;12171-12324;12375-12399;12401-12454;12509-12679;12681-12682;12728-13159;13182-13387;13401-13512;13568-13638;13696-13855;13914-13918;13920-14005;14039-14265;14315-14469;14534-14625;14634-14723;14786-15449;15476-15515;15560-15811;15872-15930;15994-16149;16169-16305;16318-16568;");
		
	}
	
	@Test
	public void testB5a1() throws Exception {
		String file = "test-data/fasta/B5a1.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");
		
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals(actual.toString(), "73G,210G,216G,750G,1438G,1789G,2706G,4769G,6012G,6575G,6659G,6706G,6707A,6805T,6960T,7028T,7546C,8584A,8860G,9950C,10398G,11204C,11719A,15235G,15326G,16129A,16183C,16189C,16249C,16519C,514-515d,3107-3107d,");

	}
	
	@Test
	public void testParseSampleWithNs() throws Exception {
		String file = "test-data/fasta/B5a1_withN.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		String allRanges = "";
		
		for (int s=0; s<samples.size(); s++) {
		String[] splits = samples.get(s).split("\t");
		allRanges+=splits[1];
		}
		assertEquals("10-16569;11-18;24-16569;1-8277;8280-16569;1-309;311-16181;16183-16569;1-16569;1-309;311-16564;", allRanges);

	}
}