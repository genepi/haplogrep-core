package core;

import java.util.ArrayList;

import exceptions.parse.sample.InvalidRangeException;

/**
 * Represents the ranges of one sample.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */

public class SampleRanges {

	ArrayList<Integer> starts = new ArrayList<Integer>();
	ArrayList<Integer> ends = new ArrayList<Integer>();

	/**
	 * Creates empty range.
	 */
	public SampleRanges() {

	}

	/**
	 * Copy constructor
	 * 
	 * @param rangeToCopy
	 *            The SampleRanges object to copy
	 */
	public SampleRanges(SampleRanges rangeToCopy) {
		starts.addAll(rangeToCopy.starts);
		ends.addAll(rangeToCopy.ends);
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
	public SampleRanges(String rangesToParse) throws InvalidRangeException {
		if (rangesToParse.equals(""))
			return;

		String[] ranges = rangesToParse.split(";");

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
				if (to > 16570 || from > 16570)
					throw new InvalidRangeException();

				try {
					// make one range to two ranges
					if (from > to) {
						this.addCustomRange(from, 16569);
						this.addCustomRange(1, to);
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
	public void addCompleteRange() {
		starts.add(1);
		ends.add(16569);
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
		for (int i = 0; i < starts.size(); i++) {
			if (starts.get(i) <= polyToCheck.getPosition() && ends.get(i) >= polyToCheck.getPosition())
				return true;
		}

		return false;
	}

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
}
