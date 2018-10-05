package contamination.objects;

import java.util.HashSet;

public class Sample {

	private String id;
	private HashSet<Position> positions;
	private int homoplasmies;
	private int heteroplasmies;
	private float totalCoverage = 0;

	public Sample() {
		positions = new HashSet<>();
	}

	public HashSet<Position> getPositions() {
		return positions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPositions(HashSet<Position> positions) {
		this.positions = positions;
	}

	public void addPosition(Position pos) {
		positions.add(pos);
	}

	public void updateCount(int type) {

		if (type == 1) {
			homoplasmies += 1;
		}

		if (type == 2) {
			heteroplasmies += 1;
		}
	}

	public void updateCoverage(int coverage) {

		totalCoverage += coverage;
	}

	public int getHomoplasmies() {
		return homoplasmies;
	}

	public void setHomoplasmies(int homoplasmies) {
		this.homoplasmies = homoplasmies;
	}

	public int getHeteroplasmies() {
		return heteroplasmies;
	}

	public void setHeteroplasmies(int heteroplasmies) {
		this.heteroplasmies = heteroplasmies;
	}

	public float getMeanCoverage() {
		return totalCoverage;
	}

	public void setMeanCoverage(int totalCoverage) {
		this.totalCoverage = totalCoverage;
	}
}
