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
public class KylczynskiRanking extends RankingMethod {

	/**
	 * @see RankingMethod
	 */
	public KylczynskiRanking() {
		super();
	}

	/**
	 * @see RankingMethod
	 */
	public KylczynskiRanking(int maxTopResults) {
		super(maxTopResults);
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
		return new KylczynskiRanking(maxTopResults);
	}
}
