package qualityAssurance.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.IssueType;
import qualityAssurance.issues.QualityInfo;
import qualityAssurance.issues.QualityIssue;
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


		for(Polymorphism currentRemainingPoly : topResult.getDetailedResult().getRemainingPolysInSample()){

			if(!currentRemainingPoly.isMTHotspot() && !(qualityAssistent.getUsedPhyloTree().getMutationRate(currentRemainingPoly) == 0) && !(currentRemainingPoly.equalsReference()))
				{
				numLocalPrivateMuations++;
		}
		}
		
		if(numLocalPrivateMuations > 1)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains " + numLocalPrivateMuations + " local private " +
					"mutation(s) associated with other Haplogroups ", IssueType.QUAL));
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

	
}
