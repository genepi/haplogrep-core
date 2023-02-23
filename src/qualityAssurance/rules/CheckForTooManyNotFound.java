package qualityAssurance.rules;

import java.text.DecimalFormat;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyNotFound extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyNotFound.class);
	
	public CheckForTooManyNotFound(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		
		double missedPolysInPercentage = (double) topResult.getDetailedResult().getFoundNotFoundPolysArray().size() / topResult.getDetailedResult().getExpectedPolys().size();
		
		if(missedPolysInPercentage >= 0.5)
			qualityAssistent.addNewIssue(new QualityFatal(qualityAssistent, currentSample, "The sample misses " + new DecimalFormat("#0.00").format(missedPolysInPercentage) + " % of the exepected polymorphisms. ", IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
