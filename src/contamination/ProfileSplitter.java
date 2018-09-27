package contamination;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import genepi.io.table.TableReaderFactory;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;

public class ProfileSplitter {

/*	public String splitFile2(String variantfile) {
		
		CsvTableReader reader = new CsvTableReader(new File(variantfile).getAbsolutePath(), '\t');
		StringBuilder lines = new StringBuilder();
		HashMap<String, String> map = new HashMap<String, String>();
		
		HSDEntry minor = new HSDEntry();
		HSDEntry major = new HSDEntry();
		minor.setID("f" + "_maj");
		minor.setRANGE("1-16569");
		major.setID("f" + "_min");
		major.setRANGE("1-16569");
		
		while (reader.next()) {
			
			String pos = reader.getString("Pos");
			String variant = reader.getString("Major/Minor");
			
			if(reader.getInteger("Variant-Type") == 1) {
				major.appendPROFILES(pos+""+variant.split("/")[0]);
				minor.appendPROFILES(pos+""+variant.split("/")[0]);
			}
			
			if(reader.getInteger("Variant-Type") == 2) {
				major.appendPROFILES(pos+""+variant.split("/")[0]);
				minor.appendPROFILES(pos+""+variant.split("/")[1]);
			}
			
		}
		lines.append(major);
		lines.append(minor);
		return lines.toString();
		
	}*/
	public String splitFile(String variantfile, double vaf) throws MalformedURLException, IOException {

		try {

			ITableReader idReader = TableReaderFactory.getReader(variantfile);

			HashMap<String, ArrayList<CheckEntry>> entries = new HashMap<String, ArrayList<CheckEntry>>();

			try {
				while (idReader.next()) {
					CheckEntry entry = new CheckEntry();
					String id = idReader.getString(HeaderNames.SampleId.colname()); // SampleID
					entry.setID(id);
					entry.setPOS(idReader.getInteger(HeaderNames.Position.colname())); // Pos
					entry.setREF(idReader.getString(HeaderNames.Reference.colname())); // Ref
					entry.setALT(idReader.getString(HeaderNames.VariantBase.colname())); // Variant
					entry.setVAF(idReader.getDouble(HeaderNames.VariantLevel.colname())); // Variant-Level
					entry.setCOV(idReader.getInteger(HeaderNames.Coverage.colname())); // Coverage-Total

					if (entries.containsKey(id)) {
						entries.get(id).add(entry);
					} else if (entries.get(id) == null) {
						entries.put(id, new ArrayList<CheckEntry>());
						entries.get(id).add(entry);
					}
				}
				idReader.close();
			} catch (Exception e) {
				System.out.println("Column names correctly present? \nExpecting tab delimited columns: \n" + HeaderNames.SampleId.colname() + " "
						+ HeaderNames.Position.colname() + " " + HeaderNames.Reference.colname() + " " + HeaderNames.VariantBase.colname() + " "
						+ HeaderNames.VariantLevel.colname() + " " + HeaderNames.Coverage.colname());
				e.printStackTrace();
			}

			return generateHSDfile(entries, vaf);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "nothing";
	}

	public String generateHSDfile(HashMap<String, ArrayList<CheckEntry>> samples, double vaf) throws Exception {

		StringBuilder builder = new StringBuilder();

		int counter = 0;
		Iterator it = samples.entrySet().iterator();
		while (it.hasNext()) {
			counter++;
			Map.Entry pair = (Map.Entry) it.next();
			HSDEntry minor = new HSDEntry();
			HSDEntry major = new HSDEntry();
			minor.setID(pair.getKey() + "_maj");
			minor.setRANGE("1-16569");
			major.setID(pair.getKey() + "_min");
			major.setRANGE("1-16569");
			int hetcounter = 0;
			ArrayList<CheckEntry> helpArray = samples.get(pair.getKey());
			for (int i = 0; i < helpArray.size(); i++) {
				if (helpArray.get(i).getREF().contains("-") || helpArray.get(i).getALT().contains("-")
						|| helpArray.get(i).getALT().length() > 1 || helpArray.get(i).getREF().length() > 1) {
					// skip indel, and 3107 on rCRS;
				} else {
					if (helpArray.get(i).getVAF() < 0.5) {
						minor.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getREF());
						major.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getALT());
						hetcounter++;
					} else if (helpArray.get(i).getVAF() >= 0.5 && helpArray.get(i).getVAF() < 1 - vaf) {
						minor.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getALT());
						major.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getREF());
						hetcounter++;
					} else {
						minor.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getALT());
						major.appendPROFILES(helpArray.get(i).getPOS() + helpArray.get(i).getALT());
					}

				}
			}
			if (hetcounter > 0) {
				builder.append(minor.getString() + "\n");
				builder.append(major.getString() + "\n");
			}

			it.remove();
		}
		return builder.toString();
	}

	public enum HeaderNames {
		SampleId("SampleID"), Position("Pos"), Reference("Ref"), VariantBase("Variant"), VariantLevel("Variant-Level"), Coverage("Coverage-Total");
		private String colName;

		HeaderNames(String colname) {
			this.colName = colname;
		}

		public String colname() {
			return colName;
		}

	}

}
