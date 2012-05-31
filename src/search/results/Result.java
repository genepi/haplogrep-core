package search.results;

import core.Haplogroup;
import search.SearchResultPerNode;

public abstract class Result  implements Comparable<Result>{
	double sumPhyloWeightsInSampleRange;
	SearchResultPerNode phyloSearchData;
	Haplogroup expectedHaplogroup;
	
	public Result(double sumPhyloWeightsInSampleRange, SearchResultPerNode phyloSearchData, Haplogroup expectedHaplogroup){
		this.sumPhyloWeightsInSampleRange = sumPhyloWeightsInSampleRange;
		this.phyloSearchData = phyloSearchData;
		this.expectedHaplogroup = expectedHaplogroup;
	}
	
	public SearchResultPerNode getPhyloSearchData(){
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
			delta = -1;
		else if (!o.phyloSearchData.getHaplogroup().equals(expectedHaplogroup) &&
				phyloSearchData.getHaplogroup().equals(expectedHaplogroup))
			delta = 1;
		
		return delta;
	}
}
