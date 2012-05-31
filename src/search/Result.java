package search;

import java.util.ArrayList;

import core.Sample;

public abstract class Result {
	Sample sample;
	ArrayList<SearchResultPerNode> phyloSearchData;
	
	public Result(Sample sample, ArrayList<SearchResultPerNode> phyloSearchData){
		this.sample = sample;
		this.phyloSearchData = phyloSearchData;
	}
}
