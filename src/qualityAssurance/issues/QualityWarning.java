package qualityAssurance.issues;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public class QualityWarning extends QualityIssue {

	public QualityWarning(QualityAssistent assistent,TestSample sampleOfIssue,String desciption, IssueType issue) {
		super(assistent,0, sampleOfIssue,desciption, issue);
		assistent.incNumWarnings();
	}
	public String toString(){
		return  "SAMPLE: " + sampleOfIssue.getSampleID() + " WARNING: " + description;
	}
	
	public void doAutoCorrection(QualityAssistent qualityAssistent,int correctionMethodID) {
		super.doAutoCorrection(qualityAssistent,correctionMethodID);
		qualityAssistent.decNumWarnings();
	}
}
