package qualityAssurance.rules;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityWarning;
import qualityAssurance.issues.errors.AlignmentInfo;
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

		int numAlignWarning = 0;
		String alignmentProblem="";
		for(Polymorphism current : currentSample.getSample().getPolymorphisms()){
			if(current.isReliable()>0)
				{numAlignWarning++;
				alignmentProblem+=current+" ("+current.isReliable()+") " ;
				}
		}
		
		if(numAlignWarning > 0)
			qualityAssistent.addNewIssue(new AlignmentInfo(qualityAssistent, currentSample, "Alignment check: " + numAlignWarning + " position to recheck: "+alignmentProblem 
	));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
