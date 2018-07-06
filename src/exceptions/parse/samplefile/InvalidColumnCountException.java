package exceptions.parse.samplefile;


import org.json.JSONException;
import org.json.JSONObject;

import exceptions.parse.HsdFileException;

/**
 * Represents a exception thrown if a hsd don't show the right number of columns 
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class InvalidColumnCountException extends HsdFileException {

	private static final long serialVersionUID = -2297850578045810738L;

	public InvalidColumnCountException(int columns)
	{
		super("File includes " + columns + " column(s) only. No variants detected.");
	}
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = super.toJSON();
		newJSONException.put("message", this.getMessage());
		
		return newJSONException;
	}
}
