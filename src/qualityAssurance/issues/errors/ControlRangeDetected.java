package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityError;


import core.Polymorphism;
import core.TestSample;

public class ControlRangeDetected extends QualityError {

	class SetCompleteRange extends CorrectionMethod
    {
      public SetCompleteRange(int methodID) {
			super("Change to metabo chip sample range",methodID);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addCompleteRange();
      }
    }
	
	class SetCustomRange extends CorrectionMethod
    {
      public SetCustomRange(int methodID) {
			super("Change to metabo chip sample range",methodID);
		}

	public void execute(TestSample testSample)
      {
		for(Polymorphism poly : testSample.getSample().getPolymorphisms())
			testSample.getSample().getSampleRanges().addCustomRange(poly.getPosition(), poly.getPosition());
      }
    }
	
	public ControlRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue) {
		super(assistent, sampleOfIssue, "Complete or custom range recognized");
		correctionMethods.add(new SetCompleteRange(correctionMethods.size()));
		correctionMethods.add(new SetCustomRange(correctionMethods.size()));
	}

	public ArrayList<CorrectionMethod> getCorrectionMethods(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
