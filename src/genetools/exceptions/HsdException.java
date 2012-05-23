package genetools.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class HsdException extends Exception {

	private int lineExceptionOccured = -1;

	public HsdException() {
		super();
	}

	public HsdException(String message) {
		super(message);
	}

	public HsdException(Throwable cause) {
		super(cause);
	}

	public HsdException(String message, Throwable cause) {
		super(message, cause);
	}

	public void setLineExceptionOccured(int lineExceptionOccured) {
		this.lineExceptionOccured = lineExceptionOccured;
		
	}

	public int getLineExceptionOccured() {
		return lineExceptionOccured;
	}
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject newJSONException = new JSONObject();
		newJSONException.put("lineOfError", lineExceptionOccured);
		
		return newJSONException;
	}
}