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
}
