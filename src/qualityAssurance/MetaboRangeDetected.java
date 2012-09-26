package qualityAssurance;

import java.util.ArrayList;

import net.sf.json.JSONArray;

import core.TestSample;

public class MetaboRangeDetected extends QualityError {

	class SetMataboRange extends CorrectionMethod
    {
      public SetMataboRange(int methodID) {
			super("Change to metabo chip sample range",methodID);
		}

	void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addMetaboChipRange();
      }
    }
	
	public MetaboRangeDetected(QualityAssistent assistent, TestSample sampleOfIssue, String desciption) {
		super(assistent, sampleOfIssue, desciption);
		correctionMethods.add(new SetMataboRange(correctionMethods.size()));
	}

//	public JSONArray getCorrectionMethodsAsJson(){
//		JSONArray jsonMethods = JSONArray.fromObject(correctionMethods);
//		return jsonMethods;
//	}
	public ArrayList<CorrectionMethod> getCorrectionMethods(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
}
