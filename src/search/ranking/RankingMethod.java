package search.ranking;

import java.util.ArrayList;

import search.SearchResult;
import search.ranking.results.RankedResult;
import core.TestSample;

public abstract class RankingMethod {
	ArrayList<RankedResult> results;
	int maxTopResults = Integer.MAX_VALUE;
	
	public abstract void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper);

	public RankingMethod(){
		results = new ArrayList<RankedResult>();
	}
	
	public RankingMethod(int maxTopResults){
		this.maxTopResults = maxTopResults;
		results = new ArrayList<RankedResult>();
	}
	
	public RankedResult getTopResult() {
		return results.get(0);
	}
	
	public ArrayList<RankedResult> getResults(){
		return results;
	}
	
	public void cutResultSetToTopHits(){
		ArrayList<RankedResult> topResults = new ArrayList<RankedResult>();
		
		if(maxTopResults != Integer.MAX_VALUE){
			for(int  i = 0; i < Math.min(maxTopResults,results.size());i++){
				topResults.add(results.get(i));
			}
			results = topResults;
		}
	}
	public abstract RankingMethod clone();
}
