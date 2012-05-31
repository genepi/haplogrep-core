package search;

public class SearchResultPerNode {

	private double usedWeightPolys = 0;
	private double remainingPolysSumWeights = 0;
	private double foundPolysSumWeights = 0;
	private double expectedPolsysSumWeight = 0;
	private double missingPolysSumWeights = 0;
	private double missingSumWeightsPolysOutOfRange = 0;
	
	public SearchResultPerNode()
	{
	}
	
	public SearchResultPerNode(SearchResultPerNode searchResultParent)
	{
		usedWeightPolys = searchResultParent.usedWeightPolys;
		foundPolysSumWeights = searchResultParent.foundPolysSumWeights;
		expectedPolsysSumWeight = searchResultParent.expectedPolsysSumWeight;
		missingSumWeightsPolysOutOfRange = searchResultParent.missingSumWeightsPolysOutOfRange;
		remainingPolysSumWeights = searchResultParent.remainingPolysSumWeights;
		missingPolysSumWeights = searchResultParent.missingPolysSumWeights;
	}
	

	public void addCorrectPhyloWeight(double phylogeneticWeight) {
		foundPolysSumWeights += phylogeneticWeight;	
		
	}
	
	public void removeCorrectPolyWeight(double phylogeneticWeight) {
		foundPolysSumWeights -= phylogeneticWeight;	
		
	}
	
	public void addNotInRangePhyloWeight(double phylogeneticWeight) {
		missingSumWeightsPolysOutOfRange += phylogeneticWeight;	
	}


	public void addExpectedPhyloWeight(double phylogeneticWeight) {
		expectedPolsysSumWeight += phylogeneticWeight;	
	}

	public void removeExpectedPhyloWeight(double phylogeneticWeight) {
		expectedPolsysSumWeight -= phylogeneticWeight;	
	}


}
