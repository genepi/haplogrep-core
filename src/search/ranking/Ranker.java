package search.ranking;

import java.util.ArrayList;

import phylotree.PhyloTree;
import search.SearchResultPerNode;
import search.results.Result;

import core.TestSample;

public abstract class Ranker {
	ArrayList<Result> results;
	
	public abstract void setResults(PhyloTree phyloTree, TestSample sample, ArrayList<SearchResultPerNode> searchPhylotreeWrapper);

	public Result getTopResult() {
		return results.get(0);
	}
	
	public ArrayList<Result> getResults(){
		return results;
	}
}
