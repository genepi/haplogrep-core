package search;


import java.util.Iterator;

import core.Polymorphism;
import core.TestSample;

public class SearchResultHamming extends SearchResult {

	public SearchResultHamming(String haplogroup, String phyolTreeString, TestSample polysInTestSample) {
		super(haplogroup,phyolTreeString, polysInTestSample);
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
			distance += missing.next().getPhylogeneticWeight(phyolTreeString);
		}
		
		for(Polymorphism remainingPoly : this.getUnusedPolys())
			distance += remainingPoly.getPhylogeneticWeight(phyolTreeString);
		
		return distance;
	}
}
