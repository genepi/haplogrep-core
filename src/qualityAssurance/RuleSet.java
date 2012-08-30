package qualityAssurance;

import java.util.ArrayList;

import core.TestSample;

import qualityAssurance.rules.CheckExpectedHGMatchesDetectedHG;
import qualityAssurance.rules.HaplogrepRule;

public class RuleSet {
	ArrayList<HaplogrepRule> rules = new ArrayList<HaplogrepRule>();
	
	public RuleSet(){
		
	}
	
	public static RuleSet createStandardRuleSet(){
		 RuleSet newRuleSet = new RuleSet();
		 newRuleSet.rules.add(new CheckExpectedHGMatchesDetectedHG());
		 
		 return newRuleSet;
	}
	
	public void addRule(HaplogrepRule newRule){
		this.rules.add(newRule);
	}
	
	void reevaluateRules(QualityAssistent qualityAssistent,TestSample currentSample) {
		for(HaplogrepRule currentRule : rules){
			currentRule.evaluate(qualityAssistent,currentSample);
		}
		
	}
}
