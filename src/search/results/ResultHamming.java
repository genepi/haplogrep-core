package search.results;

import core.Haplogroup;
import search.SearchResultPerNode;

public class ResultHamming extends Result {

	double hammingDistance;
	public ResultHamming(SearchResultPerNode phyloSearchData,double sumPhyloWeightsInSampleRange,
			Haplogroup expectedHaplogroup) {
		super(sumPhyloWeightsInSampleRange, phyloSearchData,expectedHaplogroup);
		hammingDistance = calcDistance();
	}

	public int compareTo(Result o) {
		int delta = (int)Math.signum(hammingDistance - ((ResultHamming)o).hammingDistance);
		
		if(delta == 0)
			delta = super.compareTo(o);
		
		return delta;
	}

	private double calcDistance() {
		return (sumPhyloWeightsInSampleRange - phyloSearchData.getSumCorrectWeights()) + phyloSearchData.getSumMissingPhyloWeight();
	}

	@Override
	public double getDistance() {
		return hammingDistance;
	}
}
