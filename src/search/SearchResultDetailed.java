package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.jdom.Element;

import phylotree.PhyloTreeNode2;
import core.Polymorphism;

public class SearchResultDetailed implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3578717605511291419L;
	
	private ArrayList<Polymorphism> expectedPolys;
	public ArrayList<Polymorphism> foundPolys;
	public ArrayList<Polymorphism> remainingPolys;
	public ArrayList<Polymorphism> remainingPolysNotInRange;
	public ArrayList<Polymorphism> correctedBackmutations;
	public ArrayList<Polymorphism> missingPolysOutOfRange;

	private ArrayList<SearchResultTreeNode> path = new ArrayList<SearchResultTreeNode>();
	transient private SearchResult searchResult;
	
	public SearchResultDetailed(SearchResult searchResult){	
		this.searchResult = searchResult;
		this.expectedPolys = new ArrayList<Polymorphism>();
		this.foundPolys = new ArrayList<Polymorphism>();
		this.remainingPolys = new ArrayList<Polymorphism>();
		this.remainingPolysNotInRange = new ArrayList<Polymorphism>();
		this.correctedBackmutations = new ArrayList<Polymorphism>();
		this.missingPolysOutOfRange = new ArrayList<Polymorphism>();
	}
	
	public void updateResult(){
		path.clear();
		expectedPolys.clear();
		correctedBackmutations.clear();
		remainingPolysNotInRange.clear();
		correctedBackmutations.clear();
		foundPolys.clear();
		
		PhyloTreeNode2 startNode = searchResult.getAttachedPhyloTreeNode();
		while (startNode != null) {
			SearchResultTreeNode newNode = new SearchResultTreeNode(startNode);
			for (Polymorphism currentPoly : startNode.getExpectedPolys()) {
				if (searchResult.getSample().getSampleRanges().contains(currentPoly)) {
					if (searchResult.getSample().containsWithBackmutation(currentPoly)) {
						newNode.addFoundPoly(currentPoly);
						newNode.addExpectedPoly(currentPoly);

						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(!currentPoly.isBackMutation());
						if (!expectedPolys.contains(currentPoly) && !expectedPolys.contains(newPoly))
							expectedPolys.add(currentPoly);
						 else{
							 correctedBackmutations.add(currentPoly);
							 newNode.addCorrectedBackmutation(currentPoly);
						 }
					} else {
						newNode.addExpectedPoly(currentPoly);

						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(!currentPoly.isBackMutation());
						if (!expectedPolys.contains(currentPoly) && !expectedPolys.contains(newPoly))
							expectedPolys.add(currentPoly);
//						 else{
//							 correctedBackmutations2.add(currentPoly);
//						 newNode.addCorrectedBackmutation(currentPoly);
//						}
					}
				} 
				else{
					newNode.addNotInRangePoly(currentPoly);
					missingPolysOutOfRange.add(currentPoly);
				}
			}
			path.add(newNode);
			startNode = startNode.getParent();
		}
		
		remainingPolys.addAll(searchResult.getSample().getPolymorphismn());
		for(SearchResultTreeNode currentNode : path){
			for(Polymorphism currentPoly : currentNode.foundPolys){
				Polymorphism newPoly = new Polymorphism(currentPoly);
				newPoly.setBackMutation(!currentPoly.isBackMutation());
				if(!foundPolys.contains(currentPoly) && !foundPolys.contains(newPoly)){
					foundPolys.add(currentPoly);
					remainingPolys.remove(currentPoly);
				}
				
			}

		}
		
		Collections.reverse(path);
	}
	
	public boolean equals(Object other){
		if(!(other instanceof SearchResultDetailed))
			return false;
		
		if(!arrayEqualsHelper(expectedPolys, ((SearchResultDetailed)other).expectedPolys))
			return false;
		
		if(!arrayEqualsHelper(foundPolys, ((SearchResultDetailed)other).foundPolys))
			return false;
		if(!arrayEqualsHelper(remainingPolys, ((SearchResultDetailed)other).remainingPolys))
			return false;
		if(!arrayEqualsHelper(remainingPolysNotInRange, ((SearchResultDetailed)other).remainingPolysNotInRange))
			return false;
		if(!arrayEqualsHelper(correctedBackmutations, ((SearchResultDetailed)other).correctedBackmutations))
			return false;
		if(!arrayEqualsHelper(missingPolysOutOfRange, ((SearchResultDetailed)other).missingPolysOutOfRange))
			return false;
		if(!Arrays.equals(path.toArray(), ((SearchResultDetailed)other).path.toArray()))
			return false;
		
		return true;
	}
	
	private boolean arrayEqualsHelper(ArrayList<Polymorphism> a1,ArrayList<Polymorphism> a2){
		for(Polymorphism currentPoly : a1){
			if(!a2.contains(currentPoly) && !currentPoly.isBackMutation())
				return false;
		}
		return true;
	}

	/**
	 * @param searchResult TODO
	 * @return A list of all polys checked in the phylo xml tree
	 */
	public ArrayList<Polymorphism> getCheckedPolys() {
		return expectedPolys;
	}

	public Element getUnusedPolysXML( boolean includeHotspots)
	{
		Element results = new Element("DetailedResults");
		Collections.sort(remainingPolys);
		
		ArrayList<Polymorphism> expectedPolysSuperGroup = new ArrayList<Polymorphism>();
		
		for(int i = 0; i < path.size()-1;i++)
		 expectedPolysSuperGroup.addAll(path.get(i).getExpectedPolys());
		
		
		ArrayList<Polymorphism> unusedPolysWithBackmutations = new ArrayList<Polymorphism>();
		unusedPolysWithBackmutations.addAll(remainingPolys);
		
		for(Polymorphism currentPoly : expectedPolys){
			if(!foundPolys.contains(currentPoly)){
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
			
			
			if(searchResult.getPhyloTree().getMutationRate(currentPoly) == 0)
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
				if(remainingPolysNotInRange.contains(currentPoly))
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

	public Element toXML()
	{
		
		Element results = new Element("DetailedResults");
		Collections.sort(expectedPolys);
		
		
		ArrayList<Polymorphism> unusedPolysArray = new ArrayList<Polymorphism>();
		unusedPolysArray.addAll(searchResult.getSample().getPolymorphismn());
		
		for(Polymorphism current : expectedPolys)
		{
			
			
			//The polymorphism is contained in this haplogroup
			if(!foundPolys.contains(current))
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
		
		
		
		for(Polymorphism current : expectedPolys)
		{
			
			//The polymorphism is  contained in this haplogroup
			if(foundPolys.contains(current))
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

	public Element getPhyloTreePathXML(boolean includeMissingPolys)	{
		if(path.size() == 0)
			return null;
		
		Element root =null;
		Element currentEndNode = null;
		
		for(SearchResultTreeNode currentNode : path){
			if(root == null){
				currentEndNode = root = new Element("TreeNode");
				root.setAttribute("name", currentNode.getHaplogroup().toString());
				root.setAttribute("type", "Haplogroup");
				//root.addContent(currentNode.getHaplogroup().toString());
				}
			else{
				Element newChildElement =  new Element("TreeNode");
				newChildElement.setAttribute("name", currentNode.getHaplogroup().toString());
				newChildElement.setAttribute("type", "Haplogroup");
				//newChildElement.setText(currentNode.getHaplogroup().toString());
				currentEndNode.addContent(newChildElement);
				currentEndNode = newChildElement;
				
			}
			
			Collections.sort(currentNode.getExpectedPolys());
			
			
//			for(Polymorphism currentPoly :currentNode.getFoundPolys()){
//				Element newChildElement =  new Element("Poly");
//				newChildElement.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
//				currentEndNode.addContent(newChildElement);
//			}
//			
			for(Polymorphism currentPoly :currentNode.getExpectedPolys()){
				if(!currentNode.getFoundPolys().contains(currentPoly)){
					if(includeMissingPolys){
					Element newChildElement =  new Element("Poly");
					newChildElement.setText("mis" + currentPoly.toStringShortVersion());
					currentEndNode.addContent(newChildElement);
					}
				}
				else{
					Element newChildElement =  new Element("Poly");
					newChildElement.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
					currentEndNode.addContent(newChildElement);
				}
			}
			
			
			
			}
		
		
		
		
		return root;
		
	}
	public MissingPolysIterator getIterMissingPolys(){
		return new MissingPolysIterator(searchResult);
	}
	
	public Iterator<Polymorphism> getIterExpectedPolys() {
		return expectedPolys.iterator();	
	}
	
	public ArrayList<SearchResultTreeNode> getPhyloTreePath() {	
		return path;
	}
}