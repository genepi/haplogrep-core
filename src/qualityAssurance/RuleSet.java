package qualityAssurance;

import java.util.ArrayList;
import java.util.HashMap;

import core.TestSample;
import qualityAssurance.issues.QualityIssue;
import qualityAssurance.rules.CheckExpectedHGMatchesDetectedHG;
import qualityAssurance.rules.CheckForAlignmentWarnings;
import qualityAssurance.rules.CheckForHeteroplasmy;
import qualityAssurance.rules.CheckForQuality;
import qualityAssurance.rules.CheckForRecombinationRule;
import qualityAssurance.rules.CheckForReferencePolymorhisms;
import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.CheckForSampleRSRSAligned;
import qualityAssurance.rules.CheckForSampleRange;
import qualityAssurance.rules.CheckForTooManyGlobalPrivateMutations;
import qualityAssurance.rules.CheckForTooManyLocalPrivateMutations;
import qualityAssurance.rules.HaplogrepRule;

public class RuleSet {
	HashMap<Integer,ArrayList<HaplogrepRule>> rules = new HashMap<Integer,ArrayList<HaplogrepRule>>();
	
	public RuleSet(){
		
	}
	
	public void addStandardRules() {
		addRule(new CheckForSampleRange(0));
	//	addRule(new CheckForSampleRCRSAligned(0));
	//	addRule(new CheckForSampleRSRSAligned(0));
	//	addRule(new CheckExpectedHGMatchesDetectedHG(1));
		addRule(new CheckForReferencePolymorhisms(1));
		addRule(new CheckForTooManyGlobalPrivateMutations(1));
		addRule(new CheckForTooManyLocalPrivateMutations(1));
		addRule(new CheckForAlignmentWarnings(1));
		addRule(new CheckForHeteroplasmy(1));
		addRule(new CheckForQuality(1));
		addRule(new CheckForRecombinationRule(4));
		//TODO DEFINE RULES
	}
	
	
	public void addRule(HaplogrepRule newRule){
		 if(!rules.containsKey(newRule.getPriority()))
			 rules.put(newRule.getPriority(), new ArrayList<HaplogrepRule>());
		 
		this.rules.get(newRule.getPriority()).add(newRule);
	}
	
	void reevaluateAllRules(QualityAssistent qualityAssistent, TestSample currentSample) {
		for (int i = 0; i < 10;i++)
			if(qualityAssistent.getIssues(currentSample).size() == 0)	
				reevaluateRules(qualityAssistent, i, currentSample);
				

		for (int currentPriority : rules.keySet())
			if(currentSample.getQualityLevelReached() <= currentPriority)
				suppressRules(qualityAssistent, currentPriority, currentSample);

	}
	
	private void reevaluateRules(QualityAssistent qualityAssistent,int priority,TestSample currentSample) {
		for(HaplogrepRule currentRule : rules.get(priority)){
			currentRule.evaluate(qualityAssistent,currentSample);
		}
	}
	
	private void suppressRules(QualityAssistent qualityAssistent,int priority,TestSample currentSample) {
		for(HaplogrepRule currentRule : rules.get(priority)){
			currentRule.suppressIssues(qualityAssistent,currentSample);
		}
	}

	public ArrayList<HaplogrepRule> getRulesLevel(int levelID) {
		return rules.get(levelID);
	}
}
