package search;

import core.Haplogroup;

public class SearchResultPerNode {

	PhyloTreeNode attachedNode;
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

	public double getSumCorrectWeights() {
		return foundPolysSumWeights;
	}

	public double getSumExpectedPhyloWeights() {
		return expectedPolsysSumWeight;
	}

	public double getSumMissingPhyloWeight() {
		return missingPolysSumWeights;
	}

	public void addMissingPhyloWeight(double phylogeneticWeight) {
		missingPolysSumWeights += phylogeneticWeight;
	}
	
	public void removeMissingPhyloWeight(double phylogeneticWeight) {
		missingPolysSumWeights -= phylogeneticWeight;
	}

	public Haplogroup getHaplogroup() {
		return attachedNode.getHaplogroup();
	}

	public void attach(PhyloTreeNode node){
		attachedNode = node;
	}

}
