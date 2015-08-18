package qualityAssurance.rules;


import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import qualityAssurance.issues.QualityWarning;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForReferencePolymorhisms extends HaplogrepRule {
	
	
	final Log log = LogFactory.getLog(CheckForReferencePolymorhisms.class);
	
	public CheckForReferencePolymorhisms(int priority) {
		super(priority);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		ArrayList<Polymorphism> foundReferencePolys = new ArrayList<Polymorphism>();
		
		for(Polymorphism currentPoly : currentSample.getSample().getPolymorphisms()){
			if(currentPoly.equalsReference())
				foundReferencePolys.add(currentPoly);
		}
		
		if(foundReferencePolys.size() > 0){
			qualityAssistent.addNewIssue(new QualityWarning(qualityAssistent, currentSample, "The sample contains "+ foundReferencePolys.size() +" polymorphimsms " +
					"that are equal to the reference."));	
		}
	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
