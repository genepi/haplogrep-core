package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import search.SearchResult;
import search.ranking.results.JaccardResult;
import search.ranking.results.KylczynskiResult;
import core.TestSample;

/**
 * Represents the Jaccard ranking method
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class JaccardRanking extends RankingMethod {

	/**
	 * @see RankingMethod
	 */
	public JaccardRanking() {
		super();
	}

	/**
	 * @see RankingMethod
	 */
	public JaccardRanking(int maxTopResults) {
		super(maxTopResults);
	}
	
	public JaccardRanking(int maxTopResults, String name) {
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
			results.add(new JaccardResult(currentResult, sample.getExpectedHaplogroup()));
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
		return new JaccardRanking(maxTopResults);
	}
}
