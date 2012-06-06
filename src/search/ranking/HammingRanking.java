package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.ranking.results.RankedResultHamming;
import core.TestSample;

public class HammingRanking extends RankingMethod {
	
	
	public HammingRanking(){
		super();
	}
	
	
	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {
		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new RankedResultHamming(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
	}
	public RankingMethod clone(){
		return new HammingRanking();
	}
}
