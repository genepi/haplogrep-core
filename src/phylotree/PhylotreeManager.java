package phylotree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.media.sound.InvalidFormatException;

import core.Polymorphism;
import exceptions.parse.sample.InvalidBaseException;

import search.HaploSearchManager;

public class PhylotreeManager {
	private Map<String, PhyloTree> phylotreeMap;
	static PhylotreeManager instance = null;

	private PhylotreeManager() {
		phylotreeMap = new HashMap<String, HaploSearchManager>();
	}

	public static PhylotreeManager getInstance() {
		if (instance == null) {
			instance = new PhylotreeManager();
		}
		return instance;
	}

	/** Returns the instance of the phylotree version given by filename. 
	 *  If the instance doesn't exist, it's created beforehand.
	 * @param phylotree Name of xml file which contains the phylotree
	 * @param phyloWeights Name of file with polygenetic weights
	 */
	public PhyloTree getPhylotree(String phylotree, String phyloWeights) {
		if (phylotreeMap.containsKey(phylotree))
			return phylotreeMap.get(phylotree);
		else {
			PhyloTree searchMananger = new PhyloTree(phylotree,phyloWeights);
			phylotreeMap.put(phylotree, searchMananger);
			return searchMananger;
		}
	}

}
