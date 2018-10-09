package contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import contamination.objects.Sample;
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
		MutationServerReader reader = new MutationServerReader(variantFile);

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

		HashMap<String, Sample> samples2 = reader.parse();

		ContaminationCheckerTests.createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/lab-mixture/variants-mixture-report.txt";

		ContaminationChecker contChecker = new ContaminationChecker();

		contChecker.calcContaminationSeb(samples2, hgFile, out);

		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		readerContamination.next();

		System.out.println(readerContamination.getString("Contamination"));

		assertEquals("HG_conflict", readerContamination.getString("Contamination"));
		assertEquals("7/7", readerContamination.getString("MajorSNPs"));
		assertEquals("0.987", readerContamination.getString("MajorLevel"));
		assertEquals("6/7", readerContamination.getString("MinorSNPs"));
		assertEquals("0.011", readerContamination.getString("MinorLevel"));
		assertEquals("11", readerContamination.getString("MinorHGvariants"));
		assertEquals("H1c6", readerContamination.getString("MajorHG"));
		assertEquals("U5a2e", readerContamination.getString("MinorHG"));

		FileUtil.deleteFile(hgFile);
		// FileUtil.deleteFile(out);
	}

}
