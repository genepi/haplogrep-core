package search.results;

import core.Haplogroup;
import search.SearchResultPerNode;

public class ResultKylcinski extends Result {

	double kylcinskiDistance;
	public ResultKylcinski( SearchResultPerNode phyloSearchData, double sumPhyloWeightsInSampleRange,
			Haplogroup expectedHaplogroup) {
		super(sumPhyloWeightsInSampleRange, phyloSearchData,expectedHaplogroup);
		kylcinskiDistance = calcDistance();
	}

	public int compareTo(Result o) {
		int delta = (int) Math.signum(kylcinskiDistance - ((ResultKylcinski)o).kylcinskiDistance);
			
		if(delta == 0)
			delta = super.compareTo(o);
		
		return delta;
	}

	public double calcDistance() {
		return (getCorrectPolyInTestSampleRatio() * 0.5 + getCorrectPolyInHaplogroupRatio() * 0.5);
	}

	public double getCorrectPolyInTestSampleRatio() {		
		return phyloSearchData.getSumCorrectWeights() / sumPhyloWeightsInSampleRange;
	}

	public double getCorrectPolyInHaplogroupRatio() {
		if(phyloSearchData.getSumExpectedPhyloWeights() != 0)
			return phyloSearchData.getSumCorrectWeights() / phyloSearchData.getSumExpectedPhyloWeights();
		else
			return 1;
	}	
	
	@Override
	public double getDistance() {
		return kylcinskiDistance;
	}
}
