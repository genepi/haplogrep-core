package core;

public  class Phantom implements Comparable<Phantom>{

	public String variant;
	int amount;
	String samples;
	int score;
	
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getSamples() {
		return samples;
	}
	public void setSamples(String samples) {
		this.samples = samples;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public int compareTo(Phantom o) {
		if (this.amount < o.amount)
			return 1;
		else
			return -1;
		}


	
	
}
