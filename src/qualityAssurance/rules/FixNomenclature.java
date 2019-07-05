package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;
import genepi.io.table.reader.CsvTableReader;

public class FixNomenclature extends HaplogrepRule {

	public FixNomenclature(int priority) {
		super(priority);
	}

	private HashMap<String, String> rules = null;

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) {

		if (rules == null) {

			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("nomenclature-rules.csv");

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

		for (String error : rules.keySet()) {

			boolean applyRule = true;

			String[] splits = error.split(" ");

			for (String split : splits) {
				if (!inputProfile.contains(split)) {
					applyRule = false;
				}

			}

			if (applyRule) {
				String expected = rules.get(error);
				try {
					for (String exp : expected.split(" ")) {
						outPolys.add(new Polymorphism(exp));
					}
					for (String err : error.split(" ")) {
						inputProfile.remove(err);
					}
				} catch (InvalidPolymorphismException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// add remaining
		for (String in : inputProfile) {
			try {
				outPolys.add(new Polymorphism(in));
			} catch (InvalidPolymorphismException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(outPolys);
		currentSample.getSample().setPolymorphisms(outPolys);

	}

	@Override
	public void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample) {
		// TODO Auto-generated method stub

	}

}
