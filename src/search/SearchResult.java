//package search;
//
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Iterator;
//
//import org.jdom.Element;
//
//import phylotree.PhyloTree;
//
//import core.Haplogroup;
//import core.Polymorphism;
//import core.Sample;
//import core.TestSample;
//
///**
// * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
// *
// */
//
//public class SearchResult implements Comparable<SearchResult> {
////	private Haplogroup haplogroup;
//	private PhyloTree phylotree;
//	private ArrayList<SearchResultPerNode> usedPath = new ArrayList<SearchResultPerNode>();
////	private ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();
//	private ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
//	private ArrayList<Polymorphism> remainingPolys = new ArrayList<Polymorphism>();
//	private ArrayList<Polymorphism> remainingPolysNotInRange = new ArrayList<Polymorphism>();
//	private ArrayList<Polymorphism> correctedBackmutations = new ArrayList<Polymorphism>();	
//	private ArrayList<Polymorphism> missingPolysOutOfRange = new ArrayList<Polymorphism>();
//	private HashSet<Polymorphism> missingPolys = new  HashSet<Polymorphism>();
//	
//	
//	
//	private Sample usedPolysInSample = null;
//
//	private double usedWeightPolys = 0;
//	private double remainingPolysSumWeights = 0;
//	private double foundPolysSumWeights = 0;
//	private double expectedPolsysSumWeight = 0;
//	private double missingPolysSumWeights = 0;
//	private double missingSumWeightsPolysOutOfRange = 0;
//
//	
//	
//	
//	/**
//	 * Creates a new SeachResult object with given haplogroup and test sample
//	 * @param haplogroup The detected haplogroup
//	 * @param polysInTestSample
//	 */
//	public SearchResult(PhyloTreeNode phyloNode,PhyloTree phylotree, TestSample polysInTestSample) {
////		this.haplogroup = new Haplogroup(haplogroup);
//		this.usedPolysInSample = polysInTestSample.getSample();
////		this.phyolTreeString = phyolTreeString;
//		this.phylotree = phylotree;
//		
//		remainingPolys.addAll(usedPolysInSample.getPolymorphismn());
//		
//		for (Polymorphism currentPoly : usedPolysInSample.getPolymorphismn()) {
//			usedWeightPolys += phylotree.getPhylogeneticWeight(currentPoly);
//			
//			if(polysInTestSample.getSampleRanges().contains(currentPoly)){
//				remainingPolysSumWeights += phylotree.getPhylogeneticWeight(currentPoly);
//			}
//		}
//		
//		
//		
//		SearchResultPerNode resultNode = new SearchResultPerNode(phyloNode);
//		usedPath.add(resultNode);
//	}
//
//	
//	/**
//	 * Copy constructor. Copies a given result and sets a new haplogroup name
//	 * @param newHaplogroup
//	 * @param resultToCopy
//	 */
//	public SearchResult(PhyloTreeNode phyloNode, SearchResult resultToCopy) {
////		this.haplogroup = new Haplogroup(newHaplogroup);
//		this.usedPolysInSample = resultToCopy.usedPolysInSample;
////		this.expectedPolys.addAll(resultToCopy.expectedPolys);
//		this.foundPolys.addAll(resultToCopy.foundPolys);
//		this.remainingPolys.addAll(resultToCopy.remainingPolys);
//		this.correctedBackmutations.addAll(resultToCopy.correctedBackmutations);
//		this.remainingPolysNotInRange.addAll(resultToCopy.remainingPolysNotInRange);
//		this.missingPolysOutOfRange.addAll(resultToCopy.missingPolysOutOfRange);
//		this.missingPolys.addAll(resultToCopy.missingPolys);
//		this.usedPath.addAll(resultToCopy.usedPath);
//		this.usedPath.add(new SearchResultPerNode(phyloNode));
////		this.phyolTreeString=phyolTreeString;
//		
//		usedWeightPolys = resultToCopy.usedWeightPolys;
//		foundPolysSumWeights = resultToCopy.foundPolysSumWeights;
//		expectedPolsysSumWeight = resultToCopy.expectedPolsysSumWeight;
//		missingSumWeightsPolysOutOfRange = resultToCopy.missingSumWeightsPolysOutOfRange;
//		remainingPolysSumWeights = resultToCopy.remainingPolysSumWeights;
//		missingPolysSumWeights = resultToCopy.missingPolysSumWeights;
//	}
//
//	/**
//	 * @return The detected haplogroup
//	 */
//	public Haplogroup getHaplogroup() {
//		return usedPath.get(usedPath.size() - 1).getHaplogroup();
//	}
//
//	/**
//	 * @return A list of all polys checked in the phylo xml tree
//	 */
//	public ArrayList<Polymorphism> getCheckedPolys() {
//		return expectedPolys;
//	}
//
//	/**
//	 * @return The rank of this search result
//	 */
//	public double getRank() {
//		return (getCorrectPolyInTestSampleRatio() * 0.5 + getCorrectPolyInHaplogroupRatio() * 0.5);
//	}
//
//	public double getCorrectPolyInTestSampleRatio() {		
//		return foundPolysSumWeights / usedWeightPolys;
//	}
//
//	public double getCorrectPolyInHaplogroupRatio() {
//		if(expectedPolsysSumWeight != 0)
//			return foundPolysSumWeights / expectedPolsysSumWeight;
//		else
//			return 1;
//	}
//
//	/**
//	 * @return A list of all correctly found polys of the detected haplogroup
//	 */
//	public ArrayList<Polymorphism> getFoundPolys() {
//		return foundPolys;
//	}
//
//	public ArrayList<Polymorphism> getMissingPolysOutOfRange() {
//		return missingPolysOutOfRange;
//	}
//	
//	/**
//	 * The sample a haplogroup has to be detected for
//	 * @return
//	 */
//	public Sample getSample() {
//		return usedPolysInSample;
//	}
//
//	public double getUsedWeightPolys() {
//		return usedWeightPolys;
//	}
//
//	public double getWeightFoundPolys() {
//		return foundPolysSumWeights;
//	}
//
//	public double getExpectedWeightPolys() {
//		return expectedPolsysSumWeight;
//	}
//	
//	public double getHammingDistance(){
//		return remainingPolysSumWeights + missingPolysSumWeights;
//	}
//
//	public void addFoundPoly(Polymorphism newFoundPoly) {
//		foundPolysSumWeights += phylotree.getPhylogeneticWeight(newFoundPoly);
//		foundPolys.add(newFoundPoly);
//		remainingPolys.remove(newFoundPoly);
//		remainingPolysSumWeights -= phylotree.getPhylogeneticWeight(newFoundPoly);;
//		
//		missingPolys.remove(newFoundPoly);
//		missingPolysSumWeights -= phylotree.getPhylogeneticWeight(newFoundPoly);
//	}
//
//	public void addExpectedPoly(Polymorphism newExpectedPoly) {
//		expectedPolsysSumWeight += phylotree.getPhylogeneticWeight(newExpectedPoly);
//		expectedPolys.add(newExpectedPoly);
//		
//		missingPolys.add(newExpectedPoly);
//		missingPolysSumWeights += phylotree.getPhylogeneticWeight(newExpectedPoly);
//	}
//
//	public void removeExpectedPoly(Polymorphism currentPoly) {
//		
//		Polymorphism found = null;
//		for(Polymorphism poly : expectedPolys)
//		{
//			if(poly.getPosition() == currentPoly.getPosition() && poly.getMutation() == currentPoly.getMutation()){
//				expectedPolsysSumWeight -= phylotree.getPhylogeneticWeight(expectedPolys.get(expectedPolys.indexOf(poly)));
//				found = poly;
//				
//				Polymorphism newPoly = new Polymorphism(currentPoly);
//				newPoly.setBackMutation(false);
//				
//				correctedBackmutations.add(new Polymorphism(newPoly));
//			}
//		}
//		
//		expectedPolys.remove(found);
//		
//	}
//
//	public void removeFoundPoly(Polymorphism foundPoly) {
//		Polymorphism found = null;
//		
//		for(Polymorphism poly : foundPolys){
//		if(poly.getPosition() == foundPoly.getPosition() && poly.getMutation() == foundPoly.getMutation()){
//			foundPolysSumWeights -= phylotree.getPhylogeneticWeight(foundPolys.get(foundPolys.indexOf(poly)));	
//			
//			if(!foundPoly.isBackMutation())
//				remainingPolys.add(foundPoly);
//			found = poly;
//			
//			Polymorphism newPoly = new Polymorphism(foundPoly);
//			newPoly.setBackMutation(false);
//			
//			correctedBackmutations.add(newPoly);
//		}
//		}
//		foundPolys.remove(found);
//		
//	}
//	
//
//	public void addMissingOutOfRangePoly(Polymorphism correctPoly) {
//		missingSumWeightsPolysOutOfRange += phylotree.getPhylogeneticWeight(correctPoly);
//		missingPolysOutOfRange.add(correctPoly);
//	}
//
//	public void removeMissingOutOfRangePoly(Polymorphism correctPoly) {
//		missingSumWeightsPolysOutOfRange -= phylotree.getPhylogeneticWeight(correctPoly);
//		missingPolysOutOfRange.add(correctPoly);
//	}
//
//	/*
//	public void addUnusedNotInRange(Polymorphism correctPoly) {
//		unusedPolysNotInRange.add(correctPoly);
//	}*/
//	
//	public void setUnusedNotInRange(ArrayList<Polymorphism> polyNotinRange) {
//		remainingPolysNotInRange = polyNotinRange;
//		
//	}
//	
//	/* To sort SearchResults properly according to its rank
//	 * (non-Javadoc)
//	 * @see java.lang.Comparable#compareTo(java.lang.Object)
//	 */
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
//	
//	public Element getUnusedPolysXML(boolean includeHotspots)
//	{
//		Element results = new Element("DetailedResults");
//		Collections.sort(remainingPolys);
//		
//		ArrayList<Polymorphism> expectedPolysSuperGroup = new ArrayList<Polymorphism>();
//		
//		for(int i = 0; i < usedPath.size()-1;i++)
//		 expectedPolysSuperGroup.addAll(usedPath.get(i).getExpectedPolys());
//		
//		
//		ArrayList<Polymorphism> unusedPolysWithBackmutations = new ArrayList<Polymorphism>();
//		unusedPolysWithBackmutations.addAll(remainingPolys);
//		
//		for(Polymorphism currentPoly : expectedPolys){
//			if(!foundPolys.contains(currentPoly)){
//				if(expectedPolysSuperGroup.contains(currentPoly)){
//					Polymorphism p = new Polymorphism(currentPoly);
//					p.setBackMutation(true);
//					unusedPolysWithBackmutations.add(p);
//				}
//			}
//		}
//		
//		Collections.sort(unusedPolysWithBackmutations);
//		
//			
//		for (Polymorphism currentPoly : unusedPolysWithBackmutations) {
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
//			if(phylotree.getPhylogeneticWeight(currentPoly) == 0)
//			{
//				if(currentPoly.isBackMutation()){
//					reasonUnusedPoly.setText("globalPrivateMutation");
//					newUnusedPoly.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
//					result.addContent(reasonUnusedPoly);
//					result.addContent(newUnusedPoly);
//					results.addContent(result);
//				}
//				
//				else if(currentPoly.isMTHotspot()){
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
//			
//			else
//			{
//				if(this.remainingPolysNotInRange.contains(currentPoly))
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
////	public Element getUnusedPolysXML(boolean includeHotspots,boolean includeMissingPolys)
////	{
////		Element results = new Element("DetailedResults");
////		Collections.sort(unusedPolys);
////		
////		if(includeMissingPolys)
////		for(Polymorphism currentPoly : allCheckedPolys){
////			if(!correctPolys.contains(currentPoly)){
////			
////				//Polymorphism p = new Polymorphism(currentPoly);
////				//p.setBackMutation(true);
////				
////				
////				
////				Element result = new Element("DetailedResult");
////				Element newUnusedPoly = new Element("unused");
////				newUnusedPoly.setText("mis" + currentPoly.toStringShortVersion());
////				result.addContent(newUnusedPoly);
////				
////			
////				
////				Element reasonUnusedPoly = new Element("reasonUnused");
////				reasonUnusedPoly.setText("globalPrivateMutation");
////				result.addContent(reasonUnusedPoly);
////				results.addContent(result);
////				
////			}
////		}
////			
////		for (Polymorphism currentPoly : unusedPolys) {
////
////			Element result = new Element("DetailedResult");
////			Element newUnusedPoly = new Element("unused");
////			newUnusedPoly.setText(currentPoly.toStringShortVersion());
////			
////			
////			
////			/*Element weightUnusedPoly = new Element("weight");
////			weightUnusedPoly.setText(String.valueOf(currentPoly.getMutationRate()));
////			result.addContent(weightUnusedPoly);*/
////			
////			Element reasonUnusedPoly = new Element("reasonUnused");
////			
////			
////			if(currentPoly.getMutationRate() == 0)
////			{
////				if(currentPoly.isMTHotspot()){
////					
////					if(includeHotspots){
////					reasonUnusedPoly.setText("hotspot");
////					result.addContent(reasonUnusedPoly);
////					result.addContent(newUnusedPoly);
////					results.addContent(result);
////					}
////				}
////				
////				else{
////					reasonUnusedPoly.setText("globalPrivateMutation");
////							
////					result.addContent(reasonUnusedPoly);
////					result.addContent(newUnusedPoly);
////					results.addContent(result);
////				}
////				
////			}
////			else
////			{
////				if(this.unusedPolysNotInRange.contains(currentPoly))
////					reasonUnusedPoly.setText("polyoutofrange");
////				else
////				reasonUnusedPoly.setText("localPrivateMutation");
////				
////				result.addContent(newUnusedPoly);
////				result.addContent(reasonUnusedPoly);
////				results.addContent(result);
////			}
////			
////			
////			
////			
////			
////		}
////		
////		
////		
////		return results;
////	
////	}
//	
//	public Element toXML()
//	{
//		
//		Element results = new Element("DetailedResults");
//		Collections.sort(expectedPolys);
//		
//		
//		ArrayList<Polymorphism> unusedPolysArray = new ArrayList<Polymorphism>();
//		unusedPolysArray.addAll(usedPolysInSample.getPolymorphismn());
//		
//		for(Polymorphism current : expectedPolys)
//		{
//			
//			
//			//The polymorphism is contained in this haplogroup
//			if(!foundPolys.contains(current))
//			{
//				Element result = new Element("DetailedResult");
//				
//				Element newExpectedPoly = new Element("expected");				
//				newExpectedPoly.setText(current.toString());
//				result.addContent(newExpectedPoly);
//				
//				Element newCorrectPoly = new Element("correct");				
//				newCorrectPoly.setText("no");
//				result.addContent(newCorrectPoly);
//				
//				results.addContent(result);
//			}	
//						
//		}
//		
//		
//		
//		for(Polymorphism current : expectedPolys)
//		{
//			
//			//The polymorphism is  contained in this haplogroup
//			if(foundPolys.contains(current))
//			{
//				Element result = new Element("DetailedResult");
//				
//				Element newExpectedPoly = new Element("expected");				
//				newExpectedPoly.setText(current.toString());
//				result.addContent(newExpectedPoly);
//				
//				Element newCorrectPoly = new Element("correct");				
//				newCorrectPoly.setText("yes");
//				result.addContent(newCorrectPoly);
//				unusedPolysArray.remove(current);
//				results.addContent(result);
//			}	
//
//			
//		}
//		
//		
//		
//		return results;
//	}
//
//
//	public Element getNotInRangePolysXML() {
//		Element results = new Element("OutOfRangePolys");
//		Collections.sort(missingPolysOutOfRange);
//		
//			
//		for (Polymorphism currentPoly : missingPolysOutOfRange) {
//
//			Element result = new Element("OutOfRangePoly");
//			Element newUnusedPoly = new Element("poly");
//			newUnusedPoly.setText(currentPoly.toString());
//			result.addContent(newUnusedPoly);
//			
//			Element weightUnusedPoly = new Element("weight");
//			weightUnusedPoly.setText(String.valueOf(phylotree.getPhylogeneticWeight(currentPoly)));
//			result.addContent(weightUnusedPoly);
//			
//			
//
//			results.addContent(result);
//		}
//		
//		
//		
//		return results;
//	}
//
//
//
//	public ArrayList<SearchResultPerNode> getUsedPath() {
//		
//		return usedPath;
//		
//	}
//
//
//	public void extendPath(SearchResultPerNode newNode) {
//		usedPath.add(newNode);
//		
//	}
//
//
//	public ArrayList<Polymorphism> getCorrectedBackmutations() {
//		return correctedBackmutations;
//	}
//
//
//	public ArrayList<SearchResultPerNode> getPhyloTreePath() {
//		return usedPath;
//	}
//	
//	protected void finalize() throws Throwable {
//	  // System.out.println(haplogroup +" " +  this.usedPolysInSample +  " freed");
//	        super.finalize();
//	   
//	}
//
//	public MissingPolysIterator getIterMissingPolys(){
//		return new MissingPolysIterator(this);
//	}
//
//
//	public Iterator<Polymorphism> getIterExpectedPolys() {
//		return expectedPolys.iterator();	
//	}
//	
//	public ArrayList<Polymorphism> getUnusedPolys(){
//		return remainingPolys;
//	}
//
//}