package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.ranking.results.RankedResultKylczynski;
import core.TestSample;

public class KylczynskiRanking extends RankingMethod {
	
	public KylczynskiRanking(){
		super();
	}
	
	public KylczynskiRanking(int maxTopResults){
		super(maxTopResults);
	}

	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {

		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new RankedResultKylczynski(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
		
		cutResultSetToTopHits();
	}
	
	public RankingMethod clone(){
		return new KylczynskiRanking(maxTopResults);
	}
}
