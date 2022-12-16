package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.io.FileNotFoundException;

import core.TestSample;

public abstract class HaplogrepRule {
	
	private int priority;
	private String file;
	
	public HaplogrepRule(int priority) {
		this.priority = priority;
	}

	public HaplogrepRule(int priority, String file) {
		this.priority = priority;
		this.file = file;
	}

	public abstract void evaluate(QualityAssistent qualityAssistent, TestSample currentSample) throws FileNotFoundException;

	public abstract void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample);

	public int getPriority() {
		return priority;
	}
	
	public String getFile() {
		return file;
	}
}
