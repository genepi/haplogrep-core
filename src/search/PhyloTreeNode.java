package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Haplogroup;
import core.Polymorphism;

public class PhyloTreeNode {

	protected Haplogroup haplogroup = null;
	protected ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();

	protected PhyloTreeNode parent = null;
	protected ArrayList<PhyloTreeNode> subHaplogroups = new ArrayList<PhyloTreeNode>(); 
	
	public PhyloTreeNode(PhyloTreeNode parent,Haplogroup haplogroup) {
		 this.parent = parent;
		 this.haplogroup = haplogroup;
		 this.expectedPolys.addAll(parent.expectedPolys);
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
}