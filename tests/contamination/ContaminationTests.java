package contamination;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import contamination.Contamination.Status;
import contamination.objects.Sample;
import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.results.RankedResult;

public class ContaminationTests {

	@Test
	public void testHighChipMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-chip-mix/";
		String variantFile = folder + "high-chip-mix-1000G.txt";
		String output = folder + "chip-mix-report.txt";

		VariantSplitter splitter = new VariantSplitter();

		MutationServerReader reader = new MutationServerReader(variantFile);

		HashMap<String, Sample> mutationServerSamples = reader.parse();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(26, count);

		//FileUtil.deleteFile(output);

	}

	@Test
	public void testHighFreeMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-free-mix/";
		String variantFile = folder +"high-free-mix-1000G.txt";
		String output = folder + "chip-mix-report.txt";
		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationServerSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		Contamination contamination = new Contamination();
		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(7, count);

		//FileUtil.deleteFile(output);

	}

	@Test
	public void testNoContamination1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/no-contamination/";
		String variantFile = folder + "no-contamination-1000G.txt";
		String output = folder +"no-contamination-report.txt";

		Contamination contamination = new Contamination();

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationserverSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationserverSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testPossibleSwap() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/possible-swap/";
		String variantFile = folder + "possible-swap-1000G.txt";
		String output = folder + "possible-swap-report.txt";

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationserverSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationserverSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationserverSamples, haplogrepSamples.getTestSamples(), output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		//FileUtil.deleteFile(output);

	}
	
	@Test
	public void testBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000G_BAQ.txt";
		String out = folder + "1000g-report.txt";
		String verifyBam = "test-data/contamination/1000G/verifybam/verifybam-1000G.txt";
		String verifyOut = folder + "1000g-report-verifybam.txt";

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationServerSamples = reader.parse();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), out);

		CsvTableReader readerOut = new CsvTableReader(out, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				countHigh++;
			}

			if (readerOut.getString("Contamination").equals(Status.LOW.name())) {
				countLow++;
			}
		}

		// FileUtil.deleteFile(out);

		CsvTableReader readerVerifyBam = new CsvTableReader(verifyBam, '\t');
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		FileWriter writer = new FileWriter(verifyOut);
		writer.write("SAMPLE" +"\t"+ "CONT_FREE" + "\t" + "CONT_MIX"+"\t" + "MINOR_LEVEL" + "\t" + "STATUS" +"\n");
		HashMap<String, String> samples = new HashMap<String, String>();
		
		
		while (readerContamination.next()) {
			String id = readerContamination.getString("SampleID");
			id = id.split("\\.",2)[0];
			String level = readerContamination.getString("MinorMeanHetLevel");
			String status = readerContamination.getString("Contamination");
			
			if(status.contains("HIGH")) {
			samples.put(id, level + "\t" + status);
			}
		}

		while (readerVerifyBam.next()) {
			String id = readerVerifyBam.getString("ID");
			String free = readerVerifyBam.getString("free_contam");
			String chip = readerVerifyBam.getString("chip_contam");
			String add = samples.get(id);
			
			if(add!=null){
			writer.write(id + "\t" + free + "\t" + chip + "\t" + add+"\n");
			}
		}
		writer.close();
		
		assertEquals(121, countHigh);
		assertEquals(29, countLow);

	}
	
	@Test
	public void testNoBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000G_NOBAQ.txt";
		String out = folder + "1000G_NOBAQ_report.txt";
		String verifyBam = "test-data/contamination/1000G/verifybam/verifybam-1000G.txt";
		String verifyOut = folder + "1000g-report-verifybam.txt";
		FileWriter writer = new FileWriter(verifyOut);

		MutationServerReader reader = new MutationServerReader(variantFile);
		HashMap<String, Sample> mutationServerSamples = reader.parse(0.00);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		Contamination contamination = new Contamination();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		contamination.calcContamination(mutationServerSamples, haplogrepSamples.getTestSamples(), out);

		CsvTableReader readerOut = new CsvTableReader(out, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.HIGH.name())) {
				countHigh++;
			}

			if (readerOut.getString("Contamination").equals(Status.LOW.name())) {
				countLow++;
			}
		}

		// FileUtil.deleteFile(out);

		CsvTableReader reader1000G = new CsvTableReader(verifyBam, '\t');
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		writer.write("SAMPLE" +"\t"+ "CONT_FREE" + "\t" + "CONT_MIX"+"\t" + "MINOR_LEVEL" + "\t" + "STATUS" +"\n");
		HashMap<String, String> samples = new HashMap<String, String>();
		while (readerContamination.next()) {
			String id = readerContamination.getString("SampleID");
			id = id.split("\\.",2)[0];
			String level = readerContamination.getString("MinorMeanHetLevel");
			String status = readerContamination.getString("Contamination");
			samples.put(id, level + "\t" + status);
		}

		while (reader1000G.next()) {
			String id = reader1000G.getString("ID");
			String free = reader1000G.getString("free_contam");
			String chip = reader1000G.getString("chip_contam");
			String add = samples.get(id);
			writer.write(id + "\t" + free + "\t" + chip + "\t" + add+"\n");
		}
		writer.close();
		
		assertEquals(117, countHigh);
		assertEquals(28, countLow);

	}

}
