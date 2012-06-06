package phylotree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class PhylotreeManager {
	private Map<String, Phylotree2> phylotreeMap;
	static PhylotreeManager instance = null;

	private PhylotreeManager() {
		phylotreeMap = new HashMap<String, Phylotree2>();
	}

	public static PhylotreeManager getInstance() {
		if (instance == null) {
			instance = new PhylotreeManager();
		}
		return instance;
	}

	public Phylotree2 getPhylotree(String phylotree, String weights) {
		if (phylotreeMap.containsKey(phylotree))
			return phylotreeMap.get(phylotree);
		else {
			//for CLAP protocol:
			InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotree);
			InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(weights);
			try {
				if (phyloFile == null) {
					phyloFile = new FileInputStream(new File("../HaplogrepServer/phylotree/" + phylotree));
					flucRates = new FileInputStream(new File("../HaplogrepServer/polyGeneticWeights/" + weights));
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Phylotree2 searchMananger = new Phylotree2(phyloFile,flucRates);
			phylotreeMap.put(phylotree, searchMananger);
			return searchMananger;
		}
	}

}
