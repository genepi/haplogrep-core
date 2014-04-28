package qualityAssurance.rules;


import java.util.ArrayList;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForReferencePolymorhisms extends HaplogrepRule {
	
	
	
	public CheckForReferencePolymorhisms(int priority) {
		super(priority);
		System.out.println("errooooor tooo many rCRS");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		System.out.println("2 many");
		ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
		
		for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
			if(currentPoly.equalsReference())
				foundReferencePolys.add(currentPoly);
		}
		
		if(foundReferencePolys.size() > 0){
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains "+ foundReferencePolys.size() +" polymorphimsms " +
					"that are equal to the reference."));	
			System.out.println("toooooooooooooooooooo many");
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
