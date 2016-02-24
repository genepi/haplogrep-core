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

public class KylczynskiResult extends RankedResult {

	private double kylcinskiDistance;

	/**
	 * Creates a new result with Kylczynski distance.
	 * 
	 * @see RankedResult#RankedResult(SearchResult, Haplogroup)
	 */
	public KylczynskiResult(SearchResult phyloSearchData, Haplogroup expectedHaplogroup) {
		super(phyloSearchData, expectedHaplogroup);
		this.kylcinskiDistance = calcDistance();
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
		int delta = (int) Math.signum(((KylczynskiResult) o).kylcinskiDistance - kylcinskiDistance);

		if (delta == 0)
			delta = super.compareTo(o);

		return delta;
	}

	/**
	 * @return The calculated Kylczynski distance
	 */
	private double calcDistance() {
		if (getCorrectPolyInTestSampleRatio() > 0 && getCorrectPolyInHaplogroupRatio() > 0) {
			return getCorrectPolyInTestSampleRatio() * 0.5 + getCorrectPolyInHaplogroupRatio() * 0.5;
		} else if (getCorrectPolyInTestSampleRatio() > 0) {
			return getCorrectPolyInTestSampleRatio() * 0.5;
		} else if (getCorrectPolyInHaplogroupRatio() > 0) {
			return getCorrectPolyInHaplogroupRatio() * 0.5;
		} else {
			return 0;
		}
	}

	public double getCorrectPolyInTestSampleRatio() {
		return searchResult.getWeightFoundPolys() / (searchResult.getSumWeightsAllPolysSample() - searchResult.getSeb());
	}

	private double getCorrectPolyInHaplogroupRatio() {
		if (searchResult.getExpectedWeightPolys() != 0)
			return searchResult.getWeightFoundPolys() / searchResult.getExpectedWeightPolys();
		else
			return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.results.RankedResult#getDistance()
	 */
	@Override
	public double getDistance() {
			return kylcinskiDistance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see search.ranking.results.RankedResult#attachToJsonObject(org.json.
	 * JSONObject )
	 */
	@Override
	public void attachToJsonObject(JSONObject child) throws JSONException {
		DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));

		child.put("rank", df.format(kylcinskiDistance));
		child.put("name", searchResult.getHaplogroup().toString());
		child.put("id", searchResult.getHaplogroup().toString());
		
		if (!Double.isNaN(getCorrectPolyInTestSampleRatio())){
			child.put("rankS", df.format(getCorrectPolyInTestSampleRatio()));
		} else {
			child.put("rankS", "-");
		}
		
		if (!Double.isNaN(getCorrectPolyInHaplogroupRatio())){
			child.put("rankHG", df.format(getCorrectPolyInHaplogroupRatio()));
		} else {
			child.put("rankHG", "-");
		}
		
	
	}
}
