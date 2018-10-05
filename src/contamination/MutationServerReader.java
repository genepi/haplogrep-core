package contamination;

import java.io.File;
import java.util.ArrayList;

import contamination.objects.Position;
import contamination.objects.Sample;
import genepi.io.table.reader.CsvTableReader;

public class MutationServerReader {

	public ArrayList<Sample> parse(String file) {

		CsvTableReader reader = new CsvTableReader(new File(file).getAbsolutePath(), '\t');
		ArrayList<Sample> samples = new ArrayList<Sample>();

		String tmp = null;
		Sample sample = new Sample();

		while (reader.next()) {

			String id = reader.getString("SampleID");

			if (tmp!=null && !id.equals(tmp)) {
				samples.add(sample);
				sample = new Sample();
			}

			int pos = reader.getInteger("Pos");
			char ref = reader.getString("Ref").charAt(0);
			char variant = reader.getString("Variant").charAt(0);
			double level = reader.getDouble("Variant-Level");
			char major = reader.getString("Major/Minor").split("/")[0].charAt(0);
			char minor = reader.getString("Major/Minor").split("/")[1].charAt(0);
			double majorLevel = Double.valueOf(reader.getString("Major-Percentage/Minor-Percentage").split("/")[0]);
			double minorLevel = Double.valueOf(reader.getString("Major-Percentage/Minor-Percentage").split("/")[1]);
			int coverage = reader.getInteger("Coverage-Total");
			int type = reader.getInteger("Variant-Type");

			sample.setId(id);
			Position position = new Position();
			position.setPos(pos);
			position.setRef(ref);
			position.setVariant(variant);
			position.setLevel(level);
			position.setMajor(major);
			position.setMinor(minor);
			position.setMajorLevel(majorLevel);
			position.setMinorLevel(minorLevel);
			position.setCoverage(coverage);
			position.setType(type);
			
			sample.updateCount(type);
			sample.updateCoverage(coverage);
			
			sample.addPosition(position);
			tmp = id;

		}
		
		samples.add(sample);

		return samples;
	}

}
