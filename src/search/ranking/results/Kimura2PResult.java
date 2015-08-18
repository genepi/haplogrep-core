package search.ranking.results;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import search.SearchResult;
import core.Haplogroup;

/**
 * Encapsulates a Search Result instance adding Kylczynski distance
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */

public class Kimura2PResult extends RankedResult {

	private double kimura2PDistance;

	/**
	 * Creates a new result with Kimura 2P distance.
	 * 
	 * @see RankedResult#RankedResult(SearchResult, Haplogroup)
	 */
	public Kimura2PResult(SearchResult phyloSearchData, Haplogroup expectedHaplogroup) {
		super(phyloSearchData, expectedHaplogroup);
		kimura2PDistance = calcDistance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * search.ranking.results.RankedResult#compareTo(search.ranking.results.
	 * RankedResult)
	 */
	@Override
	public int compareTo(RankedResult o) {
		int delta = (int) Math.signum(kimura2PDistance - ((Kimura2PResult) o).kimura2PDistance );

		if (delta == 0)
			delta = super.compareTo(o);
		

		return delta;
	}

	/**
	 * @return The calculated Kimura 2P distance
	 */
	private double calcDistance() {
		double p = searchResult.getDetailedResult().getSumWeightsRemainingTransitions() / 16569;
		double q = searchResult.getDetailedResult().getSumWeightsRemainingTransversion() / 16569;

		// calculate the distance (by formula) and return it.
		return ((0.5) * Math.log(1.0 / (1.0 - 2.0 * p - q)) + (0.25) * Math
				.log(1.0 / (1.0 - 2.0 * q)));
	}


	private double getCorrectPolyInTestSample() {
		return searchResult.getDetailedResult().getSumWeightsRemainingTransitions() ;
	}

	private double getAllPoly() {
		return searchResult.getDetailedResult().getSumWeightsRemainingTransversion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.results.RankedResult#getDistance()
	 */
	@Override
	public double getDistance() {
		return kimura2PDistance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * search.ranking.results.RankedResult#attachToJsonObject(org.json.JSONObject
	 * )
	 */
	@Override
	public void attachToJsonObject(JSONObject child) throws JSONException {
		DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
		child.put("rank", df.format(kimura2PDistance));
		child.put("rankHG", df.format(getCorrectPolyInTestSample()));
		child.put("rankS", df.format(getAllPoly()));
		child.put("name", searchResult.getHaplogroup().toString());
		child.put("id", searchResult.getHaplogroup().toString());
	}
}
