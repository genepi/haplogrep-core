package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.ranking.results.KylczynskiResult;
import core.TestSample;

/**
 * Represents the Kylczynski ranking method
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class KulczynskiRanking extends RankingMethod {

	/**
	 * @see RankingMethod
	 */
	public KulczynskiRanking() {
		super();
	}

	/**
	 * @see RankingMethod
	 */
	public KulczynskiRanking(int maxTopResults) {
		super(maxTopResults);
	}
	
	public KulczynskiRanking(int maxTopResults, String name) {
		super(maxTopResults, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.RankingMethod#setResults(core.TestSample,
	 * java.util.ArrayList)
	 */
	@Override
	public void setResults(TestSample sample, ArrayList<SearchResult> searchPhylotreeWrapper) {

		for (SearchResult currentResult : searchPhylotreeWrapper) {
			results.add(new KylczynskiResult(currentResult, sample.getExpectedHaplogroup()));
		}

		Collections.sort(results);

		cutResultSetToTopHits(sample);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.RankingMethod#clone()
	 */
	@Override
	public RankingMethod clone() {
		return new KulczynskiRanking(maxTopResults);
	}
}
