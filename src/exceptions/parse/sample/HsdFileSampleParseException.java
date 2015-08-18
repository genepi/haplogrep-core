package exceptions.parse.sample;


import org.json.JSONException;
import org.json.JSONObject;

import exceptions.parse.HsdFileException;
/**
 * Root exceptions class for all exceptions dealing with parsing of hsd test samples
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class HsdFileSampleParseException extends HsdFileException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7884048528486763694L;
	
	String testSampleID = "";
	public HsdFileSampleParseException(String message) {
		super(message);
	}

	/**
	 * @return The test sample id the exceptions occurred
	 */
	public String getTestSampleID() {
		return testSampleID;
	}

	/**
	 * Sets the test sample id the exception occurred
	 * @param testSampleID The test sample id
	 */
	public void setTestSampleID(String testSampleID) {
		this.testSampleID = testSampleID;
	}

	/* (non-Javadoc)
	 * @see exceptions.parse.HsdFileException#toJSON()
	 */
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		newJSONException.put("sampleID", testSampleID);
		return newJSONException;
	}
}
