package qualityAssurance;

import core.TestSample;

public class CorrectionMethod {

	String desc;
	int methodID;
	public CorrectionMethod(String description,int methodID){
		this.desc = description;
		this.methodID = methodID;
	}
	public void execute(TestSample sample){}
	public String getDesc() {
		return desc;
	}
	public int getMethodID() {
		return methodID;
	};
	
}
