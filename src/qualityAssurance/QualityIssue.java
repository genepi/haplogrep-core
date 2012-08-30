package qualityAssurance;

import core.TestSample;

public class QualityIssue implements Comparable<QualityIssue>{
	int priority;
	String description;
	TestSample sampleOfIssue;
	
	public QualityIssue(int priority,TestSample sampleofIssue, String description){
		this.priority = priority;
		this.sampleOfIssue = sampleofIssue;
		this.description = description;
	}

	@Override
	public int compareTo(QualityIssue o) {
		return priority - o.priority;
	}

	public String getDescription() {
		return description;
	}

	public TestSample getSample() {
		return sampleOfIssue;
	}
	
	
}
