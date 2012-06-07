package search.ranking.results;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import search.SearchResult;
import core.Haplogroup;

public class RankedResultHamming extends RankedResult {


	double hammingDistance;
	public RankedResultHamming(SearchResult result,Haplogroup expectedHaplogroup) {
		super(result,expectedHaplogroup);
		hammingDistance = calcDistance();
	}

	public int compareTo(RankedResult o) {
		int delta = (int)Math.signum(hammingDistance - ((RankedResultHamming)o).hammingDistance);
		if(delta == 0)
			return super.compareTo(o);
		
		return delta;
	}

	private double calcDistance() {
		return (phyloSearchData.getWeightRemainingPolys()) + phyloSearchData.getSumMissingPhyloWeight();
	}

	@Override
	public double getDistance() {
		return hammingDistance;
	}
	
	@Override
	public void attachToJsonObject(JSONObject child) throws JSONException {
		DecimalFormat df = new DecimalFormat( "0.000",new DecimalFormatSymbols(Locale.US));
		child.put("rank",df.format(hammingDistance));
		child.put("rankHG",df.format(phyloSearchData.getSumMissingPhyloWeight()));
		child.put("rankS",df.format(phyloSearchData.getWeightRemainingPolys()));
		child.put("name",phyloSearchData.getHaplogroup().toString());
		child.put("id",phyloSearchData.getHaplogroup().toString());	
	}
}
