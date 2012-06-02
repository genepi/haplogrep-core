package search.results;

import core.Haplogroup;
import search.SearchResult;

public class ResultKylcinski extends Result {

	double kylcinskiDistance;
	public ResultKylcinski( SearchResult phyloSearchData,Haplogroup expectedHaplogroup) {
		super(phyloSearchData,expectedHaplogroup);
		kylcinskiDistance = calcDistance();
	}

	public int compareTo(Result o) {
		int delta = (int) Math.signum(((ResultKylcinski)o).kylcinskiDistance - kylcinskiDistance);
			
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
}
