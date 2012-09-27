package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityError;


import core.TestSample;

public class CustomOrCompleteRangeDetected extends QualityError {

	class SetControlRange extends CorrectionMethod
    {
      public SetControlRange(int methodID) {
			super("Change to metabo chip sample range",methodID);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addControlRange();
      }
    }
	
	public CustomOrCompleteRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue) {
		super(assistent, sampleOfIssue, "Control range recognized");
		correctionMethods.add(new SetControlRange(correctionMethods.size()));
	}

	public ArrayList<CorrectionMethod> getCorrectionMethods(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
