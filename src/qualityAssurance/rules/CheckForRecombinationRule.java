package qualityAssurance.rules;


import java.util.ArrayList;
import java.util.List;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.errors.RecombinationIssue;
import search.ranking.KylczynskiRanking;
import search.ranking.results.RankedResult;
import core.Haplogroup;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;

/**
 * @author Dominic
 *	Checks a given sample for recombination. 
 */
public class CheckForRecombinationRule extends HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();

	/**
	 * Custom fragment ranges to use. If NULL standard ranges are used
	 */
	SampleRanges customFragmentRanges = null;
	
	/**
	 * The number of haplogroups the sample may differ overall fragments
	 */
	int haplogroupTolerance;
	
	public CheckForRecombinationRule(int tolerance){
		super(0);
		this.haplogroupTolerance =tolerance;
	}
	
	public CheckForRecombinationRule(int tolerance,SampleRanges customFragmentRanges){
		super(0);
		this.customFragmentRanges = customFragmentRanges;
		this.haplogroupTolerance = tolerance;
	}
	
	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample sampleToCheck) {
		boolean passedTest = true;
		
		if(!sampleToCheck.getSample().getSampleRanges().isCustomRange()){
			//Create sample of the reference haplogroup
			List<RankedResult> resultSampleToCheck = qualityAssistent.getUsedPhyloTree().search(sampleToCheck, new KylczynskiRanking(1));		
			ArrayList<Polymorphism> haplogroupDefiningPolys = resultSampleToCheck.get(0).getSearchResult().getDetailedResult().getExpectedPolys();	
			TestSample haplogroupReferenceSample = new TestSample("hgReference", haplogroupDefiningPolys, sampleToCheck.getSample().getSampleRanges());
			
			
			//Use standard fragments ranges for complete and control range sample if no custom range is given.
			SampleRanges ranges = customFragmentRanges;
			if(customFragmentRanges == null){
				 ranges = new SampleRanges();
				if(sampleToCheck.getSample().getSampleRanges().isCompleteRange()){
					ranges.addCustomRange(2488, 10858);
					ranges.addCustomRange(10898, 2687);
				}
				
				else if(sampleToCheck.getSample().getSampleRanges().isControlRange()){
					ranges.addCustomRange(16024, 16579);
					ranges.addCustomRange(1,  576);
				}
			}
			//Create fragments and determine their respective haplogroups
			ArrayList<TestSample> fragmentsReference = haplogroupReferenceSample.createFragments(ranges);
			ArrayList<TestSample> fragmentsSampleToCheck = sampleToCheck.createFragments(ranges);
			ArrayList<Haplogroup> referenceHaplogroups = new ArrayList<Haplogroup>();
			ArrayList<Haplogroup> currentSampleHaplogroups = new ArrayList<Haplogroup>();
			
			for(TestSample currentFragment : fragmentsReference){
				referenceHaplogroups.add(
				qualityAssistent.getUsedPhyloTree().search(currentFragment, new KylczynskiRanking(1)).get(0).getHaplogroup());
			}
			
			for(TestSample currentFragment : fragmentsSampleToCheck){
				currentSampleHaplogroups.add(
				qualityAssistent.getUsedPhyloTree().search(currentFragment, new KylczynskiRanking(1)).get(0).getHaplogroup());
			}
			
			//Calculates the number of groups between reference and sample for each fragment and sums it up.
			int overallDistance = 0;
			for(int i = 0; i < referenceHaplogroups.size();i++){
				overallDistance += qualityAssistent.getUsedPhyloTree().getDistanceBetweenHaplogroups(currentSampleHaplogroups.get(i),referenceHaplogroups.get(i));
			}
			
			//If there differences (even with tolerance) create a recombination issue
			if(overallDistance > haplogroupTolerance){
				qualityAssistent.addNewIssue(new RecombinationIssue(qualityAssistent, sampleToCheck,
						overallDistance,referenceHaplogroups,currentSampleHaplogroups));
			
				passedTest = true;
			}
		}
		
		//Increase quality level if no recombination has been found
		if(passedTest)
			sampleToCheck.setReachedQualityLevel(this.getPriority() + 1);
	}
	
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {

	}
}
