package search;


import java.util.ArrayList;

import core.Haplogroup;
import core.Polymorphism;

public class PhyloTreeNodeSearchResult {
	PhyloTreeNode phylotreeNode = null; 
	ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> notInRangePolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> correctedBackmutation = new ArrayList<Polymorphism>();
	
	public PhyloTreeNodeSearchResult(PhyloTreeNode phylotreeNode)
	{
		this.phylotreeNode = phylotreeNode;
	}

	public void addCorrectPoly(Polymorphism currentPoly) {
		foundPolys.add(currentPoly);
		
	}
	
	public void addNotInRangePoly(Polymorphism currentPoly) {
		notInRangePolys.add(currentPoly);
		
	}

	
	
	public void removeExpectedPoly(Polymorphism currentPoly) {
		expectedPolys.remove(currentPoly);
		//correctedBackmutation.add(currentPoly);
	}

	public void removeCorrectPoly(Polymorphism currentPoly) {
		foundPolys.remove(currentPoly);
		//correctedBackmutation.add(currentPoly);
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
	
}
