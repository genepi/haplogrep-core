

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import importer.FastaImporter;
import importer.VcfImporter;
import junit.framework.Assert;

public class FastaTest {

	@Test
	public void rcrsTest() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), false);

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
		ArrayList<String> samples = impFasta.load(new File(file), true);

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
		ArrayList<String> samples = impFasta.load(new File(file), true);

		String[] splits = samples.get(0).split("\t");
		
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		
		//exactly 52 differences between rsrs and rCRS
		assertEquals(52, (splits.length)-3);

	}
	
	@Test
	public void parseSampleWithDeletions() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), true);

		String[] splits = samples.get(0).split("\t");
		
		boolean deletion = false;
		
		for (int i = 3; i < splits.length; i++) {
			if(splits[i].equals("523d")) {
				deletion = true;
			}
			actual.append(splits[i] + ",");
		}
		
		assertEquals(true, deletion);
		

	}
	
	@Test
	public void parseSampleWithInsertions() throws Exception {
		String file = "test-data/fasta/InsertionTest.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), false);

		String[] splits = samples.get(0).split("\t");
		
		for (int i = 3; i < splits.length; i++) {
			System.out.println(splits[i]);
			actual.append(splits[i] + ",");
		}
		
	}
	


}
