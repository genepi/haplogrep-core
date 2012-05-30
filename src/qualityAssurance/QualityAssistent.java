package qualityAssurance;

import java.util.ArrayList;

import qualityAssurance.rules.HaplogrepRule;

import core.SampleFile;
import core.TestSample;

public class QualityAssistent {
	SampleFile fileToCheck;
	ArrayList<HaplogrepRule> rules;
	
	public QualityAssistent(SampleFile fileToCheck){
		this.fileToCheck = fileToCheck;
	}
	
	public void reevaluateRules(){
		for(TestSample currentSample : fileToCheck.getTestSamples()){
			reevaluateRules(currentSample);
		}
	}

	private void reevaluateRules(TestSample currentSample) {
		for(HaplogrepRule currentRule : rules){
			currentRule.evaluate(currentSample);
		}
		
	}
}
