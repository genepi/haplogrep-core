package qualityAssurance.rules;

import com.sun.org.apache.bcel.internal.generic.NEW;

import qualityAssurance.QualityAssistent;
import qualityAssurance.QualityError;
import qualityAssurance.QualityWarning;
import core.Haplogroup;
import core.TestSample;

public class CheckExpectedHGMatchesDetectedHG implements HaplogrepRule {

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		// Skip if no expected haplogroup was defined
		if (currentSample.getExpectedHaplogroup().equals(new Haplogroup("")))
			return;

		Haplogroup detectedHg = currentSample.getResults().get(0).getHaplogroup();

		if (!currentSample.getExpectedHaplogroup().equals(detectedHg)) {

			if (!currentSample.getExpectedHaplogroup().isSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), detectedHg)) {

				qualityAssistent.addNewIssue(new QualityError(qualityAssistent, currentSample, "The expected haplogroup "
						+ currentSample.getExpectedHaplogroup() + " is no super group of the detected haplogroup " + detectedHg));
			} else {
				qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The detected haplogroup " + detectedHg
						+ " does not match the expected haplogroup " + currentSample.getExpectedHaplogroup() + " but represents a valid sub haplogroup"));
			}
		}

	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
