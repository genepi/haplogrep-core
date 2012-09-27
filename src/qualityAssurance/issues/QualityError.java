package qualityAssurance.issues;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public class QualityError extends QualityIssue {

	public QualityError(QualityAssistent assistent,TestSample sampleOfIssue,String desciption) {
		super(assistent,1,sampleOfIssue,desciption);
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
