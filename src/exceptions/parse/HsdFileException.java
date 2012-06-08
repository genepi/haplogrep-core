package exceptions.parse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Root exceptions class for all exceptions dealing with hsd files operations
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class HsdFileException extends Exception {

	private static final long serialVersionUID = 5397423851831462161L;
	private int lineExceptionOccured = -1;

	public HsdFileException() {
		super();
	}

	public HsdFileException(String message) {
		super(message);
	}


	/**
	 * Sets the line in the hsd file the exception occurred
	 * @param lineExceptionOccured the line number
	 */
	public void setLineExceptionOccured(int lineExceptionOccured) {
		this.lineExceptionOccured = lineExceptionOccured;
		
	}

	/**
	 * @return The line in the hsd file the exception occurred
	 */
	public int getLineExceptionOccured() {
		return lineExceptionOccured;
	}
	
	/**
	 * Returns a JSON object if the exception. Can be used to transmit the exception to the web gui.
	 * @return	The json exception object
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = new JSONObject();
		newJSONException.put("lineOfError", lineExceptionOccured);
		
		return newJSONException;
	}
}