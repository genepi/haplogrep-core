package contamination;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import core.SampleFile;
import core.TestSample;
import exceptions.parse.HsdFileException;
import genepi.io.table.reader.CsvTableReader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;

public class ContaminationChecker {

	public ArrayList<String> splitFile(String file) {

		CsvTableReader reader = new CsvTableReader(new File(file).getAbsolutePath(), '\t');
		ArrayList<String> lines = new ArrayList<String>();
		
		TreeMap<String, ArrayList<HSDEntry>> profiles = new TreeMap<String, ArrayList<HSDEntry>>();
		while (reader.next()) {

			String id = reader.getString("SampleID");

			if (!profiles.containsKey(id)) {
				ArrayList<HSDEntry> list = new ArrayList<HSDEntry>();
				HSDEntry majorProfile = new HSDEntry();
				HSDEntry minorProfile = new HSDEntry();
				majorProfile.setId(id+"_maj");
				minorProfile.setId(id+"_min");
				list.add(majorProfile);
				list.add(minorProfile);
				profiles.put(id, list);
			}

			ArrayList<HSDEntry> profile = profiles.get(id);

			int pos = reader.getInteger("Pos");
			String major = reader.getString("Major/Minor").split("/")[0];
			String minor = reader.getString("Major/Minor").split("/")[1];
			int type = reader.getInteger("Variant-Type");

			if (type == 1) {
				profile.get(0).appendToProfile(pos + major);
				profile.get(1).appendToProfile(pos + major);
			}

			if (type == 2) {
				profile.get(0).appendToProfile(pos + major);
				profile.get(1).appendToProfile(pos + minor);
			}

		}

		for (Map.Entry<String, ArrayList<HSDEntry>> entry : profiles.entrySet()) {
			for(HSDEntry ent : entry.getValue()) {
				lines.add(ent.getString());
			}
		}

		// printMap(profiles);
		
		return lines;

	}
	
	public SampleFile calculateHaplogrops(Phylotree phylotree, ArrayList<String> profiles) {
		
		SampleFile samples = null;
		try {
			samples = new SampleFile(profiles);
			
			samples.updateClassificationResults(phylotree, new KulczynskiRanking(10));
			
			return samples;
			
		} catch (HsdFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return samples;
	
	
	}
	
	public void printMap(TreeMap<String,ArrayList<HSDEntry>> map) {
		for (Map.Entry<String, ArrayList<HSDEntry>> entry : map.entrySet()) {
			for(HSDEntry ent : entry.getValue()) {
				System.out.println(ent.getString());
			}
		}
	}

}