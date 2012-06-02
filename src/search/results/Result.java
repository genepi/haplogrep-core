package search.results;

import core.Haplogroup;
import search.SearchResult;

public abstract class Result  implements Comparable<Result>{
	SearchResult phyloSearchData;
	Haplogroup expectedHaplogroup;
	
	public Result(SearchResult phyloSearchData, Haplogroup expectedHaplogroup){
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
	
	public int compareTo(Result o) {
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
