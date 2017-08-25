package exceptions.parse.sample;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a exception thrown if the sample ranges are invalid.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class InvalidRangeException extends HsdFileSampleParseException {

	private static final long serialVersionUID = -524791862430055582L;

	public InvalidRangeException(String rangesToParse) {
		super("Invalid sample range format detected. " + rangesToParse + " could not be read!");
	}
	
	/* (non-Javadoc)
	 * @see exceptions.parse.sample.HsdFileSampleParseException#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		return newJSONException;
	}
}
