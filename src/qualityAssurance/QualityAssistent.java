package qualityAssurance;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import phylotree.Phylotree;

import qualityAssurance.rules.HaplogrepRule;
import core.SampleFile;
import core.TestSample;

public class QualityAssistent {
	SampleFile fileToCheck;
	RuleSet rules;
	HashMap<TestSample, ArrayList<QualityIssue>> allQualityIssuesLookup = new HashMap<TestSample, ArrayList<QualityIssue>>();
	HashMap<Integer, QualityIssue> issueLookup = new HashMap<Integer, QualityIssue>();
	
	ArrayList<QualityIssue> allQualityIssues = new ArrayList<QualityIssue>();
	Phylotree usedPhyloTree = null;
	int numIssues = 0;
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
		if(!allQualityIssuesLookup.containsKey(newIssue.getSampleOfIssue()))
			allQualityIssuesLookup.put(newIssue.getSampleOfIssue(), new ArrayList<QualityIssue>());
		
		allQualityIssuesLookup.get(newIssue.getSampleOfIssue()).add(newIssue);
		
		issueLookup.put(newIssue.getIssueID(), newIssue);	
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
	
	
	public int getNumIssuedWarnings(TestSample sample) {
		int numWarningsPerSample = 0;
		
		if(allQualityIssuesLookup.get(sample) != null)
		for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
			if(currentIssue.priority == 0)
				numWarningsPerSample++;
		}
		
		
		return numWarningsPerSample;
	}
	
	public int getNumIssuedErrors(TestSample sample) {
		int numErrorsPerSample = 0;
		
		if(allQualityIssuesLookup.get(sample) != null)
		for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
			if(currentIssue.priority == 1)
				numErrorsPerSample++;
		}
		
		
		return numErrorsPerSample;
	}
	
	public String toString(){
		String s = "Quality Issues: \n";
		for(QualityIssue currentIssue : allQualityIssues){
			s += currentIssue + "\n";
		}
		
		return s;
	}

	public int getNewIssueID() {
		numIssues++;
		return numIssues;	
	}
	
	public JSONArray getAllIssuesJSON(){
		JsonConfig conf = new JsonConfig();
		conf.setExcludes(new String[]{"sampleOfIssue"});
		JSONArray jsonArray = JSONArray.fromObject(allQualityIssues,conf);
		
		return jsonArray;
	}

	
}
