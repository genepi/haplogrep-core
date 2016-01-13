package qualityAssurance.rules;


import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;

import java.util.Vector;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;

public class CheckExpectedHGMatchesDetectedHG extends HaplogrepRule {

	public CheckExpectedHGMatchesDetectedHG(int priority) {
		super(priority);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		// Skip if no expected haplogroup was defined
		if (currentSample.getExpectedHaplogroup().equals(new Haplogroup("")))
			return;

		if(currentSample.getResults().size() != 0){
		Haplogroup detectedHg = currentSample.getResults().get(0).getHaplogroup();

		if (!currentSample.getExpectedHaplogroup().equals(detectedHg)) {

			if (!currentSample.getExpectedHaplogroup().isSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), detectedHg)) {

				qualityAssistent.addNewIssue(new QualityFatal(qualityAssistent, currentSample, "The expected haplogroup "
						+ currentSample.getExpectedHaplogroup() + " is no super group of the detected haplogroup " + detectedHg));
			} else {
				qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The detected haplogroup " + detectedHg
						+ " does not match the expected haplogroup " + currentSample.getExpectedHaplogroup() + " but represents a valid sub haplogroup"));
			}
		}
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}


}
