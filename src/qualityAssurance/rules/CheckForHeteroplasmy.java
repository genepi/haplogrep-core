package qualityAssurance.rules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityInfo;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForHeteroplasmy extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForHeteroplasmy.class);
	
	public CheckForHeteroplasmy(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(currentSample.getResults().size() != 0){
		SearchResult topResult = currentSample.getResults().get(0).getSearchResult();
		int numAlignWarning = 0;
		//log.debug("testsample  " + currentSample.getSampleID());
		for(Polymorphism currentRemainingPoly : currentSample.getSample().getPolymorphisms()){
			if(currentRemainingPoly.isHeteroplasmy())
				numAlignWarning++;
		}
		
		if(numAlignWarning > 0)
			qualityAssistent.addNewIssue(new QualityInfo(qualityAssistent, currentSample, "The sample contains " + numAlignWarning + " heteroplasmic position " 
	));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
