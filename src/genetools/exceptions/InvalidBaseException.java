package genetools.exceptions;

public class InvalidBaseException extends Exception
{	
	public InvalidBaseException(String base) {
		super("The base " + base + " is not supported!");
	}

}
