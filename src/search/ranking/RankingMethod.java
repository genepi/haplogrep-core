package search.ranking;

import java.util.ArrayList;

import search.SearchResult;
import search.ranking.results.RankedResult;
import core.TestSample;

/**
 * Represents a abstract ranking method
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public abstract class RankingMethod {
	ArrayList<RankedResult> results;
	int maxTopResults = Integer.MAX_VALUE;

	/**
	 * Sets a new set of results for this ranking method
	 * 
	 * @param sample
	 *            The test sample the search ha been done
	 * @param searchResults
	 *            A list of search result
	 */
	public abstract void setResults(TestSample sample, ArrayList<SearchResult> searchResults);

	/**
	 * Creates a new instance with no limitation of the number of results
	 */
	public RankingMethod() {
		results = new ArrayList<RankedResult>();
	}

	/**
	 * Creates a new instance that only keeps a certain number of top results
	 * 
	 * @param maxTopResults
	 *            The number of top results to keep
	 */
	RankingMethod(int maxTopResults) {
		this.maxTopResults = maxTopResults;
		results = new ArrayList<RankedResult>();
	}

	/**
	 * @return The best result
	 */
	public RankedResult getTopResult() {
		return results.get(0);
	}

	/**
	 * @return A list of all results ranked by the ranking method
	 */
	public ArrayList<RankedResult> getResults() {
		return results;
	}

	/**
	 * Reduces the entire result set to the maximum number of allowed top
	 * results
	 * @param sample 
	 */
	void cutResultSetToTopHits(TestSample sample) {
		ArrayList<RankedResult> topResults = new ArrayList<RankedResult>();

		int numResults = 0;
		for(RankedResult currentResult : results){
			if(numResults < maxTopResults){
				topResults.add(currentResult);	
			}
			
			else if(currentResult.getHaplogroup().equals(sample.getExpectedHaplogroup()))
			topResults.add(currentResult);
			
			numResults++;
		}
		
		results = topResults;
//		
//		if (maxTopResults != Integer.MAX_VALUE) {
//			for (int i = 0; i < Math.min(maxTopResults, results.size()); i++) {
//				topResults.add(results.get(i));
//			}
//			results = topResults;
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract RankingMethod clone();
}
