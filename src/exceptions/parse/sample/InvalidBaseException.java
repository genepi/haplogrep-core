package exceptions.parse.sample;

public class InvalidBaseException extends Exception
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6384409622623357252L;

	public InvalidBaseException(String base) {
		super("The base " + base + " is not supported!");
	}

}
