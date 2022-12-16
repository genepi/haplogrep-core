package exporter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Reference;
import core.SampleFile;
import exceptions.parse.sample.InvalidPolymorphismException;
import importer.HsdImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;

public class HsdExport {

	private static Phylotree phylotree = null;

	private static Reference reference = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException {

		reference = new Reference("test-data/reference/rcrs/rCRS.fasta");

		phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", reference);

	}

	@Test
	public void exportTophit() throws Exception {
		String file = "test-data/hsd/h100.hsd";
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");
		ref.setName("RSRS");

		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples, ref);

		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		newSampleFile.updateClassificationResults(phylotree, newRanker);

		ExportUtils.createReport(newSampleFile.getTestSamples(), ref, "test.txt", true);

		assertEquals(1, countLineBufferedReader("test.txt") - 1);

	}

	@Test
	public void exportSeveralHits() throws Exception {
		String file = "test-data/hsd/h100.hsd";
		Reference ref = new Reference("test-data/reference/rsrs/rsrs.fasta");
		ref.setName("RSRS");

		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples, ref);

		// create top 10 hits
		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		newSampleFile.updateClassificationResults(phylotree, newRanker);

		// export top 5 hits
		ExportUtils.createReport(newSampleFile.getTestSamples(), ref, "test.txt", true, 5);

		assertEquals(5, countLineBufferedReader("test.txt") - 1);

	}

	public static long countLineBufferedReader(String fileName) {

		long lines = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			while (reader.readLine() != null) {
				lines++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;

	}

}
