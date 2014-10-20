package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityIssue;
import qualityAssurance.issues.QualityWarning;
import core.TestSample;

public class AlignmentInfo extends QualityWarning {

	class TestCorrectionMethod extends CorrectionMethod
    {
      public TestCorrectionMethod(int methodID,QualityIssue issue) {
			super("Test",methodID,issue);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addControlRange();
      }
    }
	
	public AlignmentInfo(QualityAssistent assistent, TestSample sampleOfIssue,String id) {
		super(assistent, sampleOfIssue,id);
		correctionMethods.add(new TestCorrectionMethod(correctionMethods.size(),this));
	}

	public ArrayList<CorrectionMethod> getChildren(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
