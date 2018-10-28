package contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;
import contamination.Contamination.Status;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporterImproved;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class HaplogroupClassifierTests {

	@Test
	public void testSplitAndClassify() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		
		 VcfImporterImproved reader2 = new VcfImporterImproved();
		 HashMap<String, Sample> mutationServerSamples = reader2.load(new File("test-data/contamination/lab-mixture/variants-mixture.vcf"), false);

		VariantSplitter splitter = new VariantSplitter();
		
		ArrayList<String> profiles = splitter.split(mutationServerSamples);
		
		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(25, count);

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		
		SampleFile haploGroupSamples = classifier.calculateHaplogrops(phylotree, profiles);

		assertEquals("H1", haploGroupSamples.getTestSamples().get(0).getTopResult().getHaplogroup().toString());

		assertEquals("U5a2e", haploGroupSamples.getTestSamples().get(1).getTopResult().getHaplogroup().toString());

		String out = "test-data/contamination/lab-mixture/variants-mixture-report.txt";

		Contamination contChecker = new Contamination();
		
		contChecker.calcContamination(mutationServerSamples, haploGroupSamples.getTestSamples(), out);

		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		//get first line
		readerContamination.next();

		assertEquals(Status.HIGH.name(), readerContamination.getString("Contamination"));
		assertEquals("6", readerContamination.getString("MajorHomoplasmies"));
		assertEquals("7", readerContamination.getString("SampleHomoplasmies"));
		assertEquals("0.987", readerContamination.getString("MajorMeanHetLevel"));
		assertEquals("6", readerContamination.getString("MinorHomoplasmies"));
		assertEquals("0.011", readerContamination.getString("MinorMeanHetLevel"));
		assertEquals("12", readerContamination.getString("MinorHeteroplasmies"));
		assertEquals("18", readerContamination.getString("SampleHeteroplasmies"));
		assertEquals("H1", readerContamination.getString("MajorHG"));
		assertEquals("U5a2e", readerContamination.getString("MinorHG"));

		//FileUtil.deleteFile(out);
	}

}
