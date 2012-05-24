package exceptions.parse.sample;


import org.json.JSONException;
import org.json.JSONObject;

import exceptions.parse.samplefile.HsdFileException;

public class HsdFileSampleParseException extends HsdFileException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7884048528486763694L;
	
	String testSampleID = "";
	public HsdFileSampleParseException(String message) {
		super(message);
	}

	public String getTestSampleID() {
		return testSampleID;
	}

	public void setTestSampleID(String testSampleID2) {
		this.testSampleID = testSampleID2;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		newJSONException.put("sampleID", testSampleID);
		return newJSONException;
	}
}
