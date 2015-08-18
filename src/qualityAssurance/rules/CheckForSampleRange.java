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
import java.util.HashSet;
import java.util.Scanner;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityIssue;
import qualityAssurance.issues.errors.CustomOrCompleteRangeDetected;
import qualityAssurance.issues.errors.ControlRangeDetected;
import qualityAssurance.issues.errors.MetaboRangeDetected;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRange extends HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
	//static HashSet<Integer> metaboChipPositions = null;
	boolean isMetaboChip = true;
	
	public CheckForSampleRange(int priority){
		super(priority);

	}
	
	

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
			
		
		isMetaboChip = true;
		SampleRanges metaboChipRange = new SampleRanges();
		metaboChipRange.addMetaboChipRange();
		for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
			if(!metaboChipRange.contains(currentPoly)){
					isMetaboChip = false;
					break;
			}
		}
		boolean isControlRange = false;
		if(!isMetaboChip){
			isControlRange = true;
			SampleRanges controlRange = new SampleRanges();
			controlRange.addControlRange();
			
			for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
				if(!controlRange.contains(currentPoly)){
					isControlRange = false;
					break;
				}
			}
		}
		
		boolean isCompleteRange = false;
		if(!isMetaboChip && !isControlRange){
			isCompleteRange = true;
//			SampleRanges controlRange = new SampleRanges();
//			controlRange.addControlRange();
//			
//			for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
//				if(!controlRange.contains(currentPoly)){
//					isCompleteRange = false;
//					break;
//				}
//			}
		}
		
		if(isMetaboChip && !currentSample.getSample().getSampleRanges().isMataboChipRange()){
			qualityAssistent.addNewIssue(new MetaboRangeDetected(qualityAssistent, currentSample));
		}
		else if(isControlRange && !currentSample.getSample().getSampleRanges().isControlRange()){
			qualityAssistent.addNewIssue(new ControlRangeDetected(qualityAssistent, currentSample));	
		}
		else if(isCompleteRange && !currentSample.getSample().getSampleRanges().isCompleteRange()
				&& !currentSample.getSample().getSampleRanges().isCustomRange()){
			qualityAssistent.addNewIssue(new CustomOrCompleteRangeDetected(qualityAssistent, currentSample));		
		}
		else
		currentSample.setReachedQualityLevel(this.getPriority() + 1);
	}

	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		if(isMetaboChip){
			QualityIssue issue = qualityAssistent.getIssue(currentSample,"Common rCRS polymorphim (263G 8860G or 15326G)");
			if(issue != null){
				issue.setSuppress(true);
			}
			
			issue = qualityAssistent.getIssue(currentSample,"common RSRS polymorphims found!");
			if(issue != null){
				issue.setSuppress(true);
			}
		}
	}
		
}
