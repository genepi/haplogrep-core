package qualityAssurance.rules;

import qualityAssurance.QualityAssistent;

import java.util.Vector;

import core.Polymorphism;
import core.TestSample;

public abstract class HaplogrepRule {
	private int priority;
	public HaplogrepRule(int priority){
		this.priority = priority;
	}
	
	public abstract void evaluate(QualityAssistent qualityAssistent,TestSample currentSample);
	public abstract void suppressIssues(QualityAssistent qualityAssistent, TestSample currentSample);
	
	public int getPriority(){
		return priority;
	}
}
