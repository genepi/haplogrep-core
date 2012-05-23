package genetools.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class InvalidHsdFileColumnCount extends HsdException {
	public InvalidHsdFileColumnCount(int columns)
	{
		super("A hsd file consists of more than 4 tab seperated columns. " +
				"The used file had only " + columns + " column(s)");
	}
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		
		return newJSONException;
	}
}
