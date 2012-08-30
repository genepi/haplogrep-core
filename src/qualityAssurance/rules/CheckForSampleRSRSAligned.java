package qualityAssurance.rules;


import qualityAssurance.QualityAssistent;
import qualityAssurance.QualityError;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRSRSAligned implements HaplogrepRule {

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
		try {
			if(!currentSample.getSample().contains(new Polymorphism("263A")) && 
					!currentSample.getSample().contains(new Polymorphism("8860A")) &&
					!currentSample.getSample().contains(new Polymorphism("15326A"))){
				qualityAssistent.addNewIssue(new QualityError(qualityAssistent, currentSample, "Common RSRS polymorphims (263A, 8860A or 15326A) found! " +
						"The sample seems to be aligned to RSRS. Haplogrep only supports rCRS aligned samples."));
			}
			
					
		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		

	}

}
