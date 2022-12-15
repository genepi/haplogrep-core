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

public class CheckForTooManyLocalPrivateMutations extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyLocalPrivateMutations.class);

	public CheckForTooManyLocalPrivateMutations(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		Reference ref = currentSample.getReference();
		if (currentSample.getResults().size() != 0) {
			SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
			int numLocalPrivateMuations = 0;

			for (Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()) {

				if (!currentRemainingPoly.isMTHotspot(ref) && !(qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0)
						&& !(currentRemainingPoly.equalsReference())) {
					numLocalPrivateMuations++;
				}
			}

			if (numLocalPrivateMuations > 1)
				qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample,
						"The sample contains " + numLocalPrivateMuations + " local private " + "mutation(s) associated with other Haplogroups ",
						IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub

	}

}
