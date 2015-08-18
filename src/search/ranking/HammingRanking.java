package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.ranking.results.HammingResult;
import core.TestSample;

/**
 * Represents the Hamming ranking method
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class HammingRanking extends RankingMethod {
	
	/**
	 * @see RankingMethod
	 */
	public HammingRanking(){
		super();
	}
	
	/**
	 * @see RankingMethod
	 */
	public HammingRanking(int maxTopResults){
		super(maxTopResults);
	}
	
	/* (non-Javadoc)
	 * @see search.ranking.RankingMethod#setResults(core.TestSample, java.util.ArrayList)
	 */
	@Override
	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {
		for(SearchResult currentResult : searchPhylotreeWrapper){
			results.add(new HammingResult(currentResult,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
		
		cutResultSetToTopHits(sample);
	}
	/* (non-Javadoc)
	 * @see search.ranking.RankingMethod#clone()
	 */
	@Override
	public RankingMethod clone(){
		return new HammingRanking(maxTopResults);
	}
}
