package core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeSet;

import qualityAssurance.Cerberus;
import search.SearchResult;
import exceptions.parse.sample.HsdFileSampleParseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.sample.InvalidRangeException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;

public class TestSample implements Comparable<TestSample>{
	
	private String testSampleID = "Unknown";
	private Haplogroup expectedHaplogroup;
	private Haplogroup detectedHaplogroup;
	private Sample sample;
	private SampleRange sampleRange = null;
	
	private TreeSet<SearchResult> allSearchResults;
	private Cerberus cerberus = null;
	
	private String state="n/a";
	private double resultQuality=0;

	public TestSample(){
		
	}
	
	public TestSample (String sampleID, Haplogroup predefiniedHaplogroup,Sample sample, SampleRange sampleRange, String state) 
	{
		this.testSampleID = sampleID;
		this.expectedHaplogroup = predefiniedHaplogroup;
		this.sample = sample;
		this.sampleRange = sampleRange;
		this.state=state;
	}

	/**
	 * Parses a new Test sample object from an input string
	 * @param inputString The string to parse
	 * @return The parsed string as new TestSample object
	 * @throws InvalidRangeException
	 * @throws InvalidColumnCountException
	 * @throws HsdFileSampleParseException
	 */
	public static TestSample parse(String inputString) throws HsdFileException {
		TestSample parsedSample = new TestSample();

		try {
			//Split the input string in separate column strings 
			String[] columns = inputString.split("\t");

			//Check of number of columns are correct
			if (columns.length < 4)
				throw new InvalidColumnCountException(columns.length);

			//Parse the test sample id
			parsedSample.testSampleID = columns[0].trim();

			//Parse range
			columns[1] = columns[1].replaceAll("\"", "");
			parsedSample.sampleRange = new SampleRange(columns[1]);

			//Parse expected haplogroup
			if (columns[2].equals("?") || columns[2].equals("SEQ"))
				parsedSample.expectedHaplogroup = new Haplogroup("");

			else
				parsedSample.expectedHaplogroup = new Haplogroup(columns[2]);

			// Parse the sample and all its polymprhisms
			StringBuffer sampleString = new StringBuffer();
			for (int i = 3; i < columns.length; i++) {
				sampleString.append(columns[i] + " ");
			}
			parsedSample.sample = new Sample(sampleString.toString(), 0);
		} 
		
		//Something went wrong during the parse process. Throw exception.
		 catch (InvalidPolymorphismException e) {
			HsdFileSampleParseException ex = new HsdFileSampleParseException(e.getMessage());
			ex.setTestSampleID(parsedSample.testSampleID);
			throw ex;
		}

		return parsedSample;
	}

	public Haplogroup getExpectedHaplogroup() {	
		return expectedHaplogroup;
	}

	public ArrayList<Polymorphism> getPolymorphismn() {
		return sample.sample;
	}

	public SampleRange getSampleRanges() {
		return sampleRange;
	}
	
	public Sample getSample() {
		return sample;
	}

	public Haplogroup getDetectedHaplogroup() {
		return detectedHaplogroup;
	}

	public void setDetectedHaplogroup(Haplogroup recognizedHaplogroup) {
		this.detectedHaplogroup = recognizedHaplogroup;
	}

	public void setExpectedHaplogroup(Haplogroup predefiniedHaplogroup) {
		this.expectedHaplogroup = predefiniedHaplogroup;
	}

	public String getSampleID() {
		return testSampleID;
	}

	public String toString()
	{
		String result = testSampleID + "\t" + expectedHaplogroup + "\t";
		
		for(Polymorphism currentPoly : sample.sample)
		{
			result += currentPoly.toString() + " ";
		}
		
		return result;	
	}

	
	//TODO Consider removing state and use new warning/error system instead
	public String getState() {
		return state;
	}

	public void setState(String status) {
		state = status;
	}
	

	//??
	public void setResultQuality(double myDec) {
		this.resultQuality = myDec;
	}
//??
	public double getResultQuality() {
		return resultQuality;
	}

	public ArrayList<Polymorphism>getPolyNotinRange()
	{
		ArrayList<Polymorphism> notInRangePolys = new ArrayList<Polymorphism>();
		for(Polymorphism currentPoly : getPolymorphismn())
		{
			if(!sampleRange.contains(currentPoly))
				notInRangePolys.add(currentPoly);
		}
		
		return notInRangePolys;
	}

	@Override
	public int compareTo(TestSample o) {
	
		 if(this.getSampleID().compareTo(o.getSampleID())<0)
			   return -1;
		 if (this.getSampleID().compareTo(o.getSampleID())>0)	
			  return 1;
		 else
			 return 0;
	}

	public void addRecommendedHaplogroups(Haplogroup hg, double rank) {
		setDetectedHaplogroup(hg);

		double firstRank = (rank);
		BigDecimal myDec = new BigDecimal(firstRank);
		myDec = myDec.setScale(1, BigDecimal.ROUND_HALF_UP);
		setResultQuality(myDec.doubleValue());

		// set status for colors
		if (getExpectedHaplogroup().equals(getDetectedHaplogroup()))
			setState("identical");
		else if (getExpectedHaplogroup().isSuperHaplogroup(getDetectedHaplogroup()) || getDetectedHaplogroup().isSuperHaplogroup(getExpectedHaplogroup()))
			setState("similar");
		else
			setState("mismatch");

	}
	
	void addNewSearchResult(SearchResult newResult){
		allSearchResults.add(newResult);
	}
}
