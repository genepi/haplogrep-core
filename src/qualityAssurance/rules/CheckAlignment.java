package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.util.ArrayList;

import core.Mutations;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public class CheckAlignment extends HaplogrepRule {

	public CheckAlignment(int priority) {
		super(priority);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {
		// Skip if no expected haplogroup was defined

		ArrayList<Polymorphism> inPolys = currentSample.getSample().getPolymorphisms();

		ArrayList<Polymorphism> outPolys = new ArrayList<Polymorphism>();
		for (Polymorphism current : inPolys) {

			if (current.getMutation() == Mutations.DEL || current.getMutation() == Mutations.INS) {
				try {
					outPolys.add(convertPos(current));
				} catch (InvalidPolymorphismException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				outPolys.add(new Polymorphism(current));
			}
		}

		currentSample.getSample().setPolymorphisms(outPolys);

	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub

	}

	private Polymorphism convertPos(Polymorphism poly) throws InvalidPolymorphismException {
		switch (poly.toString()) {
		case "8271d":
			return new Polymorphism("8281d");
		case "8272d":
			return new Polymorphism("8282d");
		case "8273d":
			return new Polymorphism("8283d");
		case "8274d":
			return new Polymorphism("8284d");
		case "8275d":
			return new Polymorphism("8285d");
		case "8276d":
			return new Polymorphism("8286d");
		case "8277d":
			return new Polymorphism("8287d");
		case "8278d":
			return new Polymorphism("8288d");
		case "8279d":
			return new Polymorphism("8289d");
		}
		return poly;
	}

}
