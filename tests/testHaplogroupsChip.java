import static org.junit.Assert.assertEquals;

import java.io.IOException;

import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import search.ranking.HammingRanking;
import core.SampleFile;
import exceptions.parse.HsdFileException;


public class testHaplogroupsChip {

	public static void main(String[] args) {
		try {
			//SampleFile testFile = new  SampleFile("/testDataFiles/phylotree16_Affy6_filledHG.txt",true);
			SampleFile testFile = new  SampleFile("/testDataFiles/Burma_final.hsd",true);
			Phylotree phyoTree = PhylotreeManager.getInstance().getPhylotree("phylotree16.xml","weights16.txt");
			testFile.updateClassificationResults(phyoTree, new HammingRanking(), null);
			
			RuleSet rules = new RuleSet();
			rules.addStandardRules();
			
			QualityAssistent newQualityAssistent = new QualityAssistent(testFile.getTestSamples(),rules,phyoTree);
			
			newQualityAssistent.reevaluateRules();
			testFile.updateClassificationResults(phyoTree, new HammingRanking(), null);
			newQualityAssistent.reevaluateRules();
			System.out.println(newQualityAssistent);
			 newQualityAssistent.getNumIssuedWarnings();
			
			
		} catch (HsdFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

	}

}
