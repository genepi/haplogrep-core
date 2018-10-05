package contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import core.SampleFile;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class HaplogroupClassifierTests {

	@Test
	public void testSplitAndClassify() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String variantFile = "test-data/contamination/lab-mixture/variants-mixture.txt";

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(26, count);

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		assertEquals("H1c6", samples.getTestSamples().get(0).getTopResult().getHaplogroup().toString());

		assertEquals("U5a2e", samples.getTestSamples().get(1).getTopResult().getHaplogroup().toString());

		String hgFile = "test-data/contamination/lab-mixture/variants-mixture-hg.txt";

		ContaminationCheckerTests.createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/lab-mixture/variants-mixture-report.txt";

		ContaminationChecker contChecker = new ContaminationChecker();

		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		reader.next();
		System.out.println(reader.getString("Contamination"));

		assertEquals("HG_conflict", reader.getString("Contamination"));

		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);
	}

}
