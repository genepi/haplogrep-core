package qualityAssurance.issues;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;

import core.TestSample;

public class QualityIssue implements Comparable<QualityIssue>{
	int issueID;
	int priority;
	String description;
	TestSample sampleOfIssue;
	boolean suppress = false;
	protected ArrayList<CorrectionMethod> correctionMethods = new ArrayList<CorrectionMethod>();
	
	public QualityIssue(QualityAssistent qualityAssistent,int priority,TestSample sampleofIssue, String description){
		this.issueID = qualityAssistent.getNewIssueID();
		this.priority = priority;
		this.sampleOfIssue = sampleofIssue;
		this.description = description;
	}

	@Override
	public int compareTo(QualityIssue o) {
		return priority - o.priority;
	}

	public String getDescription() {
		return description;
	}

	public Integer getIssueID() {
		return issueID;
	}

	public TestSample getSampleOfIssue() {
		return sampleOfIssue;
	}

	public int getPriority() {
		return priority;
	}
	public String getSampleID() {
		return sampleOfIssue.getSampleID();
	}

	public boolean isSuppress() {
		return suppress;
	}

	public void setSuppress(boolean suppress) {
		this.suppress = suppress;
	}
	
	public ArrayList<CorrectionMethod> getChildren(){
		return correctionMethods;
	}

	public void doAutoCorrection(QualityAssistent qualityAssistent,int correctionMethodID) {
		if(correctionMethods.size() > correctionMethodID){
			correctionMethods.get(correctionMethodID).execute(sampleOfIssue);
		}
	}
	
	public boolean getLeaf() {
		return correctionMethods.isEmpty();
	}
	public String getIconCls() {
		return "icon-show-rules";
	}
}
