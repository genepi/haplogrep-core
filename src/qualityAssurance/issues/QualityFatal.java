package qualityAssurance.issues;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public class QualityFatal extends QualityIssue {

	public QualityFatal(QualityAssistent assistent,TestSample sampleOfIssue,String desciption, IssueType issue) {
		super(assistent,1,sampleOfIssue,desciption, issue);
		assistent.incNumErrors();
	}
	

	public String toString(){
		return  "SAMPLE: " + sampleOfIssue.getSampleID() + " ERROR: " + description;
	}
	
	public void doAutoCorrection(QualityAssistent qualityAssistent,int correctionMethodID) {
		super.doAutoCorrection(qualityAssistent,correctionMethodID);
		qualityAssistent.decNumErrors();
	}
}
