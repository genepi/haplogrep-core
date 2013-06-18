package core;

import java.io.Serializable;
import java.util.HashSet;

import phylotree.Phylotree;

/**
 * Represents a single haplogroup.
 * 
 * @author Dominic Pacher, Sebastian Sch�nherr, Hansi Weissensteiner
 * 
 */
public class Haplogroup implements Serializable {

	private static final long serialVersionUID = 8902692095026305359L;

	private String haplogroup;

	public Haplogroup(String haplogroup) {
		this.haplogroup = haplogroup;
	}

	@Override
	public boolean equals(Object haplogroup) {
		if (!(haplogroup instanceof Haplogroup))
			return false;

		Haplogroup c = (Haplogroup) haplogroup;
		if (!this.haplogroup.equals(c.haplogroup))
			return false;

		return true;
	}

	/**
	 * Checks if the haplogroup is a super group of another haplogroup
	 * 
	 * @param phylotree
	 *            The version of the phylotree the check should use
	 * @param hgToCheck
	 *            The haplogroup to check
	 * @return True if this haplogroup instance is a super group, false
	 *         otherwise
	 */
	public boolean isSuperHaplogroup(Phylotree phylotree, Haplogroup hgToCheck) {
		if (!(hgToCheck instanceof Haplogroup))
			return false;

		return phylotree.isSuperHaplogroup(hgToCheck,this);
	}
	
	public int distanceToSuperHaplogroup(Phylotree phylotree, Haplogroup hgToCheck) {
		if (!(hgToCheck instanceof Haplogroup))
			return -1;

		return phylotree.distanceToSuperHaplogroup(hgToCheck,this);
	}
	
	

	@Override
	public int hashCode() {
		return haplogroup.hashCode();
	}

	@Override
	public String toString() {
		return haplogroup;
	}
}
