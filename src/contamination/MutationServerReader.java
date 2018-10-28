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
		return parse(0.00);
	}
	
	public HashMap<String, Sample> parse(double requiredHetLevel) {

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
			char var = reader.getString("Variant").charAt(0);
			double level = reader.getDouble("Variant-Level");
			char major = reader.getString("Major/Minor").split("/")[0].charAt(0);
			char minor = reader.getString("Major/Minor").split("/")[1].charAt(0);
			double majorLevel = Double.valueOf(reader.getString("Major-Percentage/Minor-Percentage").split("/")[0]);
			double minorLevel = Double.valueOf(reader.getString("Major-Percentage/Minor-Percentage").split("/")[1]);
			int coverage = reader.getInteger("Coverage-Total");
			int type = reader.getInteger("Variant-Type");

			sample.setId(id);
			
			if(type == 2 && minorLevel < requiredHetLevel) {
				continue;
			}
			
			Variant variant = new Variant();
			variant.setPos(pos);
			variant.setRef(ref);
			variant.setVariantBase(var);
			variant.setLevel(level);
			variant.setMajor(major);
			variant.setMinor(minor);
			variant.setMajorLevel(majorLevel);
			variant.setMinorLevel(minorLevel);
			variant.setCoverage(coverage);
			variant.setType(type);
			sample.addVariant(variant);
			tmp = id;
		}
		samples.put(sample.getId(), sample);

		return samples;
	}

}
