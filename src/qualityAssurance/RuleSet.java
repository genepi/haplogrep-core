package qualityAssurance;

import java.util.ArrayList;

import core.TestSample;

import qualityAssurance.rules.CheckExpectedHGMatchesDetectedHG;
import qualityAssurance.rules.CheckForReferencePolymorhisms;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import qualityAssurance.rules.HaplogrepRule;

public class RuleSet {
	ArrayList<HaplogrepRule> rules = new ArrayList<HaplogrepRule>();
	
	public RuleSet(){
		
	}
	
	public static RuleSet createStandardRuleSet(){
		 RuleSet newRuleSet = new RuleSet();
		 newRuleSet.rules.add(new CheckExpectedHGMatchesDetectedHG());		 
		 newRuleSet.rules.add(new CheckForReferencePolymorhisms());
		 newRuleSet.rules.add(new CheckForTooManyGlobalPrivateMutations());

		 
		 return newRuleSet;
	}
	
	public static RuleSet createPreClassificationRuleSet(){
		 RuleSet newRuleSet = new RuleSet();
		 newRuleSet.rules.add(new CheckForSampleRange());
		 newRuleSet.rules.add(new CheckForSampleRCRSAligned());
		 newRuleSet.rules.add(new CheckForSampleRSRSAligned());	
		 return newRuleSet;
	}
	
	public void addRule(HaplogrepRule newRule){
		this.rules.add(newRule);
	}
	
	void reevaluateRules(QualityAssistent qualityAssistent,TestSample currentSample) {
		for(HaplogrepRule currentRule : rules){
			currentRule.evaluate(qualityAssistent,currentSample);
		}
		for(HaplogrepRule currentRule : rules){
			currentRule.suppressIssues(qualityAssistent,currentSample);
		}
	}
}
