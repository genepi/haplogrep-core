package qualityAssurance;

import qualityAssurance.issues.QualityIssue;
import core.TestSample;

public class CorrectionMethod {

	String description;
	int methodID;
	QualityIssue issue;
	
	public CorrectionMethod(String description,int methodID,QualityIssue issue){
		this.description = description;
		this.methodID = methodID;
		this.issue = issue;
	}
	public void execute(TestSample sample){}
	public String getDescription() {
		return description;
	}
	public int getMethodID() {
		return methodID;
	}
	public int getIssueID() {
		return issue.getIssueID();
	};
	public boolean getLeaf() {
		return true;
	}
}
