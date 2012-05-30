package phylotree;

import java.util.HashMap;
import java.util.Map;

public class PhylotreeManager {
	private Map<String, PhyloTree> phylotreeMap;
	static PhylotreeManager instance = null;

	private PhylotreeManager() {
		phylotreeMap = new HashMap<String, PhyloTree>();
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
