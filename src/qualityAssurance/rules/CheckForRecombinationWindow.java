package qualityAssurance.rules;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityIssue;
import qualityAssurance.issues.errors.CustomOrCompleteRangeDetected;
import qualityAssurance.issues.errors.ControlRangeDetected;
import qualityAssurance.issues.errors.MetaboRangeDetected;
import qualityAssurance.issues.errors.NewGroupDetected;
import qualityAssurance.issues.errors.RecombinationDetectedWindow;
import search.ranking.HammingRanking;
import search.ranking.KylczynskiRanking;
import search.ranking.RankingMethod;
import search.ranking.results.KylczynskiResult;
import search.ranking.results.RankedResult;
import core.Haplogroup;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForRecombinationWindow extends HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();

	SampleRanges customFragmentRanges = null;
	
	
	public CheckForRecombinationWindow(){
		super(0);
	}
	
	public CheckForRecombinationWindow(SampleRanges customFragmentRanges){
		super(0);
		this.customFragmentRanges= customFragmentRanges;
	}
	
	

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		//Create sample of the reference haplogroup
		List<RankedResult> result = qualityAssistent.getUsedPhyloTree().search(currentSample, new KylczynskiRanking(1));		
		ArrayList<Polymorphism> haplogroupDefiningPolys = result.get(0).getSearchResult().getDetailedResult().getExpectedPolys();	
		TestSample haplogroupReferenceSample = new TestSample("hgReference", haplogroupDefiningPolys, currentSample.getSample().getSampleRanges());
		
		
		//Use standard fragments ranges for complete and control range sample if no custom range is given.
		SampleRanges ranges = customFragmentRanges;
		if(customFragmentRanges == null){
			 ranges = new SampleRanges();
			if(currentSample.getSample().getSampleRanges().isCompleteRange()){
				ranges.addCustomRange(2488, 10858);
				ranges.addCustomRange(10898, 2687);
			}
			
			else if(currentSample.getSample().getSampleRanges().isControlRange()){
				ranges.addCustomRange(16024, 16579);
				ranges.addCustomRange(1,  576);
			}
			
			else{
				currentSample.setReachedQualityLevel(this.getPriority() + 1);
				return;
			}
		}
			
		//Create fragments and determine their respective haplogroups
		ArrayList<TestSample> fragmentsReference = haplogroupReferenceSample.createFragments(ranges);
		ArrayList<TestSample> fragmentsSampleToCheck = currentSample.createFragments(ranges);
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
		
//		int numberOfDifferences = 0;
//		int distanceToSuperHaplogroup = -1;
//		int inverseDistanceToSuperHaplogroup = -1;
//		for(int i = 0; i < referenceHaplogroups.size();i++){
////			if(distanceToSuperHaplogroup == -1)
//			distanceToSuperHaplogroup = currentSampleHaplogroups.get(i).distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), referenceHaplogroups.get(i));
////			if(inverseDistanceToSuperHaplogroup == -1)
//			inverseDistanceToSuperHaplogroup = referenceHaplogroups.get(i).distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), currentSampleHaplogroups.get(i));
////			
//			
////			if(!referenceHaplogroups.get(i).equals(currentSampleHaplogroups.get(i))){
//			if (inverseDistanceToSuperHaplogroup == -1 || inverseDistanceToSuperHaplogroup > 2){
//				numberOfDifferences++;
//			}
//		}
		int overallDistance = 0;
		int distance = 0;
		for(int i = 0; i < referenceHaplogroups.size();i++){

			distance += qualityAssistent.getUsedPhyloTree().getDistanceBetweenHaplogroups(currentSampleHaplogroups.get(i),referenceHaplogroups.get(i));
			
//			if (distance < 2){
				overallDistance += distance;
//			}
		}
		//If there differences (even with tolerance) create a recombination issue
		if(overallDistance > 4)
			qualityAssistent.addNewIssue(new RecombinationDetectedWindow(qualityAssistent, currentSample,
					overallDistance,fragmentsReference,fragmentsSampleToCheck,referenceHaplogroups,currentSampleHaplogroups,
					overallDistance,0));
		
		currentSample.setReachedQualityLevel(this.getPriority() + 1);
	}
	
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {

	}
}
