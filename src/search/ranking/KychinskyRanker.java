package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.results.ResultKylcinski;
import core.TestSample;

public class KychinskyRanker extends Ranker {
	
	public KychinskyRanker(){
		super();
	}
	

	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {

		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new ResultKylcinski(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
	}
}
