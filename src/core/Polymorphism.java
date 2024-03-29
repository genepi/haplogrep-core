package core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import phylotree.Annotation;

/**
 * Represents one polymorphism
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class Polymorphism implements Comparable<Polymorphism>, Serializable {

	private static final long serialVersionUID = 777223002615235532L;
	private int position;
	private Mutations mutation;
	boolean isBackMutation = false;
	private String numberOfIns = "";
	private String insertedPolys = "";
	boolean isHeteroplasmy = false;
	int isReliable = 0;
	public Reference reference;
	private int hashCode;

	/**
	 * rCRS reference sequence
	 */

	/**
	 * Creates a new Polymorphism instance
	 * 
	 * @param newPosition
	 *            The position on the mtDNA
	 * @param mutatedBase
	 *            The muation on the given position
	 */
	Polymorphism(Reference reference, int newPosition, Mutations mutatedBase) {
		this.reference = reference;
		this.mutation = mutatedBase;
		this.position = newPosition;
		hashCode = toString().hashCode();
	}

	Polymorphism(Reference reference, int newPosition, Mutations mutatedBase, int isReliable) {
		this.reference = reference;
		this.mutation = mutatedBase;
		this.position = newPosition;
		hashCode = toString().hashCode();
		this.setReliable(isReliable);
	}

	/**
	 * Creates a new Polymorphism instance by parsing an input string
	 * 
	 * @param The
	 *            string to parse
	 * @throws InvalidPolymorphismException
	 *             If the string could not be parsed
	 */
	public Polymorphism(Reference reference, String phyloString) throws InvalidPolymorphismException {
		this.reference = reference;
		parse(phyloString);
		hashCode = toString().hashCode();
		this.setHeteroplasmy(false);
	}

	public Polymorphism(Reference reference, String phyloString, int isReliable) throws InvalidPolymorphismException {
		this.reference = reference;
		parse(phyloString);
		hashCode = toString().hashCode();
		this.setHeteroplasmy(false);
		this.setReliable(isReliable);
	}

	public Polymorphism(Reference reference, String phyloString, boolean Heteroplasmy) throws InvalidPolymorphismException {
		this.reference = reference;
		parse(phyloString);
		this.setHeteroplasmy(Heteroplasmy); // TODO CHECK HETEREOPLASMY
		hashCode = toString().hashCode();
	}

	/**
	 * Copy constructor
	 * 
	 * @param polyToCopy
	 *            The polymorhism to copy
	 */
	public Polymorphism(Polymorphism polyToCopy) {
		this.position = polyToCopy.position;
		this.mutation = polyToCopy.mutation;
		this.isBackMutation = polyToCopy.isBackMutation;
		this.numberOfIns = polyToCopy.numberOfIns;
		this.insertedPolys = polyToCopy.insertedPolys;
		this.isHeteroplasmy = polyToCopy.isHeteroplasmy;
		this.reference = polyToCopy.reference;

		hashCode = toString().hashCode();
	}

	@Override
	public String toString() {

		if (position > reference.getLength()) {
			return "";
		}

		if (position != 0) {
			if (!isBackMutation && !this.mutation.equals("N")) {
				if (this.mutation == Mutations.INS)
					return position + numberOfIns + insertedPolys;

				else if (this.mutation == Mutations.DEL) {
					return position + "d";
				}

				else {
					return position + mutation.toString().trim();
				}
			}

			else {
				return position + mutation.toString().trim() + "!";
			}
		} else {
			return "";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Polymorphism other = (Polymorphism) obj;
		// if (hashCode != other.hashCode)
		// return false;
		if (isBackMutation != other.isBackMutation)
			return false;
		if (mutation != other.mutation)
			return false;
		if (position != other.position)
			return false;
		return true;
	}

	/**
	 * @return A shorter string version of this polymorphism instance. Removes
	 *         obvious transitions and changes 'DEL' to 'd'
	 */
	public String toStringShortVersion() {
		String convertedString = "";
		// if (!isBackMutation) {
		if (this.mutation == Mutations.INS)
			convertedString = position + numberOfIns + insertedPolys;

		else if (this.mutation == Mutations.DEL)
			convertedString = position + "d";

		else {

			if (isTransitionPoly()) {
				convertedString = String.valueOf(position);
			}

			else {
				if (position != 0) {
					convertedString = position + mutation.toString().trim();
				}
			}
		}

		if (isBackMutation) {
			return convertedString + "!";
		}

		return convertedString;
	}

	/**
	 * Reformat a string representation of a polymorphism with back mutation
	 * from '!' to '@' e.g. 185! to '@185'
	 * 
	 * @param poly
	 * @return The reformatted string
	 */
	public static String convertToATBackmutation(String poly) {
		if (poly.contains("!")) {
			poly = poly.replace("!", "");
			return "@" + poly;
		} else {
			return poly;
		}
	}

	/**
	 * Check if a polymorphism represents the reference of rCRS
	 * 
	 * @return True if reference, false otherwise
	 */
	public boolean equalsReference() {
		return this.equals(getReferenceBase(this.position));
	}

	/**
	 * Creates and returns a polymorphism representing the reference at the
	 * position of this instance
	 * 
	 * @return The polymorphism representing the reference
	 */
	public Polymorphism getReferenceBase() {
		return getReferenceBase(this.position);
	}

	/**
	 * Creates and returns a polymorphism representing the reference at a given
	 * position
	 * 
	 * @param position
	 *            The position in the reference sequence
	 * @return The polymorphism representing the reference
	 */
	private Polymorphism getReferenceBase(int position) {
		if (position <= reference.getLength() && position > 0) {
			String base = String.valueOf(reference.getSequence().charAt(position - 1));
			base = base.toUpperCase();
			try {
				return new Polymorphism(reference, position, Mutations.getBase(base));
			} catch (InvalidBaseException e) {
				e.printStackTrace();
				return null;
			}

		} else {
			return null;
		}
	}

	/**
	 * Parses a string to create a new polymorphism instance
	 * 
	 * @param stringToParse
	 *            The input string
	 * @throws InvalidPolymorphismException
	 *             If the string could not be parsed
	 */
	private void parse(String stringToParse) throws InvalidPolymorphismException {
		// Because of deletion and insertion more than one resulting
		// polymorphisms are possible
		StringTokenizer st1 = null;
		stringToParse = stringToParse.trim();

		try {
			// ignore R on insertions
			if (stringToParse.contains("R") && !stringToParse.contains(".")) {
				this.position = Integer.valueOf(stringToParse.substring(0, stringToParse.length() - 1));

				if (getReferenceBase(position).equals("A"))
					this.mutation = Mutations.G;
				if (getReferenceBase(position).equals("G"))
					this.mutation = Mutations.A;
			}
			// ignore Y on insertions
			if (stringToParse.contains("Y") && !stringToParse.contains(".")) {
				this.position = Integer.valueOf(stringToParse.substring(0, stringToParse.length() - 1));
				if (getReferenceBase(position).equals("C"))
					this.mutation = Mutations.T;
				if (getReferenceBase(position).equals("T"))
					this.mutation = Mutations.C;
			}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			throw new InvalidPolymorphismException(stringToParse);
		}

		// Only use part in parentheses
		if (stringToParse.startsWith("("))
			stringToParse = stringToParse.substring(1, stringToParse.length() - 1);

		// BACKMUTATION
		if (stringToParse.contains("!")) {
			stringToParse = stringToParse.replace("!", "");
			isBackMutation = true;
		}
		if (stringToParse.contains("@")) {
			stringToParse = stringToParse.replace("@", "");
			isBackMutation = true;
		}

		// DELETIONS
		if (stringToParse.toUpperCase().contains("D")) {
			stringToParse = stringToParse.replace("del", "");
			stringToParse = stringToParse.replace("d", "");
			stringToParse = stringToParse.replace("DEL", "");
			stringToParse = stringToParse.replace("D", "");
			this.position = Integer.valueOf(stringToParse);
			this.mutation = Mutations.DEL;
		}

		// always .1 -> INS are changed from .1C,.2C,.3C to .1CCC in Sample
		// Class
		// .2 -> only from phylotree (455.2T and 2232.2A)
		else if (stringToParse.contains(".")) {
			st1 = new StringTokenizer(stringToParse, ".");
			String token = st1.nextToken();
			String token1 = st1.nextToken().trim();
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(token1);
			m.find();
			this.position = Integer.valueOf(token);
			this.mutation = Mutations.INS;
			String mutationString = "";
			// i=2 because of PhyloTree
			for (int i = 0; i <= 2; i++) {
				String number = String.valueOf(i);

				if (token1.contains(number)) {
					mutationString = token1.replace(number, "");
					this.numberOfIns = "." + number;
				}
			}
			if (token1.contains("X")) {
				mutationString = token1.replace("X", "");
				this.numberOfIns = ".X";
			}

			// Check for valid acid
			int i = 0;
			try {
				for (i = 0; i < mutationString.length(); i++) {
					Mutations.getBase(String.valueOf(mutationString.charAt(i)));
				}
			} catch (InvalidBaseException e) {
				throw new InvalidPolymorphismException(stringToParse, String.valueOf(mutationString.charAt(i)));
			}
			this.insertedPolys = mutationString;
		}

		// TRANSITION/TRANSVERSION If base is included, its a transversion, so
		// just
		// take it as it is.
		else {
			Pattern p = Pattern.compile("[a-zA-Z]");
			Matcher m = p.matcher(stringToParse);
			if (m.find()) {
				try {
					this.mutation = Mutations.getBase(stringToParse.substring(m.start(), m.end()));
					this.position = Integer.valueOf(stringToParse.replaceFirst("[a-zA-Z]", ""));
				} catch (InvalidBaseException e) {
					throw new InvalidPolymorphismException(stringToParse, stringToParse.substring(m.start(), m.end()));
				} catch (NumberFormatException e) {
					throw new InvalidPolymorphismException(stringToParse);
				}

			}

			else {

				p = Pattern.compile("\\d+");
				m = p.matcher(stringToParse);
				m.find();

				try {
					if (stringToParse.trim().length() > 0)
						Integer.parseInt(stringToParse.substring(m.start(), m.end()), 10);
				} catch (NumberFormatException e) {
					throw new InvalidPolymorphismException(stringToParse);
				}
				int position = 0;
				if (stringToParse.trim().length() > 0) {
					position = Integer.valueOf(stringToParse);
					try {
						getTransitionPoly(position);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						position = 0;
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void getTransitionPoly(int position) throws InvalidBaseException {

		this.position = position;
		try {

			if (getReferenceBase(position).mutation == Mutations.C) {
				this.mutation = Mutations.T;
			}
			if (getReferenceBase(position).mutation == Mutations.T) {
				this.mutation = Mutations.C;
			}
			if (getReferenceBase(position).mutation == Mutations.G) {
				this.mutation = Mutations.A;
			}
			if (getReferenceBase(position).mutation == Mutations.A) {
				this.mutation = Mutations.G;
			}
		} catch (Exception e) {
			try {
				throw new InvalidPolymorphismException("" + position, "" + position);
			} catch (InvalidPolymorphismException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public boolean isTransitionPoly() {

		if (this.mutation == Mutations.T && getReferenceBase().mutation == Mutations.C) {
			return true;
		}

		if (this.mutation == Mutations.C && getReferenceBase().mutation == Mutations.T) {
			return true;
		}

		if (this.mutation == Mutations.A && getReferenceBase().mutation == Mutations.G) {
			return true;
		}

		if (this.mutation == Mutations.G && getReferenceBase().mutation == Mutations.A) {
			return true;
		}

		else
			return false;
	}

	public String getInsertedPolys() {
		return insertedPolys;
	}

	@Override
	public int compareTo(Polymorphism o) {

		if (this.position < o.position)
			return -1;

		if (this.position == o.position) {
			if ((this.toString().length() < o.toString().length()))
				return -1;
			if ((this.toString().length() == o.toString().length()))
				return this.mutation.name().compareTo(o.mutation.name());
			else
				return 1;
		}

		else
			return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + hashCode;
		result = prime * result + (isBackMutation ? 1231 : 1237);
		result = prime * result + ((mutation == null) ? 0 : mutation.hashCode());
		result = prime * result + position;
		return result;
	}

	/**
	 * @return The position of the polymorphism
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @return The mutation of the polymorphism at the position
	 */
	public Mutations getMutation() {
		return mutation;
	}

	/**
	 * @return True if the polymorphism is a back mutation, false if it is not
	 */
	public boolean isBackMutation() {
		return isBackMutation;
	}

	/**
	 * Changes the polymorphism to a back mutation or vice versa
	 * 
	 * @param isBackMutation
	 *            The new back mutation state
	 */
	public void setBackMutation(boolean isBackMutation) {
		this.isBackMutation = isBackMutation;
		hashCode = toString().hashCode();
	}

	/**
	 * @return True if the polymorphism is a heteroplasmy (R or Y), false if it
	 *         is not
	 */
	public boolean isHeteroplasmy() {
		return isHeteroplasmy;
	}

	public void setHeteroplasmy(boolean isHeteroplasmy) {
		this.isHeteroplasmy = isHeteroplasmy;
	}

	public int isReliable() {
		return isReliable;
	}

	public void setReliable(int isReliable) {
		this.isReliable = isReliable;
	}

	public AnnotationAAC getAnnotation() {

		HashMap<Polymorphism, AnnotationAAC> annotationLookup = Annotation.getInstance().getAnnotation(reference);

		AnnotationAAC annotation = null;

		if (annotationLookup.containsKey(this)) {
			annotation = annotationLookup.get(this);
		}

		return annotation;

	}

}
