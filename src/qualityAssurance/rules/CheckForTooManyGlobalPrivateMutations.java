package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;
import qualityAssurance.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyGlobalPrivateMutations implements HaplogrepRule {

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numGlobalPrivateMuations = 0;
		for(Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()){
			if(!currentRemainingPoly.isMTHotspot() && qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0)
				numGlobalPrivateMuations++;
		}
		
		if(numGlobalPrivateMuations > 0)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains " + numGlobalPrivateMuations + " global private " +
					"mutation(s) that are not known by phylotree"));

		
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
