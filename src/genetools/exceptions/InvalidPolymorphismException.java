package genetools.exceptions;

public class InvalidPolymorphismException extends Exception {
	
	public InvalidPolymorphismException(String poly, String base) {
		super("Invalid base in polymorphism description " + poly + " found. The base " + base + " is not supported!");
	}

}
