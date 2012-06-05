package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.results.Result;
import search.results.ResultHamming;
import core.TestSample;

public class HammingRanking extends RankingMethod {
	
	
	public HammingRanking(){
		super();
	}
	
	
	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {
		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new ResultHamming(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
	}
	public RankingMethod clone(){
		return new HammingRanking();
	}
}
