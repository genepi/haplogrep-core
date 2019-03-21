package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
		for (Polymorphism current : inPolys) {

			if (current.getMutation() == Mutations.DEL || current.getMutation() == Mutations.INS) {
				try {
					String substitute = rules.get(current.toString());
					if(substitute!=null) {
					outPolys.add(new Polymorphism(substitute));
					}
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

}
