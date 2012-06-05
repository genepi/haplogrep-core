package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import phylotree.PhyloTreeNode;

import core.Haplogroup;
import core.Polymorphism;

public class SearchResultDetailed implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3578717605511291419L;
	public ArrayList<Polymorphism> expectedPolys;
	public ArrayList<Polymorphism> expectedPolys2;
	public ArrayList<Polymorphism> foundPolys;
	public ArrayList<Polymorphism> foundPolys2;
	public ArrayList<Polymorphism> remainingPolys;
	public ArrayList<Polymorphism> remainingPolys2;
	public ArrayList<Polymorphism> remainingPolysNotInRange;
	public ArrayList<Polymorphism> correctedBackmutations;
	public ArrayList<Polymorphism> correctedBackmutations2;
	public ArrayList<Polymorphism> missingPolysOutOfRange;
	public ArrayList<Polymorphism> missingPolysOutOfRange2;
//	public HashSet<Polymorphism> missingPolys;
	public PhyloTreePath usedPath = new PhyloTreePath();
	private SearchResult searchResult;
	public PhyloTreeNode phyloNode;
	
	public SearchResultDetailed(SearchResult searchResult){
		
		this.expectedPolys = new ArrayList<Polymorphism>();
		this.expectedPolys2 = new ArrayList<Polymorphism>();
		this.foundPolys = new ArrayList<Polymorphism>();
		this.foundPolys2 = new ArrayList<Polymorphism>();
		this.remainingPolys = new ArrayList<Polymorphism>();
		this.remainingPolys2 = new ArrayList<Polymorphism>();
		this.remainingPolysNotInRange = new ArrayList<Polymorphism>();
		this.correctedBackmutations = new ArrayList<Polymorphism>();
		this.missingPolysOutOfRange = new ArrayList<Polymorphism>();
		this.missingPolysOutOfRange2 = new ArrayList<Polymorphism>();
		this.correctedBackmutations2 = new ArrayList<Polymorphism>();
		this.searchResult = searchResult;
	}
	
	public void updateResult(){
		usedPath.getNodes().clear();
		PhyloTreeNode startNode = phyloNode;//usedPath.getNodes().get(usedPath.getNodes().size()-1);
		while (startNode != null) {
			SearchResultTreeNode newNode = new SearchResultTreeNode(startNode);
			for (Polymorphism currentPoly : startNode.getExpectedPolys()) {
				if (searchResult.getSample().getSampleRanges().contains(currentPoly)) {
					if (searchResult.getSample().containsWithBackmutation(currentPoly)) {
						newNode.addFoundPoly(currentPoly);
						newNode.addExpectedPoly(currentPoly);

						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(!currentPoly.isBackMutation());
						if (!expectedPolys2.contains(currentPoly) && !expectedPolys2.contains(newPoly))
							expectedPolys2.add(currentPoly);
						 else{
							 correctedBackmutations2.add(currentPoly);
							 newNode.addCorrectedBackmutation(currentPoly);
						 }
					} else {
						newNode.addExpectedPoly(currentPoly);

						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(!currentPoly.isBackMutation());
						if (!expectedPolys2.contains(currentPoly) && !expectedPolys2.contains(newPoly))
							expectedPolys2.add(currentPoly);
//						 else{
//							 correctedBackmutations2.add(currentPoly);
//						 newNode.addCorrectedBackmutation(currentPoly);
//						}
					}
				} 
				else{
					newNode.addNotInRangePoly(currentPoly);
					missingPolysOutOfRange2.add(currentPoly);
				}
			}
			usedPath.add(newNode);
			startNode = startNode.getParent();
		}
		
		remainingPolys2.addAll(searchResult.getSample().getPolymorphismn());
		for(SearchResultTreeNode currentNode : usedPath.getNodes()){
			for(Polymorphism currentPoly : currentNode.foundPolys){
				Polymorphism newPoly = new Polymorphism(currentPoly);
				newPoly.setBackMutation(!currentPoly.isBackMutation());
				if(!foundPolys2.contains(currentPoly) && !foundPolys2.contains(newPoly)){
					foundPolys2.add(currentPoly);
					remainingPolys2.remove(currentPoly);
				}
				
			}
//				if(!currentPoly.isBackMutation())
//					foundPolys2.add(currentPoly);
//				else{
//					Polymorphism newPoly = new Polymorphism(currentPoly);
//					newPoly.setBackMutation(false);
//					foundPolys2.remove(newPoly);
//				}
		}
		
//		int x = 0;
//		if(usedPath.getNodes().get(0).haplogroup.equals(new Haplogroup("C5a2")))
//			x++;
//			
//		for(SearchResultTreeNode currentNode : usedPath.getNodes()){
//			for(Polymorphism currentPoly : currentNode.expectedPolys)
//				if(!currentPoly.isBackMutation())
//					expectedPolys2.add(currentPoly);
//				else{
//					Polymorphism newPoly = new Polymorphism(currentPoly);
//					newPoly.setBackMutation(false);
//					expectedPolys2.remove(newPoly);
//					foundPolys2.remove(newPoly);
//				}
//		}
		
		
	}
	
	public boolean equals(Object other){
		if(!(other instanceof SearchResultDetailed))
			return false;
		
		if(!arrayEqualsHelper(expectedPolys2, ((SearchResultDetailed)other).expectedPolys))
			return false;
		
		if(!arrayEqualsHelper(foundPolys2, ((SearchResultDetailed)other).foundPolys))
			return false;
		if(!arrayEqualsHelper(remainingPolys, ((SearchResultDetailed)other).remainingPolys))
			return false;
		if(!arrayEqualsHelper(remainingPolysNotInRange, ((SearchResultDetailed)other).remainingPolysNotInRange))
			return false;
		if(!arrayEqualsHelper(correctedBackmutations, ((SearchResultDetailed)other).correctedBackmutations))
			return false;
		if(!arrayEqualsHelper(missingPolysOutOfRange, ((SearchResultDetailed)other).missingPolysOutOfRange))
			return false;
		if(!usedPath.equals(((SearchResultDetailed)other).usedPath))
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
}