package qualityAssurance;

import qualityAssurance.issues.QualityIssue;
import core.TestSample;

public class CorrectionMethod {

	String desc;
	int methodID;
	QualityIssue issue;
	
	public CorrectionMethod(String description,int methodID,QualityIssue issue){
		this.desc = description;
		this.methodID = methodID;
		this.issue = issue;
	}
	public void execute(TestSample sample){}
	public String getDesc() {
		return desc;
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
