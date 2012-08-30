package qualityAssurance.rules;


import qualityAssurance.QualityAssistent;
import qualityAssurance.QualityError;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRCRSAligned implements HaplogrepRule {

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		try {
			if(!currentSample.getSample().contains(new Polymorphism("263G")) && 
					!currentSample.getSample().contains(new Polymorphism("8860G")) &&
					!currentSample.getSample().contains(new Polymorphism("15326G"))){
				qualityAssistent.addNewIssue(new QualityError(qualityAssistent, currentSample, "Common rCRS polymorphim (263G 8860G or 15326G) not found! " +
						"The sample seems not properly aligned to rCRS."));
			}
			
					
		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		

	}

}
