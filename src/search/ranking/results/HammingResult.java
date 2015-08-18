package search.ranking.results;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import search.SearchResult;
import core.Haplogroup;

/**
 * Encapsulates a Search Result instance adding Hamming distance
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class HammingResult extends RankedResult {

	private double hammingDistance;

	/**
	 * Creates a new result with hamming distance.
	 * 
	 * @see RankedResult#RankedResult(SearchResult, Haplogroup)
	 */
	public HammingResult(SearchResult result, Haplogroup expectedHaplogroup) {
		super(result, expectedHaplogroup);
		hammingDistance = calcDistance();
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
		int delta = (int) Math.signum(hammingDistance - ((HammingResult) o).hammingDistance);
		if (delta == 0)
			return super.compareTo(o);

		return delta;
	}

	private double calcDistance() {
		return (searchResult.getWeightRemainingPolys()) + searchResult.getSumMissingPhyloWeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.results.RankedResult#getDistance()
	 */
	@Override
	public double getDistance() {
		return hammingDistance;
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
		child.put("rank", df.format(hammingDistance));
		child.put("rankHG", df.format(searchResult.getSumMissingPhyloWeight()));
		child.put("rankS", df.format(searchResult.getWeightRemainingPolys()));
		child.put("name", searchResult.getHaplogroup().toString());
		child.put("id", searchResult.getHaplogroup().toString());
	}
}
