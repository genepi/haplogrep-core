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

public class JaccardResult extends RankedResult {

	private double jaccardDistance;

	/**
	 * Creates a new result with Kylczynski distance.
	 * 
	 * @see RankedResult#RankedResult(SearchResult, Haplogroup)
	 */
	public JaccardResult(SearchResult phyloSearchData, Haplogroup expectedHaplogroup) {
		super(phyloSearchData, expectedHaplogroup);
		jaccardDistance = calcDistance();
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
		int delta = (int) Math.signum(((JaccardResult) o).jaccardDistance - jaccardDistance);

		if (delta == 0)
			delta = super.compareTo(o);

		return delta;
	}

	/**
	 * @return The calculated Kylczynski distance
	 */
	private double calcDistance() {
		return ( getCorrectPolyInTestSample() /( getAllPoly()));
	}

	private double getCorrectPolyInTestSample() {
		return searchResult.getWeightFoundPolys() ;
	}

	private double getAllPoly() {
		return searchResult.getSumMissingPhyloWeight() + searchResult.getSumWeightsAllPolysSample();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.results.RankedResult#getDistance()
	 */
	@Override
	public double getDistance() {
		return jaccardDistance;
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
		child.put("rank", df.format(jaccardDistance));
		child.put("rankHG", df.format(getCorrectPolyInTestSample()));
		child.put("rankS", df.format(getAllPoly()));
		child.put("name", searchResult.getHaplogroup().toString());
		child.put("id", searchResult.getHaplogroup().toString());
	}
}
