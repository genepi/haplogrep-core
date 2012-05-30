package search;

import java.util.ArrayList;

import core.Haplogroup;
import core.Polymorphism;

public class PhyloTreeNode {

	protected Haplogroup haplogroup = null;
	protected ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();
	
	protected PhyloTreeNode parent = null;
	protected ArrayList<PhyloTreeNode> subHaplogroups = new ArrayList<PhyloTreeNode>(); 
	
	public PhyloTreeNode(PhyloTreeNode parent,Haplogroup haplogroup) {
		 this.parent = null;
		 this.haplogroup = haplogroup;
	}

	public void addExpectedPoly(Polymorphism currentPoly) {
		expectedPolys.add(currentPoly);	
	}

	public Haplogroup getHaplogroup() {
		return haplogroup;
	}

	public ArrayList<Polymorphism> getExpectedPolys() {
		return expectedPolys;
	}

	public void addSubNode(){
		
	}
}