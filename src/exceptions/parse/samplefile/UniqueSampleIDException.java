package exceptions.parse.samplefile;


import org.json.JSONException;
import org.json.JSONObject;

import exceptions.parse.HsdFileException;

/**
 * Represents a exception thrown if two identical test sample ids are observed
 * 
 * @author Dominic Pacher, Schoenherr, Hansi Weissensteiner
 * 
 */
public class UniqueSampleIDException extends HsdFileException {

	private static final long serialVersionUID = 235320571372918114L;
	String testSampleID = "";

	public UniqueSampleIDException() {
		super("Keys of samples (first column) are not unique");
	}

	public void setTestSampleeID(String testSampleID2) {
		this.testSampleID = testSampleID2;
	}

	/* (non-Javadoc)
	 * @see exceptions.parse.HsdFileException#toJSON()
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		newJSONException.put("sampleID", testSampleID);
		return newJSONException;
	}

}