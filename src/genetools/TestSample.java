package genetools;

import genetools.exceptions.HsdException;
import genetools.exceptions.HsdFileParseException;
import genetools.exceptions.InvalidBaseException;
import genetools.exceptions.InvalidFormatException;
import genetools.exceptions.InvalidHsdFileColumnCount;
import genetools.exceptions.InvalidHsdFileException;
import genetools.exceptions.InvalidPolymorphismException;
import genetools.exceptions.InvalidRangeException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TestSample implements Comparable<TestSample>{
	
	private Haplogroup predefiniedHaplogroup;
	private Haplogroup recognizedHaplogroup;
	private Sample sample;
	private String testSampleID = "Unknown";
	private SampleRange sampleRange = null;
	private String state="n/a";
	private double resultQuality=0;


	
	public TestSample (String sampleID, Haplogroup predefiniedHaplogroup,Sample sample, SampleRange sampleRange, String state) 
	{
		this.testSampleID = sampleID;
		this.predefiniedHaplogroup = predefiniedHaplogroup;
		this.sample = sample;
		this.sampleRange = sampleRange;
		this.state=state;
	}



	public TestSample(String currentLine) throws InvalidRangeException, NumberFormatException,InvalidHsdFileColumnCount, HsdFileParseException {
		
		String[] tokens = currentLine.split("\t");
		
		if(tokens.length < 4)
			throw new InvalidHsdFileColumnCount(tokens.length);
		
		this.testSampleID = tokens[0].trim();
		
		tokens[1] = tokens[1].replaceAll("\"", "");
		this.sampleRange = new SampleRange(tokens[1]);
		
		if(tokens[2].equals("?") || tokens[2].equals("SEQ"))
		this.predefiniedHaplogroup = new Haplogroup("");
		
		else
			this.predefiniedHaplogroup = new Haplogroup(tokens[2]);
		
		//Interpret the rest of the line polymorhismn tokens
		StringBuffer sampleString = new StringBuffer();
		for(int i = 3; i < tokens.length;i++)
		{
			//if(  !tokens[i].contains("R") &&  !tokens[i].contains("S") &&  !tokens[i].contains("K") &&  !tokens[i].contains("Y") &&  !tokens[i].contains("W")&&  !tokens[i].contains("M")){
				
			
			sampleString.append(tokens[i] + " ");
			//}
		}
		
		
			try {
				this.sample = new Sample(sampleString.toString(),0);
			} catch (NumberFormatException e) {
				HsdFileParseException ex = new HsdFileParseException(e.getMessage());
				ex.setTestSampleID(this.testSampleID);			
				throw ex;
			}
			catch (InvalidPolymorphismException e) {
				HsdFileParseException ex = new HsdFileParseException(e.getMessage());
				ex.setTestSampleID(this.testSampleID);			
				throw ex;
			}
			catch (InvalidFormatException e) {
				HsdFileParseException ex = new HsdFileParseException(e.getMessage());
				ex.setTestSampleID(this.testSampleID);			
				throw ex;
			}
		
	}

	public Haplogroup getPredefiniedHaplogroup() {	
		return predefiniedHaplogroup;
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

	public String toString()
	{
		String result = testSampleID + "\t" + predefiniedHaplogroup + "\t";
		
		for(Polymorphism currentPoly : sample.sample)
		{
			result += currentPoly.toString() + " ";
		}
		
		return result;	
	}

	public String getSampleID() {
		return testSampleID;
	}

	
	public String getStatus() {
		return state;
	}

	public void setState(String status) {
		state = status;
	}
	

	public Haplogroup getRecognizedHaplogroup() {
		return recognizedHaplogroup;
	}

	public void setRecognizedHaplogroup(Haplogroup recognizedHaplogroup) {
		this.recognizedHaplogroup = recognizedHaplogroup;
	}
	public void setPredefiniedHaplogroup(Haplogroup predefiniedHaplogroup) {
		this.predefiniedHaplogroup = predefiniedHaplogroup;
	}


	public void setResultQuality(double myDec) {
		this.resultQuality = myDec;
	}



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
		setRecognizedHaplogroup(hg);


			
			double firstRank=(rank);
			BigDecimal myDec = new BigDecimal( firstRank );
			myDec = myDec.setScale( 1, BigDecimal.ROUND_HALF_UP );
			setResultQuality(myDec.doubleValue());		
			
			//set status for colors
			if(getPredefiniedHaplogroup().equals(getRecognizedHaplogroup()))
				setState("identical");
			else if(getPredefiniedHaplogroup().isSuperHaplogroup(getRecognizedHaplogroup())||
					getRecognizedHaplogroup().isSuperHaplogroup(getPredefiniedHaplogroup()))
				setState("similar");
			else setState("mismatch");
			

		}
}
