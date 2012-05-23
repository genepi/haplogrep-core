package genetools.exceptions;


public class InvalidFormatException extends HsdException {
	public InvalidFormatException(String polyString)
	{
		super("The polymorphism " + polyString +" contains some invalid characters");
	}
}