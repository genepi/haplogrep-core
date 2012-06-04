package search;


import java.util.Iterator;

import core.Polymorphism;
import core.TestSample;

public class SearchResultHamming extends SearchResult {

	public SearchResultHamming(HaploSearchManager searchManager,String haplogroup, TestSample polysInTestSample) {
		super(searchManager,haplogroup, polysInTestSample);
	}

	
	public SearchResultHamming(String attributeValue, String phyolTreeString, SearchResult parentResult) {
		super(attributeValue,phyolTreeString,parentResult);
	}


	/* To sort SearchResults properly according to its rank
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchResult o) {
		if (this.getHammingDistance() < o.getHammingDistance())
			return -1;
		if (this.getHammingDistance() > o.getHammingDistance())
			return 1;
		else
			return 0;

	}
	
	/**
	 * @return The rank of this search result
	 */
	@Override
	public double getRank() {
		return getHammingDistance();
	}
	
	private double calcHammingDistance() {
		double distance = 0;
		Iterator<Polymorphism> missing = this.getIterMissingPolys();
		
		while(missing.hasNext()){
			distance += searchManager.getMutationRate(missing.next());
		}
		
		for(Polymorphism remainingPoly : this.getUnusedPolys())
			distance += searchManager.getMutationRate(remainingPoly);
		
		return distance;
	}
}
