package qualityAssurance.issues;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public class QualityInfo extends QualityIssue {

	public QualityInfo(QualityAssistent assistent,TestSample sampleOfIssue,String desciption) {
		super(assistent,2, sampleOfIssue,desciption);
	}
	public String toString(){
		return  "SAMPLE: " + sampleOfIssue.getSampleID() + " INFO: " + description;
	}
	

}
