package exceptions.parse.sample;


import org.json.JSONException;
import org.json.JSONObject;

public class InvalidRangeException extends HsdFileSampleParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -524791862430055582L;

	public InvalidRangeException(String rangesToParse) {
		super("Invalid sample range format detected. " + rangesToParse + " could not be read!");
	}
	public InvalidRangeException() {
		super("Invalid sample range. Region allowed: 1-16569");
	}
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		return newJSONException;
	}
}
