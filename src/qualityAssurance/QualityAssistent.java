package qualityAssurance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import phylotree.Phylotree;
import qualityAssurance.issues.QualityIssue;


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

	public void removeIssue(QualityIssue issue) {
		allQualityIssuesLookup.remove(issue);
		issueLookup.remove(issue.getIssueID());
		allQualityIssues.remove(issue);
	}
	
	public Phylotree getUsedPhyloTree() {
		return usedPhyloTree;
	}

	public void incNumWarnings(){
		numIssuedWarnings++;
	}
	
	public void decNumWarnings() {
		numIssuedWarnings--;
	}
	
	public void incNumErrors(){
		numIssuedErrors++;
	}

	public void decNumErrors() {
		numIssuedErrors--;
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
			if(currentIssue.getPriority() == 0 && !currentIssue.isSuppress())
				numWarningsPerSample++;
		}
		
		
		return numWarningsPerSample;
	}
	
	public int getNumIssuedErrors(TestSample sample) {
		int numErrorsPerSample = 0;
		
		if(allQualityIssuesLookup.get(sample) != null)
		for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
			if(currentIssue.getPriority() == 1 && !currentIssue.isSuppress())
				numErrorsPerSample++;
		}
		
		
		return numErrorsPerSample;
	}
	
	public QualityIssue getIssue(TestSample sample,String desc){
		if(allQualityIssuesLookup.get(sample) != null){
			for(QualityIssue currentIssue : allQualityIssuesLookup.get(sample)){
				if(currentIssue.getDescription().contains(desc))
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
		System.out.println(jsonArray.toString());
		
		return jsonArray;
	}
	
	public JSONArray getSampleIssues(String sampleID){
		ArrayList<QualityIssue> sampleQualityIssues = new ArrayList<QualityIssue>();
		for(QualityIssue currentIssue : allQualityIssues){
			if(currentIssue.getSampleID().equals(sampleID)&& !currentIssue.isSuppress())
			sampleQualityIssues.add(currentIssue);
		}
		JsonConfig conf = new JsonConfig();
		conf.setExcludes(new String[]{"sampleOfIssue"});
		JSONArray jsonArray = JSONArray.fromObject(sampleQualityIssues,conf);
		
		
		return jsonArray;
	}

	public QualityIssue doCorrection(int issueID,int correctionMethodID){
		QualityIssue issueToCorrect = issueLookup.get(issueID);
		if(issueToCorrect != null){
			issueToCorrect.doAutoCorrection(this,correctionMethodID);
			removeIssue(issueToCorrect);
		}
		return issueToCorrect;
	}

	public QualityIssue getIssueByID(int issueID){
		return issueLookup.get(issueID);
	}
	public void reevaluateRulesForSample(TestSample sampleToReevaluate) {
		rules.reevaluateRules(this,sampleToReevaluate);	
	}

	


}
