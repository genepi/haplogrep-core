package qualityAssurance.rules;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyLocalPrivateMutations extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyLocalPrivateMutations.class);
	
	public CheckForTooManyLocalPrivateMutations(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numLocalPrivateMuations = 0;
		StringBuffer sb = new StringBuffer();
		
		
		log.debug("testsample  " + currentSample.getSampleID());
		for(Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()){
			if(!currentRemainingPoly.isMTHotspot() && !(qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0) && !(currentRemainingPoly.equalsReference()))
				{numLocalPrivateMuations++;
				sb.append(currentRemainingPoly.toString()+" ");
				}
		}
		
		if(numLocalPrivateMuations > 1)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains " + numLocalPrivateMuations + " local private " +
					"mutation(s) associated with other Haplogroups: " + sb.toString()));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
