package qualityAssurance.rules;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityInfo;
import qualityAssurance.issues.QualityWarning;
import search.ClusteredSearchResults;
import search.SearchResult;
import search.ranking.results.KylczynskiResult;
import search.ranking.results.RankedResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForAmbiguosResult extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForAmbiguosResult.class);
	
	public CheckForAmbiguosResult(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		 ArrayList<RankedResult> topResult = currentSample.getClusteredSearchResultsAsObject().getCluster(currentSample.getTopResult().getHaplogroup());
		
	if (topResult.size()>1)
			qualityAssistent.addNewIssue(new QualityInfo(qualityAssistent, currentSample, "The sample shows ambigous best results "  , IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
