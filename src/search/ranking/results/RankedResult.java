package search.ranking.results;

import search.SearchResult;
import core.Haplogroup;

public abstract class RankedResult  implements Comparable<RankedResult>{
	SearchResult phyloSearchData;
	Haplogroup expectedHaplogroup;
	
	public RankedResult(SearchResult phyloSearchData, Haplogroup expectedHaplogroup){
		this.phyloSearchData = phyloSearchData;
		this.expectedHaplogroup = expectedHaplogroup;
	}
	
	public SearchResult getPhyloSearchData(){
		return phyloSearchData;
	}

	public Haplogroup getHaplogroup() {
		return phyloSearchData.getHaplogroup();
	}
	
	public abstract double getDistance();
	
	public int compareTo(RankedResult o) {
		int delta = 0;
	
		if(o.phyloSearchData.getHaplogroup().equals(expectedHaplogroup) &&
				!phyloSearchData.getHaplogroup().equals(expectedHaplogroup))
			delta = 1;
		else if (!o.phyloSearchData.getHaplogroup().equals(expectedHaplogroup) &&
				phyloSearchData.getHaplogroup().equals(expectedHaplogroup))
			delta = -1;
		
		return delta;
	}
	
	
}
