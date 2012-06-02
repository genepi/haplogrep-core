package search.ranking;

import java.util.ArrayList;

import search.SearchResult;
import search.results.Result;

import core.TestSample;

public abstract class Ranker {
	ArrayList<Result> results;
	
	public abstract void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper);

	public Ranker(){
		results = new ArrayList<Result>();
	}
	
	public Result getTopResult() {
		return results.get(0);
	}
	
	public ArrayList<Result> getResults(){
		return results;
	}
	
	
}
