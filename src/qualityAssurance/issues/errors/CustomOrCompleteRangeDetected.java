package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityIssue;


import core.Polymorphism;
import core.TestSample;

public class CustomOrCompleteRangeDetected extends QualityFatal {

	class SetCompleteRange extends CorrectionMethod
    {
      public SetCompleteRange(int methodID,QualityIssue issue) {
			super("Change sample range to complete region",methodID,issue);
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
			super("Create custom range",methodID,issue);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		for(Polymorphism poly : testSample.getSample().getPolymorphisms())
			testSample.getSample().getSampleRanges().addCustomRange(poly.getPosition(), poly.getPosition());
      }
    }
	
	public CustomOrCompleteRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue) {
		super(assistent, sampleOfIssue, "Complete or custom range recognized", IssueType.RANGE);
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
