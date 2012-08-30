package qualityAssurance;

import core.TestSample;

public class QualityWarning extends QualityIssue {

	public QualityWarning(QualityAssistent assistent,TestSample sampleOfIssue,String desciption) {
		super(0, sampleOfIssue,desciption);
		assistent.incNumWarnings();
	}
	public String toString(){
		return  "SAMPLE: " + sampleOfIssue.getSampleID() + " WARNING: " + description;
	}
}
