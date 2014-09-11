package qualityAssurance.rules;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForQuality extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyGlobalPrivateMutations.class);
	
	public CheckForQuality(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
	
		if(currentSample.getResults().size()!=0){
		double topResult = currentSample.getResults().get(0).getDistance();
		
		if(topResult <= 0.9 & topResult > 0.8)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample quality " + topResult + " is low "));
		else if (topResult <= 0.8)
			qualityAssistent.addNewIssue(new QualityFatal(qualityAssistent, currentSample, "The sample quality " + topResult + " is very low "));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}