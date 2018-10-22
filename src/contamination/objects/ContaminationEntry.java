package contamination.objects;

public class ContaminationEntry {
	String sampleId;
	String majorHg;
	String minorHg;
	int majorNotFound;
	int minorNotFound;
	
	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getMajorHg() {
		return majorHg;
	}

	public void setMajorHg(String majorHg) {
		this.majorHg = majorHg;
	}

	public String getMinorHg() {
		return minorHg;
	}

	public void setMinorHg(String minorHg) {
		this.minorHg = minorHg;
	}

	public int getMajorNotFound() {
		return majorNotFound;
	}

	public void setMajorNotFound(int majorNotFound) {
		this.majorNotFound = majorNotFound;
	}

	public int getMinorNotFound() {
		return minorNotFound;
	}

	public void setMinorNotFound(int minorNotFound) {
		this.minorNotFound = minorNotFound;
	}
}
