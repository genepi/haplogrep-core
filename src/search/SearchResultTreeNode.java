package search;


import java.io.Serializable;
import java.util.ArrayList;

import phylotree.PhyloTreeNode;
import core.Haplogroup;
import core.Polymorphism;

/**
 * Represents a additional search information for a phylotree node.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */

public class SearchResultTreeNode implements Serializable{

	private static final long serialVersionUID = 2832456003542749389L;
	
	private PhyloTreeNode phylotreeNode;
	private ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();
	private ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	private ArrayList<Polymorphism> notInRangePolys = new ArrayList<Polymorphism>();
	private ArrayList<Polymorphism> correctedBackmutation = new ArrayList<Polymorphism>();
	
	/**
	 * Creates a new node instance
	 * @param treeNode The phylotree node this seach result should be attached to
	 */
	SearchResultTreeNode(PhyloTreeNode treeNode)
	{
		this.phylotreeNode = treeNode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other){
		if(!(other instanceof SearchResultTreeNode))
			return false;

		if(!arrayEqualsHelper(expectedPolys, ((SearchResultTreeNode)other).expectedPolys))
			return false;	
		if(!arrayEqualsHelper(foundPolys, ((SearchResultTreeNode)other).foundPolys))
			return false;
		if(!arrayEqualsHelper(notInRangePolys, ((SearchResultTreeNode)other).notInRangePolys))
			return false;

		return true;
	}
	
	/**
	 * Adds a new expected polymorphism
	 * @param polyToAdd
	 */
	void addExpectedPoly(Polymorphism polyToAdd) {
		expectedPolys.add(polyToAdd);
		
	}

	/**
	 * Adds a new found polymorphism for the haplogroup of this phylotree node
	 * @param polyToAdd
	 */
	void addFoundPoly(Polymorphism polyToAdd) {
		foundPolys.add(polyToAdd);
		
	}
	
	/**
	 * Adds a new out of range polymorphism
	 * @param polyToAdd
	 */
	void addNotInRangePoly(Polymorphism polyToAdd) {
		notInRangePolys.add(polyToAdd);
		
	}
	
	/**
	 * @return The haplogroup of this node
	 */
	public Haplogroup getHaplogroup() {
		return phylotreeNode.getHaplogroup();
	}


	/**
	 * @return A list of all expected polymorphisms by this phylotree node 
	 * (with sample ranges taken into account)
	 */
	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}


	/**
	 * @return A list of all found polymorphisms in the sample that are expected by this phylotree node 
	 */
	public ArrayList<Polymorphism> getFoundPolys() {
		return foundPolys;
	}


	/**
	 * @return A list of all of expected but off range polymorphisms by this phylotree node 
	 */
	public ArrayList<Polymorphism> getNotInRangePolys() {
		return notInRangePolys;
	}


	/**
	 * @return A list of all back mutations
	 */
	public ArrayList<Polymorphism> getCorrectedBackmutation() {
		return correctedBackmutation;
	}


	/**
	 * Adds a corrected back mutation polymorphism to this nodes
	 * @param polyToAdd
	 */
	void addCorrectedBackmutation(Polymorphism polyToAdd) {
		correctedBackmutation.add(polyToAdd);
	}
	
	
	private boolean arrayEqualsHelper(ArrayList<Polymorphism> a1,ArrayList<Polymorphism> a2){
		for(Polymorphism currentPoly : a1){
			if(!a2.contains(currentPoly) && !currentPoly.isBackMutation())
				return false;
		}
		return true;
	}
	
	public PhyloTreeNode getPhyloTreeNode(){
		return phylotreeNode;
	}
}
