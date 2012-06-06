package search.ranking;

import java.util.ArrayList;

import search.SearchResult;
import search.ranking.results.RankedResult;
import core.TestSample;

public abstract class RankingMethod {
	ArrayList<RankedResult> results;
	
	public abstract void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper);

	public RankingMethod(){
		results = new ArrayList<RankedResult>();
	}
	
	public RankedResult getTopResult() {
		return results.get(0);
	}
	
	public ArrayList<RankedResult> getResults(){
		return results;
	}
	
	public abstract RankingMethod clone();
}
