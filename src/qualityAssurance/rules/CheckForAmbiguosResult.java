package qualityAssurance.rules;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import search.ranking.results.KylczynskiResult;
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
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		SearchResult secondResult= null;
		if (currentSample.getResults().size()>2)
			secondResult = currentSample.getResults().get(1).getSearchResult();
		
		if (topResult.getSumWeightsAllPolysSample() == secondResult.getSumWeightsAllPolysSample() && topResult.getWeightFoundPolys() == secondResult.getWeightFoundPolys() && topResult.getWeightRemainingPolys() == secondResult.getWeightRemainingPolys())
				qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample shows ambigous best results  " + topResult.getHaplogroup() + " / " + secondResult.getHaplogroup(), IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
