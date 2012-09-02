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
import qualityAssurance.QualityError;
import qualityAssurance.QualityWarning;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRange implements HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
	//static HashSet<Integer> metaboChipPositions = null;
	
	public CheckForSampleRange(){
//		if(metaboChipPositions == null){
//			metaboChipPositions = new HashSet<Integer>();
//			
//			loadMetaboChipPositions();
//		}
	}
	
	

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
			
		boolean isMetaboChip = true;
		
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
			SampleRanges controlRange = new SampleRanges();
			controlRange.addControlRange();
			
			for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
				if(!controlRange.contains(currentPoly)){
					isCompleteRange = false;
					break;
				}
			}
		}
		
		if(isMetaboChip && !currentSample.getSample().getSampleRanges().isMataboChipRange())
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "MetaboChip range detected but does not match the indicated range"));
		else if(isControlRange && !currentSample.getSample().getSampleRanges().isControlRange()){
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "Control range recognized"));	
		}
		else if(isCompleteRange && !currentSample.getSample().getSampleRanges().isCompleteRange())
		qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "Complete range recognized"));
	}

}
