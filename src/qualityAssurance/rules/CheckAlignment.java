package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import core.Mutations;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;
import genepi.io.table.reader.CsvTableReader;

public class CheckAlignment extends HaplogrepRule {

	public CheckAlignment(int priority) {
		super(priority);
		// TODO Auto-generated constructor stub
	}

	private HashMap<String, String> rules = null;

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {

		if (rules == null) {

			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("alignment-rules.csv");

			CsvTableReader reader;

			reader = new CsvTableReader(new DataInputStream(stream), ',');

			rules = new HashMap<String, String>();

			while (reader.next()) {
				rules.put(reader.getString("error"), reader.getString("expected"));
			}
		}

		ArrayList<Polymorphism> inPolys = currentSample.getSample().getPolymorphisms();

		ArrayList<Polymorphism> outPolys = new ArrayList<Polymorphism>();

		HashSet<String> inputProfile = new HashSet<String>();

		for (Polymorphism current : inPolys) {
			inputProfile.add(current.toString());
		}

		for (String errorPoly : rules.keySet()) {

			boolean applyRule = true;

			String[] splits = errorPoly.split(" ");

			for (String split : splits) {
				if (!inputProfile.contains(split)) {
					applyRule = false;
				}

			}

			if (applyRule) {
				String correctPoly = rules.get(errorPoly);
				try {
					for (String a : correctPoly.split(" ")) {
						outPolys.add(new Polymorphism(a));
					}
					for (String a : errorPoly.split(" ")) {
						inputProfile.remove(a);
					}
				} catch (InvalidPolymorphismException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for(String yo : inputProfile) {
			try {
				outPolys.add(new Polymorphism(yo));
			} catch (InvalidPolymorphismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		currentSample.getSample().setPolymorphisms(outPolys);
		
		System.out.println(outPolys);

	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub

	}

}
