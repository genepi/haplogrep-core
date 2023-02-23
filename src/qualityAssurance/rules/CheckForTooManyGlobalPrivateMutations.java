package qualityAssurance.rules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import core.Polymorphism;
import core.Reference;
import core.TestSample;
import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;

public class CheckForTooManyGlobalPrivateMutations extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyGlobalPrivateMutations.class);

	public CheckForTooManyGlobalPrivateMutations(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if (currentSample.getResults().size() != 0) {

			Reference ref = currentSample.getReference();
			SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
			int numGlobalPrivateMuations = 0;

			for (Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()) {
				if (!qualityAssistent.getUsedPhyloTree().isHotspot(currentRemainingPoly) && qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0
						&& !(currentRemainingPoly.equalsReference()))
					numGlobalPrivateMuations++;
			}

			if (numGlobalPrivateMuations > 3)
				qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample,
						"The sample contains " + numGlobalPrivateMuations + " global private " + "mutation(s) that are not known by Phylotree",
						IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub

	}

}
