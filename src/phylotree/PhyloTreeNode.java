package phylotree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Haplogroup;
import core.Polymorphism;

/**
 * Represents a node of the phylotree.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class PhyloTreeNode implements Serializable {

	private static final long serialVersionUID = -14323995952308895L;
	private Haplogroup haplogroup = null;
	private ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();

	private Phylotree tree = null;
	private PhyloTreeNode parent = null;
	private ArrayList<PhyloTreeNode> subHaplogroups = new ArrayList<PhyloTreeNode>();

	/**
	 * Creates a new root node without parent node
	 * 
	 * @param tree The underlying phylotree
	 */
	PhyloTreeNode(Phylotree tree) {
		this.tree = tree;
		haplogroup = new Haplogroup("Haplogrep Root");
	}

	/**
	 * Creates a new child node
	 * @param tree The underlying phylotree
	 * @param parentNode The parent node
	 * @param haplogroup The haplogroup of the new node
	 */
	PhyloTreeNode(Phylotree tree, PhyloTreeNode parentNode, Haplogroup haplogroup) {
		this.tree = tree;
		this.parent = parentNode;
		this.haplogroup = haplogroup;
		Collections.sort(this.expectedPolys);
	}

	/**
	 * @return The haplogroup of this phylotree node
	 */
	public Haplogroup getHaplogroup() {
		return haplogroup;
	}

	/**
	 * @return All polymorphisms expected by this phylotree node (complete range). 
	 */
	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}

	/**
	 * Adds a new child node to a given phylotree node
	 * @param newChildNode The new child node
	 */
	void addSubHaplogroup(PhyloTreeNode newChildNode) {
		subHaplogroups.add(newChildNode);
	}

	/**
	 * @return All children of a this instance
	 */
	public List<PhyloTreeNode> getSubHaplogroups() {
		return subHaplogroups;
	}

	/**
	 * Adds an expected polymorphism 
	 * @param newExpectedPoly The new polymorphism
	 */
	void addExpectedPoly(Polymorphism newExpectedPoly) {
		expectedPolys.add(newExpectedPoly);
	}

	/**
	 * @return The parent (super haplogroup) of this node instance.
	 */
	public PhyloTreeNode getParent() {
		return parent;
	}

	/**
	 * @return The tree instance of this phylotree node
	 */
	public Phylotree getTree() {
		return tree;
	}

}