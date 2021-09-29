package search.ranking.results;

import org.json.JSONException;
import org.json.JSONObject;

import search.SearchResult;
import core.Haplogroup;

/**
 * Encapsulates a Search Result instance to add ranking functionality
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */

public abstract class RankedResult implements Comparable<RankedResult> {
	protected SearchResult searchResult;
	private Haplogroup expectedHaplogroup;

	/**
	 * Creates a new ranked Result
	 * 
	 * @param searchResult
	 *            The search result object to be ranked
	 * @param expectedHaplogroup
	 *            The haplogroup expected for this result
	 */
	public RankedResult(SearchResult searchResult, Haplogroup expectedHaplogroup) {
		this.searchResult = searchResult;
		this.expectedHaplogroup = expectedHaplogroup;
	}

	/**
	 * @return The encapsulated SearchResult object
	 */
	public SearchResult getSearchResult() {
		return searchResult;
	}

	/**
	 * @return The haplogroup detected by this result
	 */
	public Haplogroup getHaplogroup() {
		return searchResult.getHaplogroup();
	}

	/**
	 * @return The distance of this result
	 */
	public abstract double getDistance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RankedResult o) {
		int delta = 0;

		if (o.searchResult.getHaplogroup().equals(expectedHaplogroup) && !searchResult.getHaplogroup().equals(expectedHaplogroup))
			delta = 1;
		else if (!o.searchResult.getHaplogroup().equals(expectedHaplogroup) && searchResult.getHaplogroup().equals(expectedHaplogroup))
			delta = -1;

		return delta;
	}

	/**
	 * Attaches this result to a given JSON object
	 * 
	 * @param parent
	 *            The JASON object
	 * @throws JSONException
	 */
	public abstract void attachToJsonObject(JSONObject parent) throws JSONException;

}
