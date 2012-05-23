package genetools.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class HsdFileParseException extends HsdException{
	String testSampleID = "";
	public HsdFileParseException(String message) {
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
