package phylotree;

import java.util.HashMap;
import java.util.Map;


public class PhylotreeManager {
	private Map<String, Phylotree> phylotreeMap;
	static PhylotreeManager instance = null;

	private PhylotreeManager() {
		phylotreeMap = new HashMap<String, Phylotree>();
	}

	public static PhylotreeManager getInstance() {
		if (instance == null) {
			instance = new PhylotreeManager();
		}
		return instance;
	}

	public Phylotree getPhylotree(String key, String weights) {
		if (phylotreeMap.containsKey(key))
			return phylotreeMap.get(key);
		else {
			Phylotree searchMananger = new Phylotree(key,
					weights);
			phylotreeMap.put(key, searchMananger);
			return searchMananger;
		}
	}

}
