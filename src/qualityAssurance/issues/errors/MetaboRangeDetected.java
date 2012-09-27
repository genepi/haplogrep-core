package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityError;


import core.TestSample;

public class MetaboRangeDetected extends QualityError {

	class SetMataboRange extends CorrectionMethod
    {
      public SetMataboRange(int methodID) {
			super("Change to metabo chip sample range",methodID);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addMetaboChipRange();
      }
    }
	
	public MetaboRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue) {
		super(assistent, sampleOfIssue, "MetaboChip range detected but does not match the indicated range");
		correctionMethods.add(new SetMataboRange(correctionMethods.size()));
	}

	public ArrayList<CorrectionMethod> getCorrectionMethods(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
