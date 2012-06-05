package search;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import phylotree.PhyloTreeNode;

import core.Haplogroup;
import core.Polymorphism;

public class SearchResultTreeNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2832456003542749389L;
	
	Haplogroup haplogroup = null;
	ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> notInRangePolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> correctedBackmutation = new ArrayList<Polymorphism>();
	
	public SearchResultTreeNode(PhyloTreeNode treeNode)
	{
		this.haplogroup = treeNode.getHaplogroup();
	}

	public boolean equals(Object other){
		if(!(other instanceof SearchResultTreeNode))
			return false;
		if(haplogroup != null && !haplogroup.equals( ((SearchResultTreeNode)other).haplogroup))
			return false;	
		
		if(!arrayEqualsHelper(expectedPolys, ((SearchResultTreeNode)other).expectedPolys))
			return false;	
		if(!arrayEqualsHelper(foundPolys, ((SearchResultTreeNode)other).foundPolys))
			return false;
		if(!arrayEqualsHelper(notInRangePolys, ((SearchResultTreeNode)other).notInRangePolys))
			return false;
//		if(!arrayEqualsHelper(correctedBackmutation, ((SearchResultTreeNode)other).correctedBackmutation))
//			return false;
			
		return true;
	}
	
	public void addExpectedPoly(Polymorphism currentPoly) {
		expectedPolys.add(currentPoly);
		
	}

	public void addFoundPoly(Polymorphism currentPoly) {
		foundPolys.add(currentPoly);
		
	}
	
	public void addNotInRangePoly(Polymorphism currentPoly) {
		notInRangePolys.add(currentPoly);
		
	}

	
	
	public void removeExpectedPoly(Polymorphism currentPoly) {
		expectedPolys.remove(currentPoly);
		//correctedBackmutation.add(currentPoly);
	}

	public void removeFoundPoly(Polymorphism currentPoly) {
		foundPolys.remove(currentPoly);
		//correctedBackmutation.add(currentPoly);
	}
	
	public Haplogroup getHaplogroup() {
		return haplogroup;
	}


	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}


	public ArrayList<Polymorphism> getFoundPolys() {
		return foundPolys;
	}


	public ArrayList<Polymorphism> getNotInRangePolys() {
		return notInRangePolys;
	}


	public ArrayList<Polymorphism> getCorrectedBackmutation() {
		return correctedBackmutation;
	}


	public void addCorrectedBackmutation(Polymorphism poly) {
		
		correctedBackmutation.add(poly);
	}
	private boolean arrayEqualsHelper(ArrayList<Polymorphism> a1,ArrayList<Polymorphism> a2){
		for(Polymorphism currentPoly : a1){
			if(!a2.contains(currentPoly) && !currentPoly.isBackMutation())
				return false;
		}
		return true;
	}
}
