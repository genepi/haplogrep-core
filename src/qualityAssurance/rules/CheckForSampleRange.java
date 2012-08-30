package qualityAssurance.rules;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	static HashSet<Integer> metaboChipPositions = null;
	
	public CheckForSampleRange(){
		if(metaboChipPositions == null){
			metaboChipPositions = new HashSet<Integer>();
			
			loadMetaboChipPositions();
		}
	}
	
	private void loadMetaboChipPositions() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("testDataFiles/metaboChipPositions")));

			String currentLine = reader.readLine();
			while (currentLine != null) {
				Scanner sc = new Scanner(currentLine);
				sc.next();
				sc.next();
				metaboChipPositions.add(Integer.parseInt(sc.next().replace("mt", "")));
				currentLine = reader.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
			
		boolean isMetaboChip = true;
		
		for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
			if(!metaboChipPositions.contains(currentPoly.getPosition())){
					isMetaboChip = false;
					break;
			}
		}
		boolean isControlRange = true;
		if(!isMetaboChip){
			SampleRanges controlRange = new SampleRanges();
			controlRange.addControlRange();
			
			for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
				if(!controlRange.contains(currentPoly)){
					isControlRange = false;
					break;
				}
			}
		}
		
		if(isMetaboChip)
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "MetaboChip recognized"));
		else if(isControlRange){
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "Control range recognized"));	
		}
		else
		qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "Complete range recognized"));
	}

}
