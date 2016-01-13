package qualityAssurance.rules;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRSRSAligned extends HaplogrepRule {
	static final Log log = LogFactory.getLog(CheckForSampleRSRSAligned.class);
	static ArrayList<Polymorphism> uniqueRSRSPolys = null;
	
	public CheckForSampleRSRSAligned(int priority){
		super(priority);
		if(uniqueRSRSPolys == null){
			uniqueRSRSPolys = new ArrayList<Polymorphism>();
			
			loadUniqueRSRSPositions();
		}
	}
	
	private void loadUniqueRSRSPositions() {
		try {
			InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream("RSRSPolymorphisms");
			
			if(phyloFile == null)
				phyloFile = new  FileInputStream(new File("testDataFiles/RSRSPolymorphisms"));
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(phyloFile));

			String currentLine = reader.readLine();

			while(currentLine != null){
			Polymorphism newUniquePoly = new Polymorphism(currentLine.trim());
			uniqueRSRSPolys.add(newUniquePoly);
			currentLine = reader.readLine();
			}
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {

		int numRSRSPolysFound = 0;
	
			
		for(Polymorphism currentUniqueRSRSPoly : uniqueRSRSPolys)	
			if(currentSample.getSample().contains(currentUniqueRSRSPoly)>0) 
				numRSRSPolysFound++;
		
		
			if(numRSRSPolysFound > 1){
				if(numRSRSPolysFound == 5){
					for(Polymorphism currentUniqueRSRSPoly : uniqueRSRSPolys)	
						if(currentSample.getSample().contains(currentUniqueRSRSPoly)>0){
							log.debug(currentUniqueRSRSPoly);
						}
								
				}
				
				qualityAssistent.addNewIssue(new QualityFatal(qualityAssistent, currentSample, numRSRSPolysFound + " common RSRS polymorphims found! " +
						"The sample seems to be aligned to RSRS. Haplogrep only supports rCRS aligned samples."));
				
			}
			else
				currentSample.setReachedQualityLevel(this.getPriority()+1);		
		}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
