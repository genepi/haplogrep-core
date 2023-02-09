package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import core.Polymorphism;
import core.Reference;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;
import genepi.io.table.reader.CsvTableReader;

public class FixNomenclature extends HaplogrepRule {

	public FixNomenclature(int priority, String file) {
		super(priority, file);
	}

	@Override
	public void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) throws FileNotFoundException {

		Reference reference = currentSample.getReference();

		InputStream stream = null;
		try {
			stream = new FileInputStream(getFile());
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Please specify a rules file!");
		}
		CsvTableReader reader;

		reader = new CsvTableReader(new DataInputStream(stream), ',');

		HashMap<String, String> rules = new HashMap<String, String>();

		while (reader.next()) {
			rules.put(reader.getString("error").trim(), reader.getString("expected").trim());
		}

		ArrayList<Polymorphism> inPolys = currentSample.getSample().getPolymorphisms();

		ArrayList<Polymorphism> outPolys = new ArrayList<Polymorphism>();

		HashSet<String> inputProfile = new HashSet<String>();
		HashSet<String> inputProfileString = new HashSet<String>();

		for (Polymorphism current : inPolys) {
			inputProfile.add(current.toString());
			inputProfileString.add(current.toString());
		}

		for (String error : rules.keySet()) {

			boolean applyRule = true;

			String[] splits = error.split(" ");

			for (String split : splits) {
				if (!inputProfileString.contains(split)) {
					applyRule = false;
				}

			}

			if (applyRule) {
				String expected = rules.get(error);
				try {
					for (String exp : expected.split(" ")) {
						outPolys.add(new Polymorphism(reference, exp));
					}
					for (String err : error.split(" ")) {
						inputProfileString.remove(err);
					}
				} catch (InvalidPolymorphismException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// add remaining
		for (String in : inputProfile) {
			// only if also removed from string
			if (inputProfileString.contains(in.toString()))
				try {
					outPolys.add(new Polymorphism(reference, in));
				} catch (InvalidPolymorphismException e) {
					// TODO Auto-generated catch block
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
