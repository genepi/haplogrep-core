import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import search.ranking.HammingRanking;

import core.SampleFile;
import exceptions.parse.HsdFileException;


public class QualityAssuranceTests {

	@Test
	public void testMatchingRulesDetectedExcpectedHg() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		testFile.updateClassificationResults(phyoTree, new HammingRanking());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),RuleSet.createStandardRuleSet(),phyoTree);
		
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
		rules.addRule(new CheckForTooManyGlobalPrivateMutations());
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
		rules.addRule(new qualityAssurance.CheckForSampleRange());
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
		rules.addRule(new CheckForSampleRSRSAligned());
		rules.addRule(new CheckForSampleRCRSAligned());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(4, newQualityAssistent.getNumIssuedErrors());
	}

	@Test
	public void testCompleteRuleSet() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/qualityIssuesTestFile.hsd",true);
		Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt");
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","weights14.txt"), new HammingRanking());
		
		RuleSet rules = RuleSet.createPreClassificationRuleSet();
		QualityAssistent preChecksQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
		
		preChecksQualityAssistent.reevaluateRules();
		System.out.println(preChecksQualityAssistent);
		System.out.println(preChecksQualityAssistent.getAllIssuesJSON().toString());
	
		//assertEquals(2, preChecksQualityAssistent.getNumIssuedErrors(testFile.getTestSample("5019773")));
		//assertEquals(3, preChecksQualityAssistent.getNumIssuedErrors());
		
		 rules = RuleSet.createStandardRuleSet();
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getPreChecksPassedSamples(),rules,phyoTree);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		System.out.println(newQualityAssistent.getAllIssuesJSON().toString());
		
		
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
}
