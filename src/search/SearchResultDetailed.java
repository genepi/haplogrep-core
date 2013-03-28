package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jdom.Element;

import phylotree.PhyloTreeNode;
import core.Polymorphism;

/**
 * Represents a DETAILED search result attached to normal SearchResult instance.
 * Calculates all polymorphisms details and as well as the exact path the result
 * has taken through the phylotree. Detailed results are generated automatically
 * on first request. Since detailed results need more memory and cpu capacity to
 * create, they meant to be used only on the top ranking results.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class SearchResultDetailed implements Serializable {
	private static final long serialVersionUID = 3578717605511291419L;

	private ArrayList<Polymorphism> expectedPolys;
	private ArrayList<Polymorphism> foundPolys;
	private ArrayList<Polymorphism> remainingPolys;
	private ArrayList<Polymorphism> remainingPolysNotInRange;
	private ArrayList<Polymorphism> correctedBackmutations;
	private ArrayList<Polymorphism> missingPolysOutOfRange;

	private ArrayList<SearchResultTreeNode> path = new ArrayList<SearchResultTreeNode>();
	transient private SearchResult searchResult;

	/**
	 * Creates a new (empty) instance
	 * 
	 * @param searchResult
	 *            The SearchResult instance this detailed search result is
	 *            attached to
	 */
	 SearchResultDetailed(SearchResult searchResult) {
		this.searchResult = searchResult;
		this.expectedPolys = new ArrayList<Polymorphism>();
		this.foundPolys = new ArrayList<Polymorphism>();
		this.remainingPolys = new ArrayList<Polymorphism>();
		this.remainingPolysNotInRange = new ArrayList<Polymorphism>();
		this.correctedBackmutations = new ArrayList<Polymorphism>();
		this.missingPolysOutOfRange = new ArrayList<Polymorphism>();
	}

	/**
	 * Generates all of the detailed content of this result instance.
	 */
	void updateResult() {
		PhyloTreeNode startNode = searchResult.getAttachedPhyloTreeNode();
		while (startNode != null) {
			SearchResultTreeNode newNode = new SearchResultTreeNode(startNode);
			for (Polymorphism currentExpectedPoly : startNode.getExpectedPolys()) {
				if (searchResult.getSample().getSampleRanges().contains(currentExpectedPoly)) {
					if (searchResult.getSample().containsWithBackmutation(currentExpectedPoly)) {
						newNode.addFoundPoly(currentExpectedPoly);
						newNode.addExpectedPoly(currentExpectedPoly);

						Polymorphism newPoly = new Polymorphism(currentExpectedPoly);
						newPoly.setBackMutation(!currentExpectedPoly.isBackMutation());
						if (!expectedPolys.contains(currentExpectedPoly) && !expectedPolys.contains(newPoly))
							expectedPolys.add(currentExpectedPoly);
						else {
							correctedBackmutations.add(currentExpectedPoly);
							newNode.addCorrectedBackmutation(currentExpectedPoly);
						}
					} else {
						newNode.addExpectedPoly(currentExpectedPoly);

						Polymorphism newPoly = new Polymorphism(currentExpectedPoly);
						newPoly.setBackMutation(!currentExpectedPoly.isBackMutation());
						if (!expectedPolys.contains(currentExpectedPoly) && !expectedPolys.contains(newPoly))
							expectedPolys.add(currentExpectedPoly);
					}
				} else {
					newNode.addNotInRangePoly(currentExpectedPoly);
					missingPolysOutOfRange.add(currentExpectedPoly);
				}
			}
			path.add(newNode);
			startNode = startNode.getParent();
		}

		remainingPolys.addAll(searchResult.getSample().getPolymorphisms());
		for (SearchResultTreeNode currentNode : path) {
			for (Polymorphism currentFoundPoly : currentNode.getFoundPolys()) {
				foundPolys.add(currentFoundPoly);
				
//				if(!currentFoundPoly.isBackMutation())
					remainingPolys.remove(currentFoundPoly);
//				else{
//					
//				}
				
////				Polymorphism newPoly = new Polymorphism(currentFoundPoly);
////				newPoly.setBackMutation(!currentFoundPoly.isBackMutation());
//				if (!foundPolys.contains(currentFoundPoly) && !foundPolys.contains(newPoly)) {
//										remainingPolys.remove(currentFoundPoly);
//				}

			}

		}
		remainingPolys.addAll(correctedBackmutations);
		Collections.reverse(path);
	}

	public double getSumWeightsRemainingTransitions(){
		double sumTransitions = 0;
		for(Polymorphism currentRemainingPoly : remainingPolys){
			if(currentRemainingPoly.isTransitionPoly())
				sumTransitions += 1;
		}
		
		for (Polymorphism current : expectedPolys) {

			// The polymorphism is contained in this haplogroup
			if (!foundPolys.contains(current)) {
				if(current.isTransitionPoly())
				sumTransitions += 1;
			}

		}
		
		return sumTransitions;
	}
	
	public double getSumWeightsRemainingTransversion(){
		double sumTransversions = 0;
		for(Polymorphism currentRemainingPoly : remainingPolys){
			if(!currentRemainingPoly.isTransitionPoly())
				sumTransversions += 1;
		}
		
		for (Polymorphism current : expectedPolys) {

			// The polymorphism is contained in this haplogroup
			if (!foundPolys.contains(current)) {
				if(!current.isTransitionPoly())
					sumTransversions += 1;
			}

		}
		
		return sumTransversions;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SearchResultDetailed))
			return false;

		if (!arrayEqualsHelper(expectedPolys, ((SearchResultDetailed) other).expectedPolys))
			return false;
		if (!arrayEqualsHelper(foundPolys, ((SearchResultDetailed) other).foundPolys))
			return false;
		if (!arrayEqualsHelper(remainingPolys, ((SearchResultDetailed) other).remainingPolys))
			return false;
		if (!arrayEqualsHelper(remainingPolysNotInRange, ((SearchResultDetailed) other).remainingPolysNotInRange))
			return false;
		if (!arrayEqualsHelper(correctedBackmutations, ((SearchResultDetailed) other).correctedBackmutations))
			return false;
		if (!arrayEqualsHelper(missingPolysOutOfRange, ((SearchResultDetailed) other).missingPolysOutOfRange))
			return false;
		if (!Arrays.equals(path.toArray(), ((SearchResultDetailed) other).path.toArray()))
			return false;

		return true;
	}

	/**
	 * Checks if two arrays of polymorphisms contain the same objects, ignoring
	 * the exact ordering
	 * 
	 * @param array1
	 * @param array2
	 * @return True if equal, false otherwise
	 */
	private boolean arrayEqualsHelper(ArrayList<Polymorphism> array1, ArrayList<Polymorphism> array2) {
		for (Polymorphism currentPoly : array1) {
			if (!array2.contains(currentPoly) && !currentPoly.isBackMutation())
				return false;
		}
		return true;
	}

	/**
	 * Converts all remaining (unused) polymorphisms to an xml representation
	 * including the reason why they are unused
	 * 
	 * @param includeHotspots
	 *            True if hotspots should be included, false otherwise
	 * @return The root element of the xml representation
	 */
	public Element getUnusedPolysXML(boolean includeHotspots) {
		Element results = new Element("DetailedResults");
		Collections.sort(remainingPolys);

		ArrayList<Polymorphism> expectedPolysSuperGroup = new ArrayList<Polymorphism>();

		for (int i = 0; i < path.size() - 1; i++)
			expectedPolysSuperGroup.addAll(path.get(i).getExpectedPolys());

		ArrayList<Polymorphism> unusedPolysWithBackmutations = new ArrayList<Polymorphism>();
		unusedPolysWithBackmutations.addAll(remainingPolys);

//		for (Polymorphism currentPoly : expectedPolys) {
//			if (!foundPolys.contains(currentPoly)) {
//				if (expectedPolysSuperGroup.contains(currentPoly)) {
//					Polymorphism p = new Polymorphism(currentPoly);
//					p.setBackMutation(true);
//					unusedPolysWithBackmutations.add(p);
//				}
//			}
//		}

		Collections.sort(unusedPolysWithBackmutations);

		for (Polymorphism currentPoly : unusedPolysWithBackmutations) {

			Element result = new Element("DetailedResult");
			Element newUnusedPoly = new Element("unused");
			newUnusedPoly.setText(currentPoly.toStringShortVersion());

			Element reasonUnusedPoly = new Element("reasonUnused");

			if (searchResult.getPhyloTree().getMutationRate(currentPoly) == 0) {
				if (currentPoly.isBackMutation()) {
					reasonUnusedPoly.setText("globalPrivateMutation");
					newUnusedPoly.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
					result.addContent(reasonUnusedPoly);
					result.addContent(newUnusedPoly);
					results.addContent(result);
				}

				else if (currentPoly.isMTHotspot()) {

					if (includeHotspots) {
						reasonUnusedPoly.setText("hotspot");
						result.addContent(reasonUnusedPoly);
						result.addContent(newUnusedPoly);
						results.addContent(result);
					}
				}

				else {
					reasonUnusedPoly.setText("globalPrivateMutation");

					result.addContent(reasonUnusedPoly);
					result.addContent(newUnusedPoly);
					results.addContent(result);
				}

			}

			else {
				if (remainingPolysNotInRange.contains(currentPoly))
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

	/**
	 * Converts all found and not found polymorphisms to one xml representation.
	 * 
	 * @return The root element of the xml representation
	 */
	public Element getFoundNotFoundPolys() {

		Element results = new Element("DetailedResults");
		Collections.sort(expectedPolys);

		ArrayList<Polymorphism> unusedPolysArray = new ArrayList<Polymorphism>();
		unusedPolysArray.addAll(searchResult.getSample().getPolymorphisms());

		for (Polymorphism current : expectedPolys) {

			// The polymorphism is contained in this haplogroup
			if (!foundPolys.contains(current)) {
				Element result = new Element("DetailedResult");

				Element newExpectedPoly = new Element("expected");
				newExpectedPoly.setText(current.toStringShortVersion());
				result.addContent(newExpectedPoly);

				Element newCorrectPoly = new Element("correct");
				newCorrectPoly.setText("no");
				result.addContent(newCorrectPoly);

				results.addContent(result);
			}

		}

		for (Polymorphism current : expectedPolys) {

			// The polymorphism is contained in this haplogroup
			if (foundPolys.contains(current)) {
				Element result = new Element("DetailedResult");

				Element newExpectedPoly = new Element("expected");
				newExpectedPoly.setText(current.toStringShortVersion());
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

	/**
	 * Converts the path of this result to an xml representation
	 * 
	 * @param includeMissingPolys
	 *            true if missing (not found) polymorphisms should be included,
	 *            false otherwise
	 * @return The root element of the xml represnentation
	 */
	public Element getPhyloTreePathXML(boolean includeMissingPolys) {
		if (path.size() == 0)
			return null;

		Element root = null;
		Element currentEndNode = null;

		for (SearchResultTreeNode currentNode : path) {
			if (root == null) {
				currentEndNode = root = new Element("TreeNode");
				root.setAttribute("name", currentNode.getHaplogroup().toString());
				root.setAttribute("type", "Haplogroup");
			} else {
				Element newChildElement = new Element("TreeNode");
				newChildElement.setAttribute("name", currentNode.getHaplogroup().toString());
				newChildElement.setAttribute("type", "Haplogroup");
				currentEndNode.addContent(newChildElement);
				currentEndNode = newChildElement;

			}

			Collections.sort(currentNode.getExpectedPolys());

			for (Polymorphism currentPoly : currentNode.getExpectedPolys()) {
				if (!currentNode.getFoundPolys().contains(currentPoly)) {
					if (includeMissingPolys) {
						Element newChildElement = new Element("Poly");
						newChildElement.setText("mis" + currentPoly.toStringShortVersion());
						currentEndNode.addContent(newChildElement);
					}
				} else {
					Element newChildElement = new Element("Poly");
					newChildElement.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
					currentEndNode.addContent(newChildElement);
				}
			}

		}

		return root;
	}

	/**
	 * @return A xml representation of all out of sample range polymorphism
	 */
	public Element getNotInRangePolysXML() {
		Element results = new Element("OutOfRangePolys");
		Collections.sort(missingPolysOutOfRange);

		for (Polymorphism currentPoly : missingPolysOutOfRange) {

			Element result = new Element("OutOfRangePoly");
			Element newUnusedPoly = new Element("poly");
			newUnusedPoly.setText(currentPoly.toString());
			result.addContent(newUnusedPoly);

			Element weightUnusedPoly = new Element("weight");
			weightUnusedPoly.setText(String.valueOf(searchResult.getPhyloTree().getMutationRate(currentPoly)));
			result.addContent(weightUnusedPoly);

			results.addContent(result);
		}

		return results;
	}


	/**
	 * @return A list of nodes representing the path through the phylotree.
	 *  Starts with phylotree root node.
	 */
	public ArrayList<SearchResultTreeNode> getPhyloTreePath() {
		return path;
	}

	/**
	 * @return The remaining (not found) polymorphisms in the test sample
	 */
	public ArrayList<Polymorphism> getRemainingPolysInSample() {
		return remainingPolys;
	}

	/**
	 * @return A list of back mutation that had been corrected during the search process. 
	 * Corrections is necessary if back mutations are neutralized with forward mutations.
	 */
	public ArrayList<Polymorphism> getCorrectedBackmutations() {
		return correctedBackmutations;
	}

	/**
	 * @return A list of all polymorphisms found in the test sample
	 */
	public ArrayList<Polymorphism> getFoundPolys() {
		return foundPolys;
	}

	/**
	 * @return A list of all out of range polymorphisms 
	 */
	public ArrayList<Polymorphism> getMissingPolysOutOfRange() {
		return missingPolysOutOfRange;
	}

	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}
	
	
}