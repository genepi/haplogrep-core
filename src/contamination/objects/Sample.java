package contamination.objects;

import java.util.HashMap;

public class Sample {

	private String id;
	private HashMap<String, Variant> variants;
	private int amountHomoplasmies;
	private int amountVariants;
	private int amountHeteroplasmies;
	private float totalCoverage = 0;
	private float meanHeteroplasmyLevel = 0;
	private float meanCoverage = 0;

	public Sample() {
		variants = new HashMap<String, Variant>();
	}

	public HashMap<String, Variant> getPositions() {
		return variants;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPositions(HashMap<String, Variant> variants) {
		this.variants = variants;
	}

	public void addPosition(Variant var) {
		variants.put(var.getPos()+String.valueOf(var.getVariant()), var);
	}

	public void updateCount(int type) {
		
		amountVariants +=1;

		if (type == 1) {
			amountHomoplasmies += 1;
		}

		if (type == 2) {
			amountHeteroplasmies += 1;
		}
	}

	public void updateCoverage(int coverage) {

		totalCoverage += coverage;
	}


	public int getAmountHomoplasmies() {
		return amountHomoplasmies;
	}

	public void setAmountHomoplasmies(int amountHomoplasmies) {
		this.amountHomoplasmies = amountHomoplasmies;
	}

	public int getAmountVariants() {
		return amountVariants;
	}

	public void setAmountVariants(int amountVariants) {
		this.amountVariants = amountVariants;
	}

	public int getAmountHeteroplasmies() {
		return amountHeteroplasmies;
	}

	public void setAmountHeteroplasmies(int amountHeteroplasmies) {
		this.amountHeteroplasmies = amountHeteroplasmies;
	}

	public float getTotalCoverage() {
		return totalCoverage;
	}

	public void setTotalCoverage(float totalCoverage) {
		this.totalCoverage = totalCoverage;
	}

	public float getMeanHeteroplasmyLevel() {
		return meanHeteroplasmyLevel;
	}

	public void setMeanHeteroplasmyLevel(float meanHeteroplasmyLevel) {
		this.meanHeteroplasmyLevel = meanHeteroplasmyLevel;
	}

	public float getMeanCoverage() {
		return meanCoverage;
	}

	public void setMeanCoverage(float meanCoverage) {
		this.meanCoverage = meanCoverage;
	}
}
