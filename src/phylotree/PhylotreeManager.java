package phylotree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import core.Reference;

/**
 * Manages all phylotree instances (versions).
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class PhylotreeManager {
	private Map<String, Phylotree> phylotreeMap;
	private static PhylotreeManager instance = null;

	/**
	 * Private constructor (singleton pattern)
	 */
	private PhylotreeManager() {
		phylotreeMap = new HashMap<String, Phylotree>();
	}

	public static PhylotreeManager getInstance() {
		if (instance == null) {
			instance = new PhylotreeManager();
		}
		return instance;
	}

	/**
	 * Returns the phylotree instance of the requested version. If the instance
	 * does not exist it is created.
	 * 
	 * @param phylotreePath
	 *            Path to xml file with the requested phylotree version
	 * @param phyloGeneticWeightsPath
	 *            Path to the file containing the phylogentic weights.
	 * @return The requested phylotree instance
	 */

	public Phylotree getPhylotree(String phylotreePath, String phyloGeneticWeightsPath, Reference reference, HashSet<String> hotspots) {
		if (phylotreeMap.containsKey(phylotreePath)) {
			return phylotreeMap.get(phylotreePath);
		} else {
			InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotreePath);
			InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(phyloGeneticWeightsPath);
			try {
				if (phyloFile == null) {
					phyloFile = new FileInputStream(new File(phylotreePath));
					flucRates = new FileInputStream(new File(phyloGeneticWeightsPath));
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Phylotree searchMananger = new Phylotree(phyloFile, flucRates, reference, hotspots);
			phylotreeMap.put(phylotreePath, searchMananger);
			return searchMananger;
		}
	}

}
