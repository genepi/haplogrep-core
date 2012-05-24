package exceptions.parse.samplefile;

import org.json.JSONException;
import org.json.JSONObject;

public class HsdFileException extends Exception {

	private int lineExceptionOccured = -1;

	public HsdFileException() {
		super();
	}

	public HsdFileException(String message) {
		super(message);
	}

	public HsdFileException(Throwable cause) {
		super(cause);
	}

	public HsdFileException(String message, Throwable cause) {
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