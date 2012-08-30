package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;
import core.TestSample;

public interface HaplogrepRule {
	void evaluate(QualityAssistent qualityAssistent,TestSample currentSample);
}
