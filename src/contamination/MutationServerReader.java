package contamination;

import java.io.File;
import java.util.HashMap;

import contamination.objects.Variant;
import contamination.objects.Sample;
import genepi.io.table.reader.CsvTableReader;

public class MutationServerReader {

	private String file;

	public MutationServerReader(String file) {
		this.file = file;
	}

	public HashMap<String, Sample> parse() {

		CsvTableReader reader = new CsvTableReader(new File(file).getAbsolutePath(), '\t');
		HashMap<String, Sample> samples = new HashMap<String, Sample>();

		String tmp = null;
		Sample sample = new Sample();

		while (reader.next()) {

			String id = reader.getString("SampleID");

			if (tmp != null && !id.equals(tmp)) {
				samples.put(sample.getId(), sample);
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
			Variant position = new Variant();
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

			if (position.getRef() != 'N') {
				sample.updateCount(type);
				sample.updateCoverage(coverage);
			}

			sample.addPosition(position);
			tmp = id;

		}
		samples.put(sample.getId(), sample);

		return samples;
	}

}
