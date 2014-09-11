package qualityAssurance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.issues.QualityIssue;
import core.SampleFile;
import core.TestSample;

public class CorrectionMethod {

	final Log log = LogFactory.getLog(CorrectionMethod.class);
	
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
	public String getIconCls() {
		return "icon-apply-rules";
	}
	
	
}
