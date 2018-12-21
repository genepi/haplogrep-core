package util;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import contamination.ContaminationDetection;
import contamination.HaplogroupClassifier;
import contamination.VariantSplitter;
import contamination.objects.ContaminationObject;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.FileUtil;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class ContaminationTool {

	public static void main(String[] args) throws Exception {
		
		String folder = "/home/seb/Desktop";

		File file = new File(FileUtil.path(folder, "variants.vcf.gz"));
		String outputHsd = FileUtil.path(folder, "variants.hsd");
		String output = FileUtil.path(folder, "report.txt");
		String level = "0.01";
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		
		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples, Double.valueOf(level));

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ContaminationDetection contamination = new ContaminationDetection();
		ArrayList<ContaminationObject> contaminationList = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		ExportUtils.createHsdInput(haplogrepSamples.getTestSamples(), outputHsd);
		contamination.writeFile(contaminationList, output);
	}

}
