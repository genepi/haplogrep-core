package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import exceptions.parse.sample.InvalidRangeException;

/**
 * Represents the ranges of a sample.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */

public class SampleRanges {

	private ArrayList<Integer> starts = new ArrayList<Integer>();
	private ArrayList<Integer> ends = new ArrayList<Integer>();
	static HashSet<Integer> metaboChipPositions = null;
	private int length;

	/**
	 * Creates empty range.
	 */
	public SampleRanges() {
		if (metaboChipPositions == null) {
			metaboChipPositions = new HashSet<Integer>();

			// loadMetaboChipPositions();
		}
	}

	private void loadMetaboChipPositions() {
		try {
			InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream("metaboChipPositions");

			if (phyloFile == null)
				phyloFile = new FileInputStream(new File("testDataFiles/metaboChipPositions"));

			BufferedReader reader = new BufferedReader(new InputStreamReader(phyloFile));

			String currentLine = reader.readLine();
			while (currentLine != null) {
				Scanner sc = new Scanner(currentLine);
				sc.next();
				sc.next();
				Integer newPosition = Integer.parseInt(sc.next().replace("mt", ""));
				metaboChipPositions.add(newPosition);
				currentLine = reader.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Parsed a new SampleRanges object. Needs hsd file format of ranges.
	 * 
	 * @param rangesToParse
	 *            The string to parse
	 * @throws InvalidRangeException
	 *             Thrown if the format is incorrect or the ranges are invalid
	 *             (e.g. < 0)
	 */
	public SampleRanges(String rangesToParse, Reference reference, boolean splitRange16569) throws InvalidRangeException {

		/*
		 * if(metaboChipPositions == null){ metaboChipPositions = new
		 * HashSet<Integer>();
		 * 
		 * loadMetaboChipPositions(); }
		 */

		if (rangesToParse.equals(""))
			return;

		String[] ranges = rangesToParse.split(";");

		this.length = ranges.length;

		for (int i = 0; i < ranges.length; i++) {
			// A range was defined
			if (ranges[i].contains("-")) {
				String[] rangeParts = ranges[i].split("-");
				if (rangeParts.length != 2)
					throw new InvalidRangeException(rangesToParse);

				// parse range if first number is greater than second
				int from = 0;
				int to = 0;
				try {
					from = Integer.valueOf(rangeParts[0].trim());
					to = Integer.valueOf(rangeParts[1].trim());
				} catch (NumberFormatException e) {
					throw new InvalidRangeException(rangeParts[0] + " " + rangeParts[1]);
				}
				if (to > reference.getLength() + 1 || from > reference.getLength() + 1)
					throw new InvalidRangeException(to + " > " + from);

				try {
					// make one range to two ranges
					if (from > to) {
						if (splitRange16569) {
							this.addCustomRange(from, reference.getLength());
							this.addCustomRange(1, to);
						} else
							this.addCustomRange(Integer.parseInt(rangeParts[0].trim()), Integer.parseInt(rangeParts[1].trim()));
					}
					// standard
					else
						this.addCustomRange(Integer.parseInt(rangeParts[0].trim()), Integer.parseInt(rangeParts[1].trim()));
				} catch (NumberFormatException e) {
					throw new InvalidRangeException(rangesToParse);
				}
			}

			// Only one position was defined
			else {
				try {
					this.addCustomRange(Integer.parseInt(ranges[i].trim()), Integer.parseInt(ranges[i].trim()));
				} catch (NumberFormatException e) {
					throw new InvalidRangeException(rangesToParse);
				}
			}
		}
	}

	/**
	 * Adds the complete range(1-16569) to this SampleRanges instance,
	 */
	public void addCompleteRange(Reference reference) {
		starts.add(1);
		ends.add(reference.getLength());
	}

	/**
	 * Adds the control range(16024-16569 1-576) to this SampleRanges instance,
	 */
	public void addControlRange() {

		starts.add(16024);
		ends.add(16569);

		starts.add(1);
		ends.add(576);
	}

	/**
	 * Adds the Metabo chip positions to this SampleRanges instance,
	 */
	public void addMetaboChipRange() {
		for (int currentMetaboPosition : metaboChipPositions) {
			starts.add(currentMetaboPosition);
			ends.add(currentMetaboPosition);
		}
	}

	/**
	 * Adds a custom range to this instance.
	 * 
	 * @param newRangeStart
	 * @param newRangeEnd
	 */
	public void addCustomRange(int newRangeStart, int newRangeEnd) {
		starts.add(newRangeStart);
		ends.add(newRangeEnd);
	}

	/**
	 * Checks if a polymorphism is contained by one of the ranges
	 * 
	 * @param polyToCheck
	 *            The polymorphism to check
	 * @return True if the polymorphism is contained, false otherwise
	 */
	public boolean contains(Polymorphism polyToCheck) {
		return getSubrangeID(polyToCheck) != -1 ? true : false;
	}
//	public boolean contains(Polymorphism polyToCheck) {
//		return containsPosition(polyToCheck.getPosition());
//	}
//
//	private boolean containsPosition(int position) {
//		for (int i = 0; i < starts.size(); i++) {
//			if (starts.get(i) <= position && ends.get(i) >= position)
//				return true;
//		}
//
//		return false;
//	}

	/**
	 * @return All start positions of ranges of this instances
	 */
	public ArrayList<Integer> getStarts() {
		return starts;
	}

	/**
	 * @return All end positions of ranges of this instance
	 */
	public ArrayList<Integer> getEnds() {
		return ends;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";

		for (int i = 0; i < starts.size(); i++) {
			if (starts.get(i) == ends.get(i)) {
				result += starts.get(i) + " ; ";
			} else
				result += starts.get(i) + "-" + ends.get(i) + " ; ";
		}
		return result.trim();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ends == null) ? 0 : ends.hashCode());
		result = prime * result + ((starts == null) ? 0 : starts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;
		SampleRanges other = (SampleRanges) obj;
		if (ends == null) {
			if (other.ends != null)
				return false;
		}

		else {
			for (int i : ends)
				if (!other.ends.contains(i))
					return false;
			for (int i : other.ends)
				if (!ends.contains(i))
					return false;

		}
		if (starts == null) {
			for (int i : starts)
				if (!other.starts.contains(i))
					return false;
		}

		if (starts == null) {
			for (int i : other.starts)
				if (!starts.contains(i))
					return false;
		}
		return true;
	}

	public boolean isMataboChipRange() {
		SampleRanges metaboChipRange = new SampleRanges();
		metaboChipRange.addMetaboChipRange();

		return metaboChipRange.equals(this);
	}

	public boolean isControlRange() {
		SampleRanges controlRange = new SampleRanges();
		controlRange.addControlRange();

		return controlRange.equals(this);
	}

	public boolean isCompleteRange(Reference reference) {
		SampleRanges completeRange = new SampleRanges();
		completeRange.addCompleteRange(reference);

		return completeRange.equals(this);
	}

	public void clear() {
		starts.clear();
		ends.clear();
	}

	public boolean isCustomRange(Reference reference) {

		return !isCompleteRange(reference) && !isControlRange() && !isMataboChipRange();
	}

	public int getSubrangeID(Polymorphism currentPoly) {
		int foundRangeID = -1;
		for (int i = 0; i < starts.size(); i++) {
			if ((starts.get(i) <= currentPoly.getPosition() && ends.get(i) >= currentPoly.getPosition())
					|| (starts.get(i) > ends.get(i) && (starts.get(i) >= currentPoly.getPosition() || ends.get(i) <= currentPoly.getPosition()))) {
				foundRangeID = i;
				break;
			}
		}
		return foundRangeID;
	}

	public SampleRanges getSubrange(int i) {
		SampleRanges newSubrange = new SampleRanges();
		newSubrange.addCustomRange(starts.get(i), ends.get(i));
		return newSubrange;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
