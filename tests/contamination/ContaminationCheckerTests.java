package contamination;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.results.RankedResult;

public class ContaminationCheckerTests {

	@Test
	public void testHighChipMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VariantSplitter splitter = new VariantSplitter();
		
		String variantFile = "test-data/contamination/baq-mapQ30/high-chip-mix/high-chip-mix-1000G.txt";
		
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationChecker contChecker = new ContaminationChecker();
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		String hgFile = "test-data/contamination/baq-mapQ30/high-chip-mix/hg.txt";
		
		createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/baq-mapQ30/high-chip-mix/report.txt";
		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		
		int count = 0;
		while(reader.next()) {
			if(reader.getString("Contamination").equals("HG_conflict")) {
				count++;
				
			}
		}
		
		assertEquals(26, count);
		
		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);

	}
	
	@Test
	public void testHighFreeMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		ContaminationChecker contChecker = new ContaminationChecker();
		
		String variantFile = "test-data/contamination/baq-mapQ30/high-free-mix/high-free-mix-1000G.txt";
		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		String hgFile = "test-data/contamination/baq-mapQ30/high-free-mix/hg.txt";
		
		createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/baq-mapQ30/high-free-mix/report.txt";
		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		int count = 0;
		while(reader.next()) {
			if(reader.getString("Contamination").equals("HG_conflict")) {
				count++;
				
			}
		}
		
		assertEquals(7, count);
		
		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);

	}
	
	@Test
	public void testNoContamination1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		ContaminationChecker contChecker = new ContaminationChecker();
		
		String variantFile = "test-data/contamination/baq-mapQ30/no-contamination/no-contamination-1000G.txt";
		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		String hgFile = "test-data/contamination/baq-mapQ30/no-contamination/hg.txt";
		
		createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/baq-mapQ30/no-contamination/report.txt";
		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		int count = 0;
		while(reader.next()) {
			if(reader.getString("Contamination").equals("HG_conflict")) {
				count++;
				
			}
		}
		
		assertEquals(0, count);
		
		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);

	}	
	
	@Test
	public void testPossibleSwap() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/possible-swap/possible-swap-1000G.txt";
		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationChecker contChecker = new ContaminationChecker();
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		String hgFile = "test-data/contamination/baq-mapQ30/possible-swap/hg.txt";
		
		createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/baq-mapQ30/possible-swap/report.txt";
		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		int count = 0;
		while(reader.next()) {
			if(reader.getString("Contamination").equals("HG_conflict")) {
				count++;
				
			}
		}
		
		assertEquals(0, count);
		
		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);

	}	
	
	@Test
	public void test1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		String variantFile = "test-data/contamination/baq-mapQ30/1000G-All-Samples/1000G.txt";
		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.splitFile(variantFile);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationChecker contChecker = new ContaminationChecker();
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile samples = classifier.calculateHaplogrops(phylotree, profiles);

		String hgFile = "test-data/contamination/baq-mapQ30/possible-swap/hg.txt";
		
		createFakeReport(samples.getTestSamples(), new File(hgFile));

		String out = "test-data/contamination/baq-mapQ30/possible-swap/report.txt";
		contChecker.calcContamination(hgFile, variantFile, out, 0.01);

		CsvTableReader reader = new CsvTableReader(out, '\t');
		int count = 0;
		while(reader.next()) {
			if(reader.getString("Contamination").equals("HG_conflict")) {
				count++;
				
			}
		}
		
		assertEquals(120, count);
		
		FileUtil.deleteFile(hgFile);
		FileUtil.deleteFile(out);

	}	

	public static void createFakeReport(List<TestSample> sampleCollection, File out) throws IOException {

		StringBuffer result = new StringBuffer();

		Collections.sort((List<TestSample>) sampleCollection);

		result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\tNot_Found_Polys\tFound_Polys\tRemaining_Polys\tAAC_In_Remainings\t Input_Sample\n");

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID() + "\t");

				for (RankedResult currentResult : sample.getResults()) {

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i) + ";";
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i) + ";";
						}
					}
					result.append(resultRange);

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					result.append("\t");

					ArrayList<Polymorphism> found = currentResult.getSearchResult().getDetailedResult().getFoundPolys();

					ArrayList<Polymorphism> expected = currentResult.getSearchResult().getDetailedResult().getExpectedPolys();

					Collections.sort(found);

					Collections.sort(expected);

					for (Polymorphism currentPoly : expected) {
						if (!found.contains(currentPoly))
							result.append(" " + currentPoly);
					}

					result.append("\t");

					for (Polymorphism currentPoly : found) {
						result.append(" " + currentPoly);

					}

					result.append("\t");
					ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
					Collections.sort(allChecked);

					for (Polymorphism currentPoly : allChecked) {
						result.append(" " + currentPoly);
					}

					result.append("\t");

					ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult().getRemainingPolysInSample();
					Collections.sort(aac);

					result.append("\t");

					ArrayList<Polymorphism> input = sample.getSample().getPolymorphisms();

					Collections.sort(input);

					for (Polymorphism currentPoly : input) {
						result.append(" " + currentPoly);
					}
					result.append("\n");

				}
			}
		}

		FileWriter fileWriter = new FileWriter(out);

		fileWriter.write(result.toString().replace("\t ", "\t"));

		fileWriter.close();

	}

}
