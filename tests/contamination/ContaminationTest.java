package contamination;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import core.SampleFile;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class ContaminationTest {

	@Test
	public void contaminationMajorTest() throws Exception {

		ContaminationChecker splitter = new ContaminationChecker();
		ArrayList<String> profiles = splitter.splitFile("test-data/contamination/variants-mixture.txt");

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");
		
		int count = 0;
		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}
		
		assertEquals(26, count);
		assertEquals(true, set.contains("11719G"));
		assertEquals(true, set.contains("12308A"));
		assertEquals(true, set.contains("12372G"));
		assertEquals(true, set.contains("13617T"));
		assertEquals(true, set.contains("1438G"));
		assertEquals(true, set.contains("15236A"));
		assertEquals(true, set.contains("15289T"));
		assertEquals(true, set.contains("15326G"));
		assertEquals(true, set.contains("16129G"));
		assertEquals(true, set.contains("16234C"));
		assertEquals(true, set.contains("16256C"));
		assertEquals(true, set.contains("16270C"));
		assertEquals(true, set.contains("16311T"));
		assertEquals(true, set.contains("263G"));
		assertEquals(true, set.contains("2706A"));
		assertEquals(true, set.contains("3010A"));
		assertEquals(true, set.contains("3107C"));
		assertEquals(true, set.contains("3768A"));
		assertEquals(true, set.contains("4769G"));
		assertEquals(true, set.contains("477C"));
		assertEquals(true, set.contains("5979A"));
		assertEquals(true, set.contains("7028C"));
		assertEquals(true, set.contains("73A"));
		assertEquals(true, set.contains("750G"));
		assertEquals(true, set.contains("8860G"));
		assertEquals(true, set.contains("9145G"));
	}

	@Test
	public void contaminationMinorTest() throws Exception {

		ContaminationChecker contChecker = new ContaminationChecker();
		ArrayList<String> profiles = contChecker.splitFile("test-data/contamination/variants-mixture.txt");

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(1).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(26, count);
		assertEquals(true, set.contains("11719A"));
		assertEquals(true, set.contains("12308G"));
		assertEquals(true, set.contains("12372A"));
		assertEquals(true, set.contains("13617C"));
		assertEquals(true, set.contains("1438G"));
		assertEquals(true, set.contains("15236G"));
		assertEquals(true, set.contains("15289C"));
		assertEquals(true, set.contains("16129A"));
		assertEquals(true, set.contains("16234T"));
		assertEquals(true, set.contains("16256T"));
		assertEquals(true, set.contains("16270T"));
		assertEquals(true, set.contains("16311C"));
		assertEquals(true, set.contains("263G"));
		assertEquals(true, set.contains("2706G"));
		assertEquals(true, set.contains("3010G"));
		assertEquals(true, set.contains("3768G"));
		assertEquals(true, set.contains("5979G"));
		assertEquals(true, set.contains("7028T"));
		assertEquals(true, set.contains("73G"));
		assertEquals(true, set.contains("9145A"));
	}

	@Test
	public void contamination2SamplesTest() throws Exception {

		ContaminationChecker contChecker = new ContaminationChecker();
		ArrayList<String> profiles = contChecker.splitFile("test-data/contamination/variants-mixture-2samples.txt");

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(3, count);
		assertEquals(true, set.contains("11719G"));
		assertEquals(true, set.contains("12308A"));
		assertEquals(true, set.contains("1438G"));
	}
	
	@Test
	public void splitAndClassify() throws Exception {
		
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		ContaminationChecker contChecker = new ContaminationChecker();
		ArrayList<String> profiles = contChecker.splitFile("test-data/contamination/variants-mixture.txt");

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(26, count);
		
		SampleFile samples = contChecker.calculateHaplogrops(phylotree, profiles);
		
		assertEquals("H1c6", samples.getTestSamples().get(0).getTopResult().getHaplogroup().toString());

		assertEquals("U5a2e", samples.getTestSamples().get(1).getTopResult().getHaplogroup().toString());
		
	}
	
}
