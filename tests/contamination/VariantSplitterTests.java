package contamination;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

public class VariantSplitterTests {

	@Test
	public void testContaminationMajor() throws Exception {

		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.splitFile("test-data/contamination/lab-mixture/variants-mixture.txt");

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
	public void testContaminationMinor() throws Exception {

		VariantSplitter contChecker = new VariantSplitter();
		ArrayList<String> profiles = contChecker.splitFile("test-data/contamination/lab-mixture/variants-mixture.txt");

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
	public void testContamination2Samples() throws Exception {

		VariantSplitter contChecker = new VariantSplitter();
		ArrayList<String> profiles = contChecker.splitFile("test-data/contamination/test-mixture/variants-mixture-2samples.txt");

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

}