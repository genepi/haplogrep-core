package exceptions.parse.sample;

/**
 * Represents a exception thrown if the polymorphism format is invalid.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class InvalidPolymorphismException extends HsdFileSampleParseException {
	
	private static final long serialVersionUID = 663352021254006358L;

	public InvalidPolymorphismException(String poly, String base) {
		super("Invalid base in polymorphism description " + poly + " found. The base " + base + " is not supported!");
	}

	public InvalidPolymorphismException(String polyString)
	{
		super("The polymorphism " + polyString +" contains some invalid characters");
	}
}
