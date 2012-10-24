package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityIssue;


import core.Polymorphism;
import core.TestSample;

public class ControlRangeDetected extends QualityFatal {

	class SetCompleteRange extends CorrectionMethod
    {
      public SetCompleteRange(int methodID,QualityIssue issue) {
			super("Change to metabo chip sample range",methodID,issue);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addCompleteRange();
      }
    }
	
	class SetCustomRange extends CorrectionMethod
    {
      public SetCustomRange(int methodID,QualityIssue issue) {
			super("Change to metabo chip sample range",methodID,issue);
		}

	public void execute(TestSample testSample)
      {
		for(Polymorphism poly : testSample.getSample().getPolymorphisms())
			testSample.getSample().getSampleRanges().addCustomRange(poly.getPosition(), poly.getPosition());
      }
    }
	
	public ControlRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue) {
		super(assistent, sampleOfIssue, "Complete or custom range recognized");
		correctionMethods.add(new SetCompleteRange(correctionMethods.size(),this));
		correctionMethods.add(new SetCustomRange(correctionMethods.size(),this));
	}

	public ArrayList<CorrectionMethod> getChildren(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
