package qualityAssurance;

import java.util.ArrayList;
import java.util.HashMap;

import phylotree.Phylotree;

import qualityAssurance.rules.HaplogrepRule;
import core.SampleFile;
import core.TestSample;

public class QualityAssistent {
	SampleFile fileToCheck;
	RuleSet rules;
	HashMap<TestSample, ArrayList<QualityIssue>> allQualityIssuesLookup = new HashMap<TestSample, ArrayList<QualityIssue>>();
	 ArrayList<QualityIssue> allQualityIssues = new ArrayList<QualityIssue>();
	Phylotree usedPhyloTree = null;
	int numIssuedWarnings = 0;
	int numIssuedErrors = 0;
	
	
	public QualityAssistent(SampleFile fileToCheck,RuleSet usedRules){
		this.fileToCheck = fileToCheck;
		this.rules = usedRules;
		this.usedPhyloTree = fileToCheck.getTestSamples().get(0).getResults().
				get(0).getSearchResult().getAttachedPhyloTreeNode().getTree();		
	}
	
	public void reevaluateRules(){
		for(TestSample currentSample : fileToCheck.getTestSamples()){
			rules.reevaluateRules(this,currentSample);
		}
	}

	public void addNewIssue(QualityIssue newIssue){
		if(!allQualityIssuesLookup.containsKey(newIssue.getSample()))
			allQualityIssuesLookup.put(newIssue.getSample(), new ArrayList<QualityIssue>());
		
		allQualityIssuesLookup.get(newIssue.getSample()).add(newIssue);
		
		allQualityIssues.add(newIssue);
	}

	public Phylotree getUsedPhyloTree() {
		return usedPhyloTree;
	}

	void incNumWarnings(){
		numIssuedWarnings++;
	}
	
	void incNumErrors(){
		numIssuedErrors++;
	}

	public int getNumIssuedWarnings() {
		return numIssuedWarnings;
	}

	public int getNumIssuedErrors() {
		return numIssuedErrors;
	}
	
	public String toString(){
		String s = "Quality Issues: \n";
		for(QualityIssue currentIssue : allQualityIssues){
			s += currentIssue + "\n";
		}
		
		return s;
	}
	
}
