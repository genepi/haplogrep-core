package exceptions.parse.sample;

public class InvalidPolymorphismException extends HsdFileSampleParseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 663352021254006358L;

	public InvalidPolymorphismException(String poly, String base) {
		super("Invalid base in polymorphism description " + poly + " found. The base " + base + " is not supported!");
	}

	public InvalidPolymorphismException(String polyString)
	{
		super("The polymorphism " + polyString +" contains some invalid characters");
	}
}
