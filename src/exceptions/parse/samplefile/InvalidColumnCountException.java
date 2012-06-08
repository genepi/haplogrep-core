package exceptions.parse.samplefile;


import org.json.JSONException;
import org.json.JSONObject;

import exceptions.parse.HsdFileException;

/**
 * Represents a exception thrown if a hsd don't show the right number of columns 
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class InvalidColumnCountException extends HsdFileException {

	private static final long serialVersionUID = -2297850578045810738L;

	public InvalidColumnCountException(int columns)
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
