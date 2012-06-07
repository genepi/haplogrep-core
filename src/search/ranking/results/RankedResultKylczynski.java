package search.ranking.results;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import search.SearchResult;
import core.Haplogroup;

public class RankedResultKylczynski extends RankedResult {

	double kylcinskiDistance;
	public RankedResultKylczynski( SearchResult phyloSearchData,Haplogroup expectedHaplogroup) {
		super(phyloSearchData,expectedHaplogroup);
		kylcinskiDistance = calcDistance();
	}

	public int compareTo(RankedResult o) {
		int delta = (int) Math.signum(((RankedResultKylczynski)o).kylcinskiDistance - kylcinskiDistance);
			
		if(delta == 0)
			delta = super.compareTo(o);
		
		return delta;
	}

	public double calcDistance() {
		return (getCorrectPolyInTestSampleRatio() * 0.5 + getCorrectPolyInHaplogroupRatio() * 0.5);
	}

	public double getCorrectPolyInTestSampleRatio() {		
		return phyloSearchData.getWeightFoundPolys() / phyloSearchData.getUsedWeightPolys();
	}

	public double getCorrectPolyInHaplogroupRatio() {
		if(phyloSearchData.getExpectedWeightPolys() != 0)
			return phyloSearchData.getWeightFoundPolys() / phyloSearchData.getExpectedWeightPolys();
		else
			return 1;
	}	
	
	@Override
	public double getDistance() {
		return kylcinskiDistance;
	}
	
	@Override
	public void attachToJsonObject(JSONObject child) throws JSONException {
		DecimalFormat df = new DecimalFormat( "0.000",new DecimalFormatSymbols(Locale.US));
		child.put("rank",df.format(kylcinskiDistance));
		child.put("rankHG",df.format(getCorrectPolyInHaplogroupRatio()));
		child.put("rankS",df.format(getCorrectPolyInTestSampleRatio()));
		child.put("name",phyloSearchData.getHaplogroup().toString());
		child.put("id",phyloSearchData.getHaplogroup().toString());	
	}
}
