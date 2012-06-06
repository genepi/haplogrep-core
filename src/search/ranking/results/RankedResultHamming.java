package search.ranking.results;

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
}
