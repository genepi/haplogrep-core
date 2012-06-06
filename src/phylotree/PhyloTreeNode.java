package phylotree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Haplogroup;
import core.Polymorphism;

public class PhyloTreeNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -14323995952308895L;
	protected Haplogroup haplogroup = null;
	protected ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();

	protected Phylotree2 tree = null;
	protected PhyloTreeNode parent = null;
	protected ArrayList<PhyloTreeNode> subHaplogroups = new ArrayList<PhyloTreeNode>(); 
	
	public PhyloTreeNode(Phylotree2 tree) {
		this.tree = tree;
		haplogroup = new Haplogroup("unkown");
	}
	
	public PhyloTreeNode(Phylotree2 tree, PhyloTreeNode parent, Haplogroup haplogroup) {
		this.tree = tree;
		this.parent = parent;
		this.haplogroup = haplogroup;
		Collections.sort(this.expectedPolys);
	}

	public Haplogroup getHaplogroup() {
		return haplogroup;
	}

	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}

	public void addSubHaplogroup(PhyloTreeNode newSubGroup){
		subHaplogroups.add(newSubGroup);
	}

	public List<PhyloTreeNode> getSubHaplogroups() {
		return subHaplogroups;
	}

	public void addExpectedPoly(Polymorphism newExpectedPoly) {
		expectedPolys.add(newExpectedPoly);	
	}	
	
	public PhyloTreeNode getParent(){
		return parent;
	}

	public Phylotree2 getTree() {
		return tree;	
	}
	
}