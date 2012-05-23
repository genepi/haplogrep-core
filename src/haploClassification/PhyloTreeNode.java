package haploClassification;

import genetools.Haplogroup;
import genetools.Polymorphism;

import java.util.ArrayList;

public class PhyloTreeNode {
	Haplogroup haplogroup = null;
	ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> notInRangePolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> correctedBackmutation = new ArrayList<Polymorphism>();
	
	public PhyloTreeNode(Haplogroup haplogroup)
	{
		this.haplogroup = haplogroup;
	}

	public void addExpectedPoly(Polymorphism currentPoly) {
		expectedPolys.add(currentPoly);
		
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
	
}
