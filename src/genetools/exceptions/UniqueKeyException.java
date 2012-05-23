package genetools.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class UniqueKeyException extends HsdException {
	String testSampleID = "";
	public UniqueKeyException() {
		super("Keys of samples (first column) are not unique");
	}
	public void setTestSampleeID(String testSampleID2) {
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