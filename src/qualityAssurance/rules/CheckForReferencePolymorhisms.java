package qualityAssurance.rules;


import java.util.ArrayList;

import qualityAssurance.QualityAssistent;
import qualityAssurance.QualityError;
import qualityAssurance.QualityWarning;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForReferencePolymorhisms implements HaplogrepRule {
	ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
	
	
	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
			if(currentPoly.equalsReference())
				foundReferencePolys.add(currentPoly);
		}
		
		if(foundReferencePolys.size() > 0){
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains "+ foundReferencePolys.size() +" polymorphimsms " +
					"that are equal to the reference."));	
		}
	}

}
