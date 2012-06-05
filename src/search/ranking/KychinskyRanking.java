package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.results.ResultKylcinski;
import core.TestSample;

public class KychinskyRanking extends RankingMethod {
	
	public KychinskyRanking(){
		super();
	}
	

	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {

		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new ResultKylcinski(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
	}
	
	public RankingMethod clone(){
		return new KychinskyRanking();
	}
}
