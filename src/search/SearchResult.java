package search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.jdom.Element;

import phylotree.PhyloTreeNode;
import phylotree.Phylotree;

import core.Haplogroup;
import core.Polymorphism;
import core.Sample;
import core.TestSample;

/**
 * @author Dominic Pacher, Sebastian Sch�nherr, Hansi Weissensteiner
 *
 */

public class SearchResult implements Comparable<SearchResult> {
	private Haplogroup haplogroup;
	private SearchResultDetailed detailedResult = new SearchResultDetailed(this);
	private Sample usedPolysInSample = null;

	private double usedWeightPolys = 0;
	private double remainingPolysSumWeights = 0;
	private double foundPolysSumWeights = 0;
	private double expectedPolsysSumWeight = 0;
	private double missingPolysSumWeights = 0;
	private double missingSumWeightsPolysOutOfRange = 0;

//	protected String phyolTreeString;
	protected Phylotree searchManager;
	/**
	 * Creates a new SeachResult object with given haplogroup and test sample
	 * @param phyloNode The detected haplogroup
	 * @param parentResult
	 */
	public SearchResult(Phylotree phyloTree,PhyloTreeNode phyloNode, TestSample parentResult) {
		this.haplogroup = phyloNode.getHaplogroup();
		this.usedPolysInSample = parentResult.getSample();
		this.searchManager = phyloTree;
		
		
		detailedResult.remainingPolys.addAll(usedPolysInSample.getPolymorphismn());
		
		for (Polymorphism currentPoly : usedPolysInSample.getPolymorphismn()) {
			usedWeightPolys += phyloTree.getMutationRate(currentPoly);
			
			if(parentResult.getSample().getSampleRanges().contains(currentPoly)){
				remainingPolysSumWeights += phyloTree.getMutationRate(currentPoly);
			}
		}
		
		
		
		SearchResultTreeNode rootNode = new SearchResultTreeNode(phyloNode);
		detailedResult.usedPath.add(rootNode);
		detailedResult.phyloNode = phyloNode;
	}

	
	/**
	 * Copy constructor. Copies a given result and sets a new haplogroup name
	 * @param newHaplogroup
	 * @param resultToCopy
	 */
	public SearchResult(String newHaplogroup,PhyloTreeNode phyloNode, SearchResult resultToCopy) {
		this.haplogroup = new Haplogroup(newHaplogroup);
		this.usedPolysInSample = resultToCopy.usedPolysInSample;
		this.detailedResult.expectedPolys.addAll(resultToCopy.detailedResult.expectedPolys);
		this.detailedResult.foundPolys.addAll(resultToCopy.detailedResult.foundPolys);
		this.detailedResult.remainingPolys.addAll(resultToCopy.detailedResult.remainingPolys);
		this.detailedResult.correctedBackmutations.addAll(resultToCopy.detailedResult.correctedBackmutations);
		this.detailedResult.remainingPolysNotInRange.addAll(resultToCopy.detailedResult.remainingPolysNotInRange);
		this.detailedResult.missingPolysOutOfRange.addAll(resultToCopy.detailedResult.missingPolysOutOfRange);
//		this.detailedResult.missingPolys.addAll(resultToCopy.detailedResult.missingPolys);
		this.detailedResult.usedPath =  new PhyloTreePath(resultToCopy.detailedResult.usedPath);
		this.searchManager=resultToCopy.searchManager;
		detailedResult.phyloNode = phyloNode;
		
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
		return haplogroup;
	}

	/**
	 * @return A list of all polys checked in the phylo xml tree
	 */
	public ArrayList<Polymorphism> getCheckedPolys() {
		return detailedResult.expectedPolys;
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
	public ArrayList<Polymorphism> getFoundPolys() {
		return detailedResult.foundPolys;
	}

	public ArrayList<Polymorphism> getMissingPolysOutOfRange() {
		return detailedResult.missingPolysOutOfRange;
	}
	
	/**
	 * The sample a haplogroup has to be detected for
	 * @return
	 */
	public Sample getSample() {
		return usedPolysInSample;
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
	
	public double getHammingDistance(){
		return remainingPolysSumWeights + missingPolysSumWeights;
	}

	public void addFoundPoly(Polymorphism newFoundPoly) {	
		detailedResult.foundPolys.add(newFoundPoly);
		detailedResult.remainingPolys.remove(newFoundPoly);
	}
	
	public void addFoundPolyWeight(Polymorphism newFoundPoly) {
		foundPolysSumWeights += searchManager.getMutationRate(newFoundPoly);	
		remainingPolysSumWeights -= searchManager.getMutationRate(newFoundPoly);
		missingPolysSumWeights -= searchManager.getMutationRate(newFoundPoly);
	}

	public void addExpectedPoly(Polymorphism newExpectedPoly) {
		detailedResult.expectedPolys.add(newExpectedPoly);
	}
	
	public void addExpectedPolyWeight(Polymorphism newExpectedPoly) {
		expectedPolsysSumWeight += searchManager.getMutationRate(newExpectedPoly);
		missingPolysSumWeights += searchManager.getMutationRate(newExpectedPoly);
	}

	public void removeExpectedPoly(Polymorphism currentPoly) {
		
		Polymorphism found = null;
		boolean foundPoly = false;
		for(Polymorphism poly : detailedResult.expectedPolys)
		{
			if(poly.getPosition() == currentPoly.getPosition() && poly.getMutation() == currentPoly.getMutation()){
				//expectedPolsysSumWeight -= searchManager.getMutationRate(detailedResult.expectedPolys.get(detailedResult.expectedPolys.indexOf(poly)));
				removeExpectedPolyWeight(currentPoly);
				found = poly;
				foundPoly = true;
				Polymorphism newPoly = new Polymorphism(currentPoly);
				newPoly.setBackMutation(false);
				
				detailedResult.correctedBackmutations.add(new Polymorphism(newPoly));
			}
		}
//		if(!foundPoly)
//			System.out.println("Hansi: " + currentPoly);
		detailedResult.expectedPolys.remove(found);
		
	}

	public void removeExpectedPolyWeight(Polymorphism polyToRemove){
		if(polyToRemove.isBackMutation())
		{
			Polymorphism newPoly = new Polymorphism(polyToRemove);
			newPoly.setBackMutation(false);
//			double expectedPolsysSumWeight2= expectedPolsysSumWeight;
			expectedPolsysSumWeight -= searchManager.getMutationRate(newPoly);
		}
//		else
//			expectedPolsysSumWeight -= searchManager.getMutationRate(polyToRemove);
	}
	public void removeFoundPoly(Polymorphism foundPoly) {
		Polymorphism found = null;
		
		for(Polymorphism poly : detailedResult.foundPolys){
		if(poly.getPosition() == foundPoly.getPosition() && poly.getMutation() == foundPoly.getMutation()){
//			foundPolysSumWeights -= searchManager.getMutationRate(detailedResult.foundPolys.get(detailedResult.foundPolys.indexOf(poly)));		
			
			if(!foundPoly.isBackMutation())
				detailedResult.remainingPolys.add(foundPoly);
			found = poly;
			
			Polymorphism newPoly = new Polymorphism(foundPoly);
			newPoly.setBackMutation(false);
			
			detailedResult.correctedBackmutations.add(newPoly);
		}
		}
		detailedResult.foundPolys.remove(found);
		
	}
	public void removeFoundPolyWeight(Polymorphism foundPoly,Sample sample){
		if(foundPoly.isBackMutation())
		{
			Polymorphism newPoly = new Polymorphism(foundPoly);
			newPoly.setBackMutation(false);
			if(sample.contains(newPoly)){
				foundPolysSumWeights -= searchManager.getMutationRate(newPoly);		
				
			}
		}
		else
			foundPolysSumWeights -= searchManager.getMutationRate(foundPoly);		
		
	}

	public void addMissingOutOfRangePoly(Polymorphism correctPoly) {
		missingSumWeightsPolysOutOfRange += searchManager.getMutationRate(correctPoly);
		detailedResult.missingPolysOutOfRange.add(correctPoly);
	}

	public void removeMissingOutOfRangePoly(Polymorphism correctPoly) {
		missingSumWeightsPolysOutOfRange -= searchManager.getMutationRate(correctPoly);
		detailedResult.missingPolysOutOfRange.add(correctPoly);
	}

	/*
	public void addUnusedNotInRange(Polymorphism correctPoly) {
		unusedPolysNotInRange.add(correctPoly);
	}*/
	
	public void setUnusedNotInRange(ArrayList<Polymorphism> polyNotinRange) {
		detailedResult.remainingPolysNotInRange = polyNotinRange;
		
	}
	
	/* To sort SearchResults properly according to its rank
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchResult o) {
		if (this.getRank() > o.getRank())
			return -1;
		if (this.getRank() < o.getRank())
			return 1;
		else
			return 0;

	}
	
	public Element getUnusedPolysXML(PhyloTreePath phyloTreePath, boolean includeHotspots)
	{
		Element results = new Element("DetailedResults");
		Collections.sort(detailedResult.remainingPolys);
		
		ArrayList<Polymorphism> expectedPolysSuperGroup = new ArrayList<Polymorphism>();
		
		for(int i = 0; i < phyloTreePath.getNodes().size()-1;i++)
		 expectedPolysSuperGroup.addAll(phyloTreePath.getNodes().get(i).getExpectedPolys());
		
		
		ArrayList<Polymorphism> unusedPolysWithBackmutations = new ArrayList<Polymorphism>();
		unusedPolysWithBackmutations.addAll(detailedResult.remainingPolys);
		
		for(Polymorphism currentPoly : detailedResult.expectedPolys){
			if(!detailedResult.foundPolys.contains(currentPoly)){
				if(expectedPolysSuperGroup.contains(currentPoly)){
					Polymorphism p = new Polymorphism(currentPoly);
					p.setBackMutation(true);
					unusedPolysWithBackmutations.add(p);
				}
			}
		}
		
		Collections.sort(unusedPolysWithBackmutations);
		
			
		for (Polymorphism currentPoly : unusedPolysWithBackmutations) {

			Element result = new Element("DetailedResult");
			Element newUnusedPoly = new Element("unused");
			newUnusedPoly.setText(currentPoly.toStringShortVersion());
			
			
			
			/*Element weightUnusedPoly = new Element("weight");
			weightUnusedPoly.setText(String.valueOf(currentPoly.getMutationRate()));
			result.addContent(weightUnusedPoly);*/
			
			Element reasonUnusedPoly = new Element("reasonUnused");
			
			
			if(searchManager.getMutationRate(currentPoly) == 0)
			{
				if(currentPoly.isBackMutation()){
					reasonUnusedPoly.setText("globalPrivateMutation");
					newUnusedPoly.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
					result.addContent(reasonUnusedPoly);
					result.addContent(newUnusedPoly);
					results.addContent(result);
				}
				
				else if(currentPoly.isMTHotspot()){
					
					if(includeHotspots){
					reasonUnusedPoly.setText("hotspot");
					result.addContent(reasonUnusedPoly);
					result.addContent(newUnusedPoly);
					results.addContent(result);
					}
				}
				
				else{
					reasonUnusedPoly.setText("globalPrivateMutation");
							
					result.addContent(reasonUnusedPoly);
					result.addContent(newUnusedPoly);
					results.addContent(result);
				}
				
			}
			
			else
			{
				if(this.detailedResult.remainingPolysNotInRange.contains(currentPoly))
					reasonUnusedPoly.setText("polyoutofrange");
				else
				reasonUnusedPoly.setText("localPrivateMutation");
				
				result.addContent(newUnusedPoly);
				result.addContent(reasonUnusedPoly);
				results.addContent(result);
			}
			
			
			
			
			
		}
		
		
		
		return results;
	
	}
//	public Element getUnusedPolysXML(boolean includeHotspots,boolean includeMissingPolys)
//	{
//		Element results = new Element("DetailedResults");
//		Collections.sort(unusedPolys);
//		
//		if(includeMissingPolys)
//		for(Polymorphism currentPoly : allCheckedPolys){
//			if(!correctPolys.contains(currentPoly)){
//			
//				//Polymorphism p = new Polymorphism(currentPoly);
//				//p.setBackMutation(true);
//				
//				
//				
//				Element result = new Element("DetailedResult");
//				Element newUnusedPoly = new Element("unused");
//				newUnusedPoly.setText("mis" + currentPoly.toStringShortVersion());
//				result.addContent(newUnusedPoly);
//				
//			
//				
//				Element reasonUnusedPoly = new Element("reasonUnused");
//				reasonUnusedPoly.setText("globalPrivateMutation");
//				result.addContent(reasonUnusedPoly);
//				results.addContent(result);
//				
//			}
//		}
//			
//		for (Polymorphism currentPoly : unusedPolys) {
//
//			Element result = new Element("DetailedResult");
//			Element newUnusedPoly = new Element("unused");
//			newUnusedPoly.setText(currentPoly.toStringShortVersion());
//			
//			
//			
//			/*Element weightUnusedPoly = new Element("weight");
//			weightUnusedPoly.setText(String.valueOf(currentPoly.getMutationRate()));
//			result.addContent(weightUnusedPoly);*/
//			
//			Element reasonUnusedPoly = new Element("reasonUnused");
//			
//			
//			if(currentPoly.getMutationRate() == 0)
//			{
//				if(currentPoly.isMTHotspot()){
//					
//					if(includeHotspots){
//					reasonUnusedPoly.setText("hotspot");
//					result.addContent(reasonUnusedPoly);
//					result.addContent(newUnusedPoly);
//					results.addContent(result);
//					}
//				}
//				
//				else{
//					reasonUnusedPoly.setText("globalPrivateMutation");
//							
//					result.addContent(reasonUnusedPoly);
//					result.addContent(newUnusedPoly);
//					results.addContent(result);
//				}
//				
//			}
//			else
//			{
//				if(this.unusedPolysNotInRange.contains(currentPoly))
//					reasonUnusedPoly.setText("polyoutofrange");
//				else
//				reasonUnusedPoly.setText("localPrivateMutation");
//				
//				result.addContent(newUnusedPoly);
//				result.addContent(reasonUnusedPoly);
//				results.addContent(result);
//			}
//			
//			
//			
//			
//			
//		}
//		
//		
//		
//		return results;
//	
//	}
	
	public Element toXML()
	{
		
		Element results = new Element("DetailedResults");
		Collections.sort(detailedResult.expectedPolys);
		
		
		ArrayList<Polymorphism> unusedPolysArray = new ArrayList<Polymorphism>();
		unusedPolysArray.addAll(usedPolysInSample.getPolymorphismn());
		
		for(Polymorphism current : detailedResult.expectedPolys)
		{
			
			
			//The polymorphism is contained in this haplogroup
			if(!detailedResult.foundPolys.contains(current))
			{
				Element result = new Element("DetailedResult");
				
				Element newExpectedPoly = new Element("expected");				
				newExpectedPoly.setText(current.toString());
				result.addContent(newExpectedPoly);
				
				Element newCorrectPoly = new Element("correct");				
				newCorrectPoly.setText("no");
				result.addContent(newCorrectPoly);
				
				results.addContent(result);
			}	
						
		}
		
		
		
		for(Polymorphism current : detailedResult.expectedPolys)
		{
			
			//The polymorphism is  contained in this haplogroup
			if(detailedResult.foundPolys.contains(current))
			{
				Element result = new Element("DetailedResult");
				
				Element newExpectedPoly = new Element("expected");				
				newExpectedPoly.setText(current.toString());
				result.addContent(newExpectedPoly);
				
				Element newCorrectPoly = new Element("correct");				
				newCorrectPoly.setText("yes");
				result.addContent(newCorrectPoly);
				unusedPolysArray.remove(current);
				results.addContent(result);
			}	

			
		}
		
		
		
		return results;
	}


	public Element getNotInRangePolysXML() {
		Element results = new Element("OutOfRangePolys");
		Collections.sort(detailedResult.missingPolysOutOfRange);
		
			
		for (Polymorphism currentPoly : detailedResult.missingPolysOutOfRange) {

			Element result = new Element("OutOfRangePoly");
			Element newUnusedPoly = new Element("poly");
			newUnusedPoly.setText(currentPoly.toString());
			result.addContent(newUnusedPoly);
			
			Element weightUnusedPoly = new Element("weight");
			weightUnusedPoly.setText(String.valueOf(searchManager.getMutationRate(currentPoly)));
			result.addContent(weightUnusedPoly);
			
			

			results.addContent(result);
		}
		
		
		
		return results;
	}



	public PhyloTreePath getUsedPath() {
		
		return detailedResult.usedPath;
		
	}


	public void extendPath(SearchResultTreeNode newNode) {
		detailedResult.usedPath.add(newNode);
		
	}


	public ArrayList<Polymorphism> getCorrectedBackmutations() {
		return detailedResult.correctedBackmutations;
	}


	public PhyloTreePath getPhyloTreePath() {
		return detailedResult.usedPath;
	}
	
	protected void finalize() throws Throwable {
	  // System.out.println(haplogroup +" " +  this.usedPolysInSample +  " freed");
	        super.finalize();
	   
	}

	public MissingPolysIterator getIterMissingPolys(){
		return new MissingPolysIterator(this);
	}


	public Iterator<Polymorphism> getIterExpectedPolys() {
		return detailedResult.expectedPolys.iterator();	
	}
	
	public ArrayList<Polymorphism> getUnusedPolys(){
		return detailedResult.remainingPolys;
	}


	public double getSumMissingPhyloWeight() {
		return missingPolysSumWeights;
	}
	public double getWeightRemainingPolys(){
		return remainingPolysSumWeights;
	}


	public Phylotree getSearchMananger() {
		return searchManager;
	}


	public SearchResultDetailed getDetailedResult() {
		return detailedResult;
	}
	
	
}