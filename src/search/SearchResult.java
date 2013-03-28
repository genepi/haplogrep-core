package search;

import phylotree.PhyloTreeNode;
import phylotree.Phylotree;
import core.Haplogroup;
import core.Polymorphism;
import core.Sample;
import core.TestSample;

/**
 * Represents a search result attached to a phylotree node. Calculates data to
 * rank the results.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class SearchResult {

	private PhyloTreeNode attachedPhyloTreeNode;
	private Sample sampleChecked = null;
	private SearchResultDetailed detailedResult = null;

	private double sumWeightsAllPolysSample = 0;
	private double remainingPolysSumWeights = 0;
	private double foundPolysSumWeights = 0;
	private double expectedPolsysSumWeight = 0;
	private double missingPolysSumWeights = 0;
	private double missingSumWeightsPolysOutOfRange = 0;
	private double sumWeightedTransitions = 0;
	private double sumWeightedTransversions = 0;

	/**
	 * Creates a new SeachResult object with given haplogroup and test sample
	 * 
	 * @param phyloNode
	 *            The detected haplogroup
	 * @param parentResult
	 */
	public SearchResult(PhyloTreeNode phyloNode, TestSample parentResult) {
		this.sampleChecked = parentResult.getSample();
		this.attachedPhyloTreeNode = phyloNode;

		for (Polymorphism currentPoly : sampleChecked.getPolymorphisms()) {
			sumWeightsAllPolysSample += getPhyloTree().getMutationRate(currentPoly);

			if (parentResult.getSample().getSampleRanges().contains(currentPoly)) {
				remainingPolysSumWeights += getPhyloTree().getMutationRate(currentPoly);
			}
		}

	}

	/**
	 * Copy constructor. Copies a given result and sets a new haplogroup name
	 * 
	 * @param parentNode
	 *            The parent node of phylotree
	 * @param resultToCopy
	 *            The instance of SeachResult to copy
	 */
	public SearchResult(PhyloTreeNode parentNode, SearchResult resultToCopy) {
		this.attachedPhyloTreeNode = parentNode;
		this.sampleChecked = resultToCopy.sampleChecked;

		sumWeightsAllPolysSample = resultToCopy.sumWeightsAllPolysSample;
		foundPolysSumWeights = resultToCopy.foundPolysSumWeights;
		expectedPolsysSumWeight = resultToCopy.expectedPolsysSumWeight;
		missingSumWeightsPolysOutOfRange = resultToCopy.missingSumWeightsPolysOutOfRange;
		remainingPolysSumWeights = resultToCopy.remainingPolysSumWeights;
		missingPolysSumWeights = resultToCopy.missingPolysSumWeights;
		sumWeightedTransitions = resultToCopy.sumWeightedTransitions;
		sumWeightedTransversions = resultToCopy.sumWeightedTransversions;
	}

	/**
	 * @return The detected haplogroup
	 */
	public Haplogroup getHaplogroup() {
		return attachedPhyloTreeNode.getHaplogroup();
	}

	/**
	 * The sample a haplogroup has to be detected for
	 * 
	 * @return
	 */
	public Sample getSample() {
		return sampleChecked;
	}

	/**
	 * @return The sum of weights of all polymorphisms in the test sample
	 */
	public double getSumWeightsAllPolysSample() {
		return sumWeightsAllPolysSample;
	}

	/**
	 * @return The sum of weights of all polymorphisms found for this haplogroup
	 */
	public double getWeightFoundPolys() {
		return foundPolysSumWeights;
	}

	/**
	 * @return The sum of weights of all polymorphisms expected for this
	 *         haplogroup
	 */
	public double getExpectedWeightPolys() {
		return expectedPolsysSumWeight;
	}

	/**
	 * Adds the weight of a newly found polymorphism
	 * 
	 * @param newFoundPoly
	 *            The polymorphism the weight should be added
	 */
	public void addFoundPolyWeight(Polymorphism newFoundPoly) {
		foundPolysSumWeights += getPhyloTree().getMutationRate(newFoundPoly);
		remainingPolysSumWeights -= getPhyloTree().getMutationRate(newFoundPoly);
		missingPolysSumWeights -= getPhyloTree().getMutationRate(newFoundPoly);
		
		if(newFoundPoly.isTransitionPoly())
			sumWeightedTransitions -= 1;//getPhyloTree().getMutationRate(newFoundPoly);
		else
			sumWeightedTransversions -= 1;//getPhyloTree().getMutationRate(newFoundPoly);
	}

	/**
	 * Removes the weight of found polymorphism
	 * 
	 * @param polyToRemove
	 *            The polymorphism the weight should be removed
	 * @param sample
	 *            The test sample
	 */
	public void removeFoundPolyWeight(Polymorphism polyToRemove, Sample sample) {
		if (polyToRemove.isBackMutation()) {
			Polymorphism newPoly = new Polymorphism(polyToRemove);
			newPoly.setBackMutation(false);
			if (sample.contains(newPoly)) {
				foundPolysSumWeights -= getPhyloTree().getMutationRate(newPoly);
				remainingPolysSumWeights += getPhyloTree().getMutationRate(newPoly);
				
				if(newPoly.isTransitionPoly())
					sumWeightedTransitions += 1;//getPhyloTree().getMutationRate(newPoly);		
				else
					sumWeightedTransversions += 1;//getPhyloTree().getMutationRate(newPoly);
				
			}
		} else
			foundPolysSumWeights -= getPhyloTree().getMutationRate(polyToRemove);
	}

	/**
	 * Adds weight of newly expected polymorphism
	 * 
	 * @param newExpectedPoly
	 *            The polymorphism the weight should be added
	 */
	public void addExpectedPolyWeight(Polymorphism newExpectedPoly) {
		expectedPolsysSumWeight += getPhyloTree().getMutationRate(newExpectedPoly);
		missingPolysSumWeights += getPhyloTree().getMutationRate(newExpectedPoly);
		
//		if(newExpectedPoly.isTransitionPoly())
//			sumWeightedTransitions += 1;//getPhyloTree().getMutationRate(newExpectedPoly);		
//		else
//			sumWeightedTransversions += 1;//getPhyloTree().getMutationRate(newExpectedPoly);
	}

	
	/**
	 * Removes weight of a the expected polymorphisms
	 * 
	 * @param polyToRemove
	 *            The polymorphism the weight should be reduced
	 */
	public void removeExpectedPolyWeight(Polymorphism polyToRemove) {
		Polymorphism newPoly = new Polymorphism(polyToRemove);
		newPoly.setBackMutation(false);
		expectedPolsysSumWeight -= getPhyloTree().getMutationRate(newPoly);
	}

	/**
	 * Adds weight of out of sample range polymorphism
	 * 
	 * @param outOfRangePoly
	 *            The polymorphism the weight should be added
	 */
	public void addMissingOutOfRangeWeight(Polymorphism outOfRangePoly) {
		missingSumWeightsPolysOutOfRange += getPhyloTree().getMutationRate(outOfRangePoly);
	}

	/**
	 * Removes weight of a the out of range polymorphisms
	 * 
	 * @param outOfRangePoly
	 *            The polymorphism the weight should be reduced
	 */
	public void removeMissingOutOfRangeWeight(Polymorphism outOfRangePoly) {
		missingSumWeightsPolysOutOfRange -= getPhyloTree().getMutationRate(outOfRangePoly);
	}

	/**
	 * @return The sum of weight of all polymorphisms missing for this
	 *         haplogroup
	 */
	public double getSumMissingPhyloWeight() {
		return missingPolysSumWeights;
	}

	/**
	 * @return The sum of all weights remaining unused in the test sample
	 */
	public double getWeightRemainingPolys() {
		return remainingPolysSumWeights;
	}

	/**
	 * @return Returns the detailed result of this search result. Generated of
	 *         it's not existing yet.
	 */
	public SearchResultDetailed getDetailedResult() {
		if (detailedResult == null) {
			detailedResult = new SearchResultDetailed(this);
			detailedResult.updateResult();
		}

		return detailedResult;
	}

	/**
	 * @return The node in the phylotree this search result is attached to.
	 */
	public PhyloTreeNode getAttachedPhyloTreeNode() {
		return attachedPhyloTreeNode;
	}

	/**
	 * @return The instance of the phylotree used to generate this search result instance.
	 */
	public Phylotree getPhyloTree() {
		return attachedPhyloTreeNode.getTree();
	}

	public double getSumWeightedTransversions() {
		return sumWeightedTransversions;
	}
	
	public double getSumWeightedTransitions() {
		return sumWeightedTransitions;
	}
	
	
}