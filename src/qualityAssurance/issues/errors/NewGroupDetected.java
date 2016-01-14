package qualityAssurance.issues.errors;

import java.util.ArrayList;

import qualityAssurance.CorrectionMethod;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityIssue;
import qualityAssurance.issues.QualityWarning;


import core.Haplogroup;
import core.SampleRanges;
import core.TestSample;

public class NewGroupDetected extends QualityWarning {

//	int numberOfDifferences = 0;
//	ArrayList<TestSample> fragmentsReference = null;
//	ArrayList<TestSample> fragmentsSampleToCheck = null;
//	ArrayList<Haplogroup> referenceHaplogroups = null;
//	ArrayList<Haplogroup> currentSampleHaplogroups = null;
	
	Haplogroup remainingHaplogroup;
	double remainingQuality;

	
	class SetMataboRange extends CorrectionMethod
    {
      public SetMataboRange(int methodID,QualityIssue issue) {
			super("New haplogroup detected",methodID,issue);
		}

	public void execute(TestSample testSample)
      {
		testSample.getSample().getSampleRanges().clear();
		testSample.getSample().getSampleRanges().addMetaboChipRange();
      }
    }
	
//	public RecombinationDetected(QualityAssistent assistent,TestSample sampleOfIssue, int numberOfDifferences,
//								ArrayList<TestSample> fragmentsReference,ArrayList<TestSample> fragmentsSampleToCheck,
//								ArrayList<Haplogroup> referenceHaplogroups,ArrayList<Haplogroup> currentSampleHaplogroups) {
//		super(assistent, sampleOfIssue, "Possible recombiantion detected");
//		this.numberOfDifferences = numberOfDifferences;
//		this.fragmentsReference = fragmentsReference;
//		this.fragmentsSampleToCheck = fragmentsSampleToCheck;
//		this.referenceHaplogroups = referenceHaplogroups;
//		this.currentSampleHaplogroups = currentSampleHaplogroups;
//	}
	public NewGroupDetected(QualityAssistent assistent, TestSample sampleOfIssue,Haplogroup remainingHaplogroup,double remainingQuality) {
		super(assistent, sampleOfIssue, "New haplogroup detected", IssueType.RECOMB2);
		this.remainingHaplogroup = remainingHaplogroup;
		this.remainingQuality = remainingQuality;
	}

	public ArrayList<CorrectionMethod> getChildren(){
		return correctionMethods;
	}
	
	public void executeCorrectionMethodeByID(TestSample testSample,int methodID){
		correctionMethods.get(methodID).execute(testSample);
	}
	
	public String toString(){
		String result = "New Group detected " + " Sample " + getSampleOfIssue().getSampleID() + " - Remaining hg: " + remainingHaplogroup +" with quality "+ remainingQuality + "\r\n";
		return result;
	}
	
	public String getDescription() {
		return toString();
	}
//	public String toString(){
//		String result = "Sample " + getSampleOfIssue().getSampleID() + " - Detected differences: " + numberOfDifferences + "\r\n";
//		result += "Fragment\t";
//		for(int i = 0; i < referenceHaplogroups.size();i++){
//			result += i + "\t";
//		}
//		result += "\r\nHG Reference\t";
//		for(int i = 0; i < referenceHaplogroups.size();i++){
//			result += referenceHaplogroups.get(i) + "\t";
//		}
//		
//		result += "\r\nSample\t\t";
//		for(int i = 0; i < currentSampleHaplogroups.size();i++){
//			result += currentSampleHaplogroups.get(i) + "\t";
//		}
//		return result;
//	}
}
