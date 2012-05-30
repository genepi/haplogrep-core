package qualityAssurance;

public class QualityIssue implements Comparable<QualityIssue>{
	int priority;
	String description;
	
	public QualityIssue(int priority,String description){
		this.priority = priority;
		this.description = description;
	}

	@Override
	public int compareTo(QualityIssue o) {
		return priority - o.priority;
	}

	public String getDescription() {
		return description;
	}
	
	
}
