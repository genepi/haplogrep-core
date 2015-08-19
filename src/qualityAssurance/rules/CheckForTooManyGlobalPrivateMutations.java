package qualityAssurance.rules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyGlobalPrivateMutations extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyGlobalPrivateMutations.class);
	
	public CheckForTooManyGlobalPrivateMutations(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numGlobalPrivateMuations = 0;

		
		for(Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()){
			if(!currentRemainingPoly.isMTHotspot() && qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0 && !(currentRemainingPoly.equalsReference()))
				numGlobalPrivateMuations++;
		}
		
		if(numGlobalPrivateMuations > 2)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains " + numGlobalPrivateMuations + " global private " +
					"mutation(s) that are not known by Phylotree"));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
