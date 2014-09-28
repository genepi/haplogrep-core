package qualityAssurance.rules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForAlignmentWarnings extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForAlignmentWarnings.class);
	
	public CheckForAlignmentWarnings(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numAlignWarning = 0;
		String alignmentProblem="";
		log.debug("testsample  " + currentSample.getSampleID());
		for(Polymorphism current : currentSample.getSample().getPolymorphisms()){
			if(current.isReliable()>0)
				{numAlignWarning++;
				alignmentProblem+=current+" ";
				}
		}
		
		if(numAlignWarning > 0)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "Alignment check: " + numAlignWarning + " position to recheck: "+alignmentProblem 
	));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
