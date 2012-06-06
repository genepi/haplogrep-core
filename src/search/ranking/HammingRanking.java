package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import com.sun.net.httpserver.Authenticator.Result;

import search.SearchResult;
import search.ranking.results.RankedResult;
import search.ranking.results.RankedResultHamming;
import core.TestSample;

public class HammingRanking extends RankingMethod {
	
	
	public HammingRanking(){
		super();
	}
	
	public HammingRanking(int maxTopResults){
		super(maxTopResults);
	}
	
	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {
		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new RankedResultHamming(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
		
		cutResultSetToTopHits();
	}
	public RankingMethod clone(){
		return new HammingRanking(maxTopResults);
	}
}
