package qualityAssurance.rules;


import qualityAssurance.QualityAssistent;
import qualityAssurance.issues.QualityFatal;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckForSampleRCRSAligned extends HaplogrepRule {

	public CheckForSampleRCRSAligned(int priority) {
		super(priority);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		
	//	try {
//			if(!currentSample.getSample().contains(new Polymorphism("263G")) && 
//					!currentSample.getSample().contains(new Polymorphism("8860G")) &&
//					!currentSample.getSample().contains(new Polymorphism("930G")) &&
//					!currentSample.getSample().contains(new Polymorphism("15326G"))){
				qualityAssistent.addNewIssue(new QualityFatal(qualityAssistent, currentSample, "Common rCRS polymorphim (263G 8860G or 15326G) not found! " +
						"The sample seems not properly aligned to rCRS."));
	//		}
			
					
//		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
			
		

	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub
		
	}

}
