package search.results;

import core.Haplogroup;
import search.SearchResult;

public class ResultHamming extends Result {


	double hammingDistance;
	public ResultHamming(SearchResult result,Haplogroup expectedHaplogroup) {
		super(result,expectedHaplogroup);
		hammingDistance = calcDistance();
	}

	public int compareTo(Result o) {
		int delta = (int)Math.signum(hammingDistance - ((ResultHamming)o).hammingDistance);
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
}
