package qualityAssurance.issues;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public class QualityInfo extends QualityIssue {

	public QualityInfo(QualityAssistent assistent,TestSample sampleOfIssue,String desciption, IssueType issue) {
		super(assistent,2, sampleOfIssue,desciption, issue);
	}
	public String toString(){
		return  "SAMPLE: " + sampleOfIssue.getSampleID() + " INFO: " + description;
	}
	

}
