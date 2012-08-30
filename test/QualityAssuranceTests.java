import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForSampleRange;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import search.ranking.HammingRanking;

import core.SampleFile;
import exceptions.parse.HsdFileException;


public class QualityAssuranceTests {

	@Test
	public void testMatchingRulesDetectedExcpectedHg() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","fluctRates14.txt"), new HammingRanking());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile,RuleSet.createStandardRuleSet());
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testCheckTooManyPrivateMutations() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/Burma_44GG.txt",true);
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","fluctRates14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForTooManyGlobalPrivateMutations());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile,rules);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForMetaboChip() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/selected-samples-metabochip.hsd",true);
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","fluctRates14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRange());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile,rules);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}
	
	@Test
	public void testForRSRS() throws HsdFileException, IOException {
		SampleFile testFile = new  SampleFile("/testDataFiles/RSRS.hsd",true);
		
		testFile.updateClassificationResults(PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","fluctRates14.txt"), new HammingRanking());
		
		RuleSet rules = new RuleSet();
		rules.addRule(new CheckForSampleRSRSAligned());
		rules.addRule(new CheckForSampleRCRSAligned());
		QualityAssistent newQualityAssistent = new QualityAssistent(testFile,rules);
		
		newQualityAssistent.reevaluateRules();
		System.out.println(newQualityAssistent);
		assertEquals(3, newQualityAssistent.getNumIssuedWarnings());
	}


}
