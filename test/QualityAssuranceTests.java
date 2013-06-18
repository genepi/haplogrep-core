import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.rules.CheckForRecombinationRule;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForSampleRange;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import search.ranking.HammingRanking;

import core.Haplogroup;
import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.HsdFileException;


public class QualityAssuranceTests {

	@Test
	public void testMatchingRulesDetectedExcpectedHg() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		testFile.updateClassificationResults(phyoTree, new HammingRanking());
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testCheckTooManyPrivateMutations() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForTooManyGlobalPrivateMutations(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForMetaboChip() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/selected-samples-metabochip.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRange(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForRSRS() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/RSRS.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRSRSAligned(0));
		rules.addRule(new CheckForSampleRCRSAligned(0));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(4, newQualityAssistent.getNumIssuedErrors());
	}

	@Test
	public void testCompleteRuleSet() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/qualityIssuesTestFile.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		
		newQualityAssistent.getAllIssuesJSON();
		//assertEquals(2, preChecksQualityAssistent.getNumIssuedErrors(testFile.getTestSample("5019773")));
		//assertEquals(3, preChecksQualityAssistent.getNumIssuedErrors());
			
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testCompleteRangeDetectionRule() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/completeRangeDetection.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		
		RuleSet rules = new RuleSet();
		rules.addStandardRules();
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testRecombinationControlRegion() throws Exception {
		SampleFile testFile = new  SampleFile("/testDataFiles/Bandelt.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
//	ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
//	file.addAll(testFile.getTestSamples());
//		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForRecombinationRule(4));
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
//		newQualityAssistent.getIssueByID(0).getDescription()
//		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testRecombinationRule() throws Exception {
		SampleFile testFile = new  SampleFile("/testDataFiles/test4Recombination.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
	ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
	file.addAll(testFile.getTestSamples());
//		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForRecombinationRule(4));
		QualityAssistent newQualityAssistent = new QualityAssistent(file,rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
//		newQualityAssistent.getIssueByID(0).getDescription()
//		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void test() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/zeroPolysFound.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		testFile.updateClassificationResults(phyoTree, new HammingRanking());
		testFile.getTestSample("5019784").getSample().getSampleRanges().clear();
		testFile.getTestSample("5019784").getSample().getSampleRanges().addMetaboChipRange();
		System.out.println();
		System.out.println(testFile.getTestSample("5019784").getClusteredSearchResults());
	}
	
	@Test
	public void testRecombinationGenerator() throws Exception{
		SampleFile testFile = new  SampleFile("/testDataFiles/test4recombination.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree15.xml","weights15.txt");
		ArrayList<TestSample> file = generateRecombination(phyoTree,testFile);
		
		SampleFile newFile = new SampleFile();
		newFile.setTestSamples(file);
		FileWriter fileWriter = new FileWriter("GeneratedRecombination.hsd");
		fileWriter.write(newFile.toHSDFileString());
		fileWriter.close();

		System.out.println();
		for(TestSample currentSample : file){
			System.out.println(currentSample);
		}
	}
	
	private ArrayList<TestSample> generateRecombination(Phylotree phylotee, SampleFile samplesToRecombinate) throws Exception{
		if(samplesToRecombinate.getTestSamples().size() < 2){
			throw new Exception("The recombination generator requries at least two testSamples as input");
		}
		
		SampleRanges ranges  = new SampleRanges();
		ranges.addCustomRange(2488, 10858);
		ranges.addCustomRange(10898, 2687);
//		ranges.addCustomRange(0, 1000);
//		ranges.addCustomRange(1000, 16569);
		
		ArrayList<TestSample> recombiantedTestSamples = new ArrayList<TestSample>();
		
		
		ArrayList<ArrayList<TestSample>> fragments = new ArrayList<ArrayList<TestSample>>();
		ArrayList<Haplogroup> haplogroupsOfFragments = new ArrayList<Haplogroup>();
 		
		for(TestSample currentSample : samplesToRecombinate.getTestSamples()){
			Haplogroup haplogroup = phylotee.search(currentSample, new HammingRanking(1)).get(0).getHaplogroup();
			haplogroupsOfFragments.add(haplogroup);
			fragments.add(currentSample.createFragments(ranges));
		}
		
		Random r = new Random(5);
		
		
		
		String recombinatedSampleID = "";
		for(int i = 0; i < samplesToRecombinate.getTestSamples().size();i++){
			int nextRandom = r.nextInt(samplesToRecombinate.getTestSamples().size());
			TestSample currentSample = samplesToRecombinate.getTestSamples().get(i);
			ArrayList<Polymorphism> newRecombiantedPolys = new ArrayList<Polymorphism>();
			
			recombinatedSampleID = currentSample.getSampleID();
			recombinatedSampleID += "_" + haplogroupsOfFragments.get(i);
			newRecombiantedPolys.addAll(fragments.get(i).get(0).getSample().getPolymorphisms());
			
			while(true){
				nextRandom = r.nextInt(samplesToRecombinate.getTestSamples().size());		
				if(i != nextRandom)
					break;
			}
			
			currentSample = samplesToRecombinate.getTestSamples().get(nextRandom);
			recombinatedSampleID += "_" + currentSample.getSampleID();
			recombinatedSampleID += "_" + haplogroupsOfFragments.get(nextRandom);
			
			newRecombiantedPolys.addAll(fragments.get(nextRandom).get(1).getSample().getPolymorphisms());
			
			recombiantedTestSamples.add(new TestSample(recombinatedSampleID, newRecombiantedPolys, currentSample.getSample().getSampleRanges()));
		}
		
		return recombiantedTestSamples;
	}
	
	@Test
	public void testSampleRange() throws Exception{
		SampleRanges ranges  = new SampleRanges();
		ranges.addCustomRange(2488, 10858);
		ranges.addCustomRange(10898, 2687);
		
		Assert.assertTrue(ranges.contains(new Polymorphism("73G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("16512G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("10898G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("2687G")));
		
		ranges  = new SampleRanges();
		ranges.addCustomRange(16024, 16569);
		ranges.addCustomRange(74, 576);
			
		Assert.assertFalse(ranges.contains(new Polymorphism("73G")));
		Assert.assertTrue(ranges.contains(new Polymorphism("16512G")));
		Assert.assertFalse(ranges.contains(new Polymorphism("10898G")));
		Assert.assertFalse(ranges.contains(new Polymorphism("2687G")));
	}
}
