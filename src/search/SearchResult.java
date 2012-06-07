package search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.jdom.Element;
import org.json.JSONObject;

import phylotree.PhyloTreeNode;
import phylotree.Phylotree;

import core.Haplogroup;
import core.Polymorphism;
import core.Sample;
import core.TestSample;

/**
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 *
 */

public class SearchResult /*implements Comparable<SearchResult>*/{

	private PhyloTreeNode attachedPhyloTreeNode;
	private Sample sampleChecked = null;
	private SearchResultDetailed detailedResult = null;
	

	private double usedWeightPolys = 0;
	private double remainingPolysSumWeights = 0;
	private double foundPolysSumWeights = 0;
	private double expectedPolsysSumWeight = 0;
	private double missingPolysSumWeights = 0;
	private double missingSumWeightsPolysOutOfRange = 0;

	
	/**
	 * Creates a new SeachResult object with given haplogroup and test sample
	 * @param phyloNode The detected haplogroup
	 * @param parentResult
	 */
	public SearchResult(PhyloTreeNode phyloNode, TestSample parentResult) {
		this.sampleChecked = parentResult.getSample();
		this.attachedPhyloTreeNode = phyloNode;
		
			
		for (Polymorphism currentPoly : sampleChecked.getPolymorphismn()) {
			usedWeightPolys += getPhyloTree().getMutationRate(currentPoly);
			
			if(parentResult.getSample().getSampleRanges().contains(currentPoly)){
				remainingPolysSumWeights += getPhyloTree().getMutationRate(currentPoly);
			}
		}
		
		
		
		SearchResultTreeNode rootNode = new SearchResultTreeNode(phyloNode);
//		detailedResult.usedPath.add(rootNode);
		
	}

	
	/**
	 * Copy constructor. Copies a given result and sets a new haplogroup name
	 * @param newHaplogroup
	 * @param resultToCopy
	 */
	public SearchResult(String newHaplogroup,PhyloTreeNode phyloNode, SearchResult resultToCopy) {
		this.attachedPhyloTreeNode = phyloNode;
		this.sampleChecked = resultToCopy.sampleChecked;
//		this.detailedResult.foundPolys.addAll(resultToCopy.detailedResult.foundPolys);
//		this.detailedResult.remainingPolys.addAll(resultToCopy.detailedResult.remainingPolys);
//		this.detailedResult.correctedBackmutations.addAll(resultToCopy.detailedResult.correctedBackmutations);
//		this.detailedResult.remainingPolysNotInRange.addAll(resultToCopy.detailedResult.remainingPolysNotInRange);
//		this.detailedResult.missingPolysOutOfRange.addAll(resultToCopy.detailedResult.missingPolysOutOfRange);
//		this.detailedResult.usedPath =  new PhyloTreePath(resultToCopy.detailedResult.usedPath);
	
		usedWeightPolys = resultToCopy.usedWeightPolys;
		foundPolysSumWeights = resultToCopy.foundPolysSumWeights;
		expectedPolsysSumWeight = resultToCopy.expectedPolsysSumWeight;
		missingSumWeightsPolysOutOfRange = resultToCopy.missingSumWeightsPolysOutOfRange;
		remainingPolysSumWeights = resultToCopy.remainingPolysSumWeights;
		missingPolysSumWeights = resultToCopy.missingPolysSumWeights;
	}

	/**
	 * @return The detected haplogroup
	 */
	public Haplogroup getHaplogroup() {
		return attachedPhyloTreeNode.getHaplogroup();
	}

	/**
	 * @return The rank of this search result
	 */
	public double getRank() {
		return (getCorrectPolyInTestSampleRatio() * 0.5 + getCorrectPolyInHaplogroupRatio() * 0.5);
	}

	public double getCorrectPolyInTestSampleRatio() {		
		return foundPolysSumWeights / usedWeightPolys;
	}

	public double getCorrectPolyInHaplogroupRatio() {
		if(expectedPolsysSumWeight != 0)
			return foundPolysSumWeights / expectedPolsysSumWeight;
		else
			return 1;
	}

	/**
	 * @return A list of all correctly found polys of the detected haplogroup
	 */
//	public ArrayList<Polymorphism> getFoundPolys() {
//		return detailedResult.foundPolys;
//	}

//	public ArrayList<Polymorphism> getMissingPolysOutOfRange() {
//		return detailedResult.missingPolysOutOfRange;
//	}
	
	/**
	 * The sample a haplogroup has to be detected for
	 * @return
	 */
	public Sample getSample() {
		return sampleChecked;
	}

	public double getUsedWeightPolys() {
		return usedWeightPolys;
	}

	public double getWeightFoundPolys() {
		return foundPolysSumWeights;
	}

	public double getExpectedWeightPolys() {
		return expectedPolsysSumWeight;
	}
	


//	public void addFoundPoly(Polymorphism newFoundPoly) {	
//		detailedResult.foundPolys.add(newFoundPoly);
//		detailedResult.remainingPolys.remove(newFoundPoly);
//	}
	
	public void addFoundPolyWeight(Polymorphism newFoundPoly) {
		foundPolysSumWeights += getPhyloTree().getMutationRate(newFoundPoly);	
		remainingPolysSumWeights -= getPhyloTree().getMutationRate(newFoundPoly);
		missingPolysSumWeights -= getPhyloTree().getMutationRate(newFoundPoly);
	}

//	public void addExpectedPoly(Polymorphism newExpectedPoly) {
//		detailedResult.expectedPolys.add(newExpectedPoly);
//	}
	
	public void addExpectedPolyWeight(Polymorphism newExpectedPoly) {
		expectedPolsysSumWeight += getPhyloTree().getMutationRate(newExpectedPoly);
		missingPolysSumWeights += getPhyloTree().getMutationRate(newExpectedPoly);
	}
	//TODO Move to test getSearchManager() for incorrect backmutations
//	private void removeExpectedPoly(Polymorphism currentPoly, PhyloTreeNode currentElement) {
//		
//		Polymorphism found = null;
//		boolean foundPoly = false;
//		for(Polymorphism poly : detailedResult.expectedPolys)
//		{
//			if(poly.getPosition() == currentPoly.getPosition() && poly.getMutation() == currentPoly.getMutation()){
//				//expectedPolsysSumWeight -= searchManager.getMutationRate(detailedResult.expectedPolys.get(detailedResult.expectedPolys.indexOf(poly)));
//				removeExpectedPolyWeight(currentPoly);
//				found = poly;
//				foundPoly = true;
//				Polymorphism newPoly = new Polymorphism(currentPoly);
//				newPoly.setBackMutation(false);
//				
//				detailedResult.correctedBackmutations.add(new Polymorphism(newPoly));
//			}
//		}
//		if(!foundPoly){
//			System.out.println("Hansi: " + currentPoly);
//			PhyloTreeNode current = currentElement;
//			System.out.println("Path");
//			while(current != null){
//				System.out.print(current.getHaplogroup() + " ");
//				for(Polymorphism poly : current.getExpectedPolys())
//					System.out.print(poly + " ");
//				
//				System.out.println();
//				current = current.getParent();
//			}
//		}
//		detailedResult.expectedPolys.remove(found);
//		
//	}

	public void removeExpectedPolyWeight(Polymorphism polyToRemove) {
		Polymorphism newPoly = new Polymorphism(polyToRemove);
		newPoly.setBackMutation(false);
		expectedPolsysSumWeight -= getPhyloTree().getMutationRate(newPoly);
	}
	

//	public void removeFoundPoly(Polymorphism foundPoly) {
//		Polymorphism found = null;
//		
//		for(Polymorphism poly : detailedResult.foundPolys){
//		if(poly.getPosition() == foundPoly.getPosition() && poly.getMutation() == foundPoly.getMutation()){
////			foundPolysSumWeights -= searchManager.getMutationRate(detailedResult.foundPolys.get(detailedResult.foundPolys.indexOf(poly)));		
//			
//			if(!foundPoly.isBackMutation())
//				detailedResult.remainingPolys.add(foundPoly);
//			found = poly;
//			
//			Polymorphism newPoly = new Polymorphism(foundPoly);
//			newPoly.setBackMutation(false);
//			
//			detailedResult.correctedBackmutations.add(newPoly);
//		}
//		}
//		detailedResult.foundPolys.remove(found);
//		
//	}
	public void removeFoundPolyWeight(Polymorphism foundPoly,Sample sample){
		if(foundPoly.isBackMutation())
		{
			Polymorphism newPoly = new Polymorphism(foundPoly);
			newPoly.setBackMutation(false);
			if(sample.contains(newPoly)){
				foundPolysSumWeights -= getPhyloTree().getMutationRate(newPoly);		
				
			}
		}
		else
			foundPolysSumWeights -= getPhyloTree().getMutationRate(foundPoly);		
		
	}

	public void addMissingOutOfRangeWeight(Polymorphism correctPoly) {
		missingSumWeightsPolysOutOfRange += getPhyloTree().getMutationRate(correctPoly);
	}

	public void removeMissingOutOfRangeWeight(Polymorphism correctPoly) {
		missingSumWeightsPolysOutOfRange -= getPhyloTree().getMutationRate(correctPoly);
	}
	
//	public void addMissingOutOfRangePoly(Polymorphism correctPoly) {
//		detailedResult.missingPolysOutOfRange.add(correctPoly);
//	}
//
//	public void removeMissingOutOfRangePoly(Polymorphism correctPoly) {
//		detailedResult.missingPolysOutOfRange.add(correctPoly);
//	}

	/*
	public void addUnusedNotInRange(Polymorphism correctPoly) {
		unusedPolysNotInRange.add(correctPoly);
	}*/
	
//	public void setUnusedNotInRange(ArrayList<Polymorphism> polyNotinRange) {
//		detailedResult.remainingPolysNotInRange = polyNotinRange;
//		
//	}
	
	/* To sort SearchResults properly according to its rank
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
//	@Override
//	public int compareTo(SearchResult o) {
//		if (this.getRank() > o.getRank())
//			return -1;
//		if (this.getRank() < o.getRank())
//			return 1;
//		else
//			return 0;
//
//	}
	
	



	




	


	
	
	protected void finalize() throws Throwable {
	  // System.out.println(haplogroup +" " +  this.usedPolysInSample +  " freed");
	        super.finalize();
	   
	}

	
	
	


	public double getSumMissingPhyloWeight() {
		return missingPolysSumWeights;
	}
	public double getWeightRemainingPolys(){
		return remainingPolysSumWeights;
	}


	public SearchResultDetailed getDetailedResult() {
		if(detailedResult == null){
			detailedResult = new SearchResultDetailed(this);
			detailedResult.updateResult();
//			detailedResult.remainingPolys.addAll(sampleChecked.getPolymorphismn());
			
		}
		
		return detailedResult;
	}


	public PhyloTreeNode getAttachedPhyloTreeNode() {
		return attachedPhyloTreeNode;
	}	
	
	public Phylotree getPhyloTree(){
		return attachedPhyloTreeNode.getTree();
	}
}