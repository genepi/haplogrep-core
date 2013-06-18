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
//	static HashSet<Integer> metaboChipPositions = null;
//	boolean isMetaboChip = true;
	int windowSize;
	
	public CheckForRecombinationWindow(int windowSize){
		super(0);
		this.windowSize= 2000;
	}
	
	

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		List<RankedResult> result = qualityAssistent.getUsedPhyloTree().search(currentSample, new KylczynskiRanking(1));	
		
		
		ArrayList<Polymorphism> haplogroupDefiningPolys = result.get(0).getSearchResult().getDetailedResult().getExpectedPolys();
		System.out.println("Recomb haplogroup "+  result.get(0).getSearchResult().getHaplogroup());
		TestSample haplogroupReferenceSample = new TestSample("hgReference", haplogroupDefiningPolys, currentSample.getSample().getSampleRanges());
		
		SampleRanges ranges = new SampleRanges();
		if(currentSample.getSample().getSampleRanges().isCompleteRange()){
			ranges.addCustomRange(2488, 10858);
			ranges.addCustomRange(10898, 2687);
		}
		
		else if(currentSample.getSample().getSampleRanges().isControlRange()){
//			//HSV1 range
//			ranges.addCustomRange(16024, 16383);
//			//Control Range
//			ranges.addCustomRange(16384,  56);
//			
//			//HSV 2
//			ranges.addCustomRange(57,  437);
//			
//			//HSV 3
//			ranges.addCustomRange(438,  576);
			
			ranges.addCustomRange(16024, 16579);
//			//Control Range
			ranges.addCustomRange(1,  576);
		}
		
		else{
			currentSample.setReachedQualityLevel(this.getPriority() + 1);
			return;
		}
			
//		int i2 = 0 ;
//		for( i2 = 0; i2 < 16569 / windowSize;i2++){
//			ranges.addCustomRange(windowSize*i2, windowSize*(i2+1)-1);
//		}
//		ranges.addCustomRange(windowSize*(i2), 16569);
		
		
		
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
		
		int numberOfDifferences = 0;
		int distanceToSuperHaplogroup = -1;
		int inverseDistanceToSuperHaplogroup = -1;
		for(int i = 0; i < referenceHaplogroups.size();i++){
//			if(distanceToSuperHaplogroup == -1)
			distanceToSuperHaplogroup = currentSampleHaplogroups.get(i).distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), referenceHaplogroups.get(i));
//			if(inverseDistanceToSuperHaplogroup == -1)
			inverseDistanceToSuperHaplogroup = referenceHaplogroups.get(i).distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), currentSampleHaplogroups.get(i));
//			
			
//			if(!referenceHaplogroups.get(i).equals(currentSampleHaplogroups.get(i))){
			if (inverseDistanceToSuperHaplogroup == -1 || inverseDistanceToSuperHaplogroup > 2){
				numberOfDifferences++;
			}
		}
		
//		Haplogroup currentParentGroup = currentSampleHaplogroups.get(0);
		
//		boolean recombinationDetected = false;
		
//		for (int i = 1; i < currentSampleHaplogroups.size(); i++) {
//			distanceToSuperHaplogroup = currentSampleHaplogroups.get(i).distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), currentParentGroup);
//			inverseDistanceToSuperHaplogroup = currentParentGroup.distanceToSuperHaplogroup(qualityAssistent.getUsedPhyloTree(), currentSampleHaplogroups.get(i));
//			if (distanceToSuperHaplogroup > -1 && distanceToSuperHaplogroup < 4) {
//				currentParentGroup = currentSampleHaplogroups.get(i);
//			}
//
//			else if (inverseDistanceToSuperHaplogroup == -1 || inverseDistanceToSuperHaplogroup >= 4) {
//				recombinationDetected = true;
//				break;
//			}
//		}
		
		if(numberOfDifferences != 0)
			qualityAssistent.addNewIssue(new RecombinationDetectedWindow(qualityAssistent, currentSample,
					numberOfDifferences,fragmentsReference,fragmentsSampleToCheck,referenceHaplogroups,currentSampleHaplogroups,
					distanceToSuperHaplogroup,inverseDistanceToSuperHaplogroup));
		
		currentSample.setReachedQualityLevel(this.getPriority() + 1);
	}
	
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
//		if(isMetaboChip){
//			QualityIssue issue = qualityAssistent.getIssue(currentSample,"Common rCRS polymorphim (263G 8860G or 15326G)");
//			if(issue != null){
//				issue.setSuppress(true);
//			}
//			
//			issue = qualityAssistent.getIssue(currentSample,"common RSRS polymorphims found!");
//			if(issue != null){
//				issue.setSuppress(true);
//			}
//		}
	}
	
	
	

}
