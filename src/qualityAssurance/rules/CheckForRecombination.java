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
import qualityAssurance.issues.errors.RecombinationDetected;
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

public class CheckForRecombination extends HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
	//static HashSet<Integer> metaboChipPositions = null;
	boolean isMetaboChip = true;
	int windowSize;
	
	public CheckForRecombination(int windowSize){
		super(0);
		this.windowSize= windowSize;
	}
	
	

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		List<RankedResult> result = qualityAssistent.getUsedPhyloTree().search(currentSample, new KylczynskiRanking(1));	
		
		System.out.println(currentSample.getSampleID() + " Org " + result.get(0).getHaplogroup() + " qual " + result.get(0).getDistance() + " sample " + ((KylczynskiResult)result.get(0)).getCorrectPolyInTestSampleRatio());
		
		if(result.get(0).getDistance() < 0.9){
		ArrayList<Polymorphism> haplogroupDefiningPolys = result.get(0).getSearchResult().getDetailedResult().getRemainingPolysInSample();
		
		
		
		SampleRanges ranges = new SampleRanges();
		for(Polymorphism currentPoly : haplogroupDefiningPolys){
			ranges.addCustomRange(currentPoly.getPosition(), currentPoly.getPosition());
		}
		TestSample haplogroupReferenceSample = new TestSample("hgRemaining", haplogroupDefiningPolys, currentSample.getSample().getSampleRanges());
		
//		SampleRanges ranges = new SampleRanges();
//		int i2 = 0 ;
//		for( i2 = 0; i2 < 16569 / windowSize;i2++){
//			ranges.addCustomRange(windowSize*i2, windowSize*(i2+1)-1);
//		}
//		ranges.addCustomRange(windowSize*(i2), 16569);
		
		
		RankedResult l = qualityAssistent.getUsedPhyloTree().search(haplogroupReferenceSample, new KylczynskiRanking(1)).get(0);
		System.out.println("Remaining haplogroup "+  l.getSearchResult().getHaplogroup());
		for(Polymorphism currentPoly : haplogroupDefiningPolys)
			System.out.println(currentPoly + " ");
				
//		String groupStart = result.get(0).getHaplogroup().toString().substring(0, Math.min(2, result.get(0).getHaplogroup().toString().length()));
//		String remainStart = l.getHaplogroup().toString().substring(0, Math.min(2,l.getHaplogroup().toString().length()));
//		
//		if(groupStart.equals(remainStart)){
//			qualityAssistent.addNewIssue(new NewGroupDetected(qualityAssistent, currentSample,l.getHaplogroup(),l.getDistance()));
//		}
//		else
			qualityAssistent.addNewIssue(new RecombinationDetected(qualityAssistent, currentSample,l.getHaplogroup(),l.getDistance()));
		}
		
		else
		currentSample.setReachedQualityLevel(this.getPriority() + 1);
	}

//	@Override
//	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
//		
//		List<RankedResult> result = qualityAssistent.getUsedPhyloTree().search(currentSample, new HammingRanking(1));	
//		
//		
//		ArrayList<Polymorphism> haplogroupDefiningPolys = result.get(0).getSearchResult().getDetailedResult().getExpectedPolys();
//		System.out.println("Recomb haplogroup "+  result.get(0).getSearchResult().getHaplogroup());
//		TestSample haplogroupReferenceSample = new TestSample("hgReference", haplogroupDefiningPolys, currentSample.getSample().getSampleRanges());
//		
//		SampleRanges ranges = new SampleRanges();
//		int i2 = 0 ;
//		for( i2 = 0; i2 < 16569 / windowSize;i2++){
//			ranges.addCustomRange(windowSize*i2, windowSize*(i2+1)-1);
//		}
//		ranges.addCustomRange(windowSize*(i2), 16569);
//		
//		ArrayList<TestSample> fragmentsReference = haplogroupReferenceSample.createFragments(ranges);
//		ArrayList<TestSample> fragmentsSampleToCheck = currentSample.createFragments(ranges);
//		ArrayList<Haplogroup> referenceHaplogroups = new ArrayList<Haplogroup>();
//		ArrayList<Haplogroup> currentSampleHaplogroups = new ArrayList<Haplogroup>();
//		
//		for(TestSample currentFragment : fragmentsReference){
//			referenceHaplogroups.add(
//			qualityAssistent.getUsedPhyloTree().search(currentFragment, new HammingRanking(1)).get(0).getHaplogroup());
//		}
//		
//		for(TestSample currentFragment : fragmentsSampleToCheck){
//			currentSampleHaplogroups.add(
//			qualityAssistent.getUsedPhyloTree().search(currentFragment, new HammingRanking(1)).get(0).getHaplogroup());
//		}
//		
//		int numberOfDifferences = 0;
//		
//		for(int i = 0; i < referenceHaplogroups.size();i++){
//			if(!referenceHaplogroups.get(i).equals(currentSampleHaplogroups.get(i))){
//				numberOfDifferences++;
//			}
//		}
//		
//		if(numberOfDifferences > 0)
//			qualityAssistent.addNewIssue(new RecombinationDetected(qualityAssistent, currentSample,
//					numberOfDifferences,fragmentsReference,fragmentsSampleToCheck,referenceHaplogroups,currentSampleHaplogroups));
//		
//		currentSample.setReachedQualityLevel(this.getPriority() + 1);
//	}
	
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
