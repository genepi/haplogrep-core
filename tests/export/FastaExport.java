package export;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Reference;
import core.SampleFile;
import exceptions.parse.sample.InvalidPolymorphismException;
import importer.FastaImporter;
import importer.HsdImporter;
import static org.junit.Assert.assertEquals;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;

public class FastaExport {

	private static Phylotree phylotree = null;

	private static Reference reference = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException {

		reference = new Reference("test-data/reference/rcrs/rCRS.fasta");
		
		HashSet<String> hotspots = new HashSet<>(Arrays.asList("315.1C", "309.1C", "309.1CC", "523d","524d","524.1AC","524.1ACAC","3107d","16182C","16183C","16193.1C","16193.1CC","16519C"));

		phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", reference, hotspots);

	}
	
	@Test
	public void exportFasta() throws Exception {
		String file = "test-data/fasta/test3_removeNs.fasta";
		Reference ref = new Reference("test-data/reference/rcrs/rCRS.fasta");
		
		FastaImporter importFasta = new FastaImporter();

		ArrayList<String> samples = importFasta.load(new File(file), ref);
		SampleFile newSampleFile = new SampleFile(samples, ref);

		KulczynskiRanking newRanker = new KulczynskiRanking(10);
		newSampleFile.updateClassificationResults(phylotree, newRanker);

		ExportUtils.generateFasta(newSampleFile.getTestSamples(), ref, "test3_out.txt");

	}
	
}