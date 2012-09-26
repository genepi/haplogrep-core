package qualityAssurance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import phylotree.Phylotree;

import qualityAssurance.rules.CheckForSampleRCRSAligned;
import qualityAssurance.rules.HaplogrepRule;
import core.SampleFile;
import core.TestSample;

public class QualityAssistent {
	Collection<TestSample> sampleToCheck;
	RuleSet rules;
	HashMap<TestSample, ArrayList<QualityIssue>> allQualityIssuesLookup = new HashMap<TestSample, ArrayList<QualityIssue>>();
	HashMap<Integer, QualityIssue> issueLookup = new HashMap<Integer, QualityIssue>();
	
	ArrayList<QualityIssue> allQualityIssues = new ArrayList<QualityIssue>();
	
	Phylotree usedPhyloTree = null;
	int numIssues = 0;
	int numIssuedWarnings = 0;
	int numIssuedErrors = 0;
	
	
	public QualityAssistent(Collection<TestSample> fileToCheck,RuleSet usedRules,Phylotree usedPhyloTree){
		this.sampleToCheck = fileToCheck;
		this.rules = usedRules;
		this.usedPhyloTree = usedPhyloTree;
//		if(sampleToCheck.get(0).getResults().size() > 0)
//			this.usedPhyloTree = fileToCheck.get(0).getResults().
//					get(0).getSearchResult().getAttachedPhyloTreeNode().getTree();		
	}
	
	public void reevaluateRules(){
		for(TestSample currentSample : sampleToCheck){
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
			if(currentIssue.priority == 0 && !currentIssue.isSuppress())
				numWarningsPerSample++;
		}
		
		
		return numWarningsPerSample;
	}
	
	public int getNumIssuedErrors(TestSample sample) {
		int numErrorsPerSample = 0;
		
		if(allQualityIssuesLookup.get(sample) != null)
		for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
			if(currentIssue.priority == 1 && !currentIssue.isSuppress())
				numErrorsPerSample++;
		}
		
		
		return numErrorsPerSample;
	}
	
	public QualityIssue getIssue(TestSample sample,String desc){
		if(allQualityIssuesLookup.get(sample) != null){
			for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
				if(currentIssue.description.contains(desc))
					return currentIssue;
			}
		}
		return null;
	}
	public String toString(){
		String s = "Quality Issues: \n";
		for(QualityIssue currentIssue : allQualityIssues){
			if(!currentIssue.isSuppress())
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
		
		ArrayList<QualityIssue> notSuppressedIssues = new ArrayList<QualityIssue>();
		
		for(QualityIssue currentIssue : allQualityIssues){
			if(!currentIssue.isSuppress())
				notSuppressedIssues.add(currentIssue);
		}
		
		JSONArray jsonArray = JSONArray.fromObject(notSuppressedIssues,conf);
		
		return jsonArray;
	}
	
	public JSONArray getSampleIssues(String sampleID){
		ArrayList<QualityIssue> sampleQualityIssues = new ArrayList<QualityIssue>();
		for(QualityIssue currentIssue : allQualityIssues){
			if(currentIssue.getSampleID().equals(sampleID))
			sampleQualityIssues.add(currentIssue);
		}
		JsonConfig conf = new JsonConfig();
		conf.setExcludes(new String[]{"sampleOfIssue"});
		JSONArray jsonArray = JSONArray.fromObject(sampleQualityIssues,conf);
		
		return jsonArray;
	}

	
}
