package qualityAssurance.rules;



import java.text.DecimalFormat;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import search.SearchResult;
import core.Polymorphism;
import core.TestSample;

public class CheckForQuality extends HaplogrepRule {

	static final Log log = LogFactory.getLog(CheckForTooManyGlobalPrivateMutations.class);
	
	public CheckForQuality(int priority) {
		super(priority);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
	
		if(currentSample.getResults().size()!=0){
			
		double topResult = currentSample.getResults().get(0).getDistance();
	
		 if (topResult <= 0.9)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The detected haplogroup quality " + new DecimalFormat("#0.00").format(topResult)+" is below 0.9.", IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
