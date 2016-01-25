package qualityAssurance.rules;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityInfo;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Mutations;
import core.Polymorphism;
import core.TestSample;

public class CheckForTooManyN extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyN.class);
	
	public CheckForTooManyN(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numAlignWarning = 0;
		StringBuffer listHeteroplasmy = new StringBuffer();
		
		for(Polymorphism currentRemainingPoly : currentSample.getSample().getPolymorphisms()){
			if(currentRemainingPoly.getMutation().equals(Mutations.N)){
			listHeteroplasmy.append(currentRemainingPoly+"\t");
				numAlignWarning++;
			}
		}
		
		if(numAlignWarning == 1)
			qualityAssistent.addNewIssue(new QualityInfo(qualityAssistent, currentSample, "The sample contains " + numAlignWarning + " undetermined variant: " + listHeteroplasmy, IssueType.ALIGN)); 
		else if(numAlignWarning > 1)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains " + numAlignWarning + " undetermined variants: " + listHeteroplasmy, IssueType.ALIGN)); 
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
