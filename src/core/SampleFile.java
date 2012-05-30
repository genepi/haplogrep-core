package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.jdom.Element;

import qualityAssurance.QualityIssue;

import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidRangeException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import exceptions.parse.samplefile.UniqueKeyException;



public class SampleFile {
	Hashtable<String,TestSample> testSamples = new Hashtable<String,TestSample>();
	ArrayList<QualityIssue> qualityIssues = new ArrayList<QualityIssue>();
	
	
	public SampleFile(ArrayList<String> sampleLines) throws HsdFileException
	{
		int lineIndex = 1;
		for(String currentLine : sampleLines)
		{
			TestSample newSample;
			try {
				newSample = TestSample.parse(currentLine);
			} catch (HsdFileException e) {
				e.setLineExceptionOccured(lineIndex);
				throw e;
			}
			if(testSamples.containsKey(newSample.getSampleID()))
				try {
					throw new UniqueKeyException();
				} catch (UniqueKeyException e) {
					e.setLineExceptionOccured(lineIndex);
					e.setTestSampleeID(newSample.getSampleID());
					throw e;
				}
			else
			testSamples.put(newSample.getSampleID(), newSample);
			lineIndex++;
		}
	}
	//depends on the read in method
	public SampleFile(String pathToSampleFile,boolean testCase) throws IOException, NumberFormatException, HsdFileException, InvalidBaseException, InvalidRangeException, InvalidColumnCountException 
	{
		BufferedReader sampleFileStream;
		if(testCase){ //for test cases
		File sampleFile = new File(pathToSampleFile);
		System.out.println("%%%% "+pathToSampleFile);
		sampleFileStream = new BufferedReader(new FileReader(sampleFile));
		}
		else{ //"Load Testdata" button
		InputStream testFile = this.getClass().getClassLoader().getResourceAsStream(pathToSampleFile);
		sampleFileStream = new BufferedReader ( new InputStreamReader ( testFile ) );
		}
		String currentLine = sampleFileStream.readLine();
		
		while (currentLine != null) {
			TestSample newSample = TestSample.parse(currentLine);
			testSamples.put(newSample.getSampleID(), newSample);

			currentLine = sampleFileStream.readLine();
		}
		
	}
	
	public TestSample getTestSample(String sampleID)
	{
		return testSamples.get(sampleID);
	}
	
	public ArrayList<TestSample> getTestSamples()
	{
		return new ArrayList<TestSample>(testSamples.values());
	}
	
	public String toString()
	{
		String result = "";
		
		for(TestSample currenTestSample : testSamples.values())
		{
			result += currenTestSample.toString() + System.getProperty("line.separator");
		}
		
		return result;
	}
	
	

	public Element toXMLString() {
		Element root = new Element("catalog");
		
		for (TestSample sample : testSamples.values()) {
			Element newElement = new Element("mtDNA_lines");
			Element newElement1 = new Element("sample_name");
			newElement1.setText(sample.getSampleID().toString());
			newElement.addContent(newElement1);
			newElement1 = new Element("range");
			SampleRange range = sample.getSampleRanges();
			ArrayList<Integer> startRange = range.getStarts();
			
			ArrayList<Integer> endRange = range.getEnds();
			StringBuffer result = new StringBuffer();
			for(int i=0;i<startRange.size();i++){
				if(startRange.get(i).equals(endRange.get(i))){result.append(startRange.get(i)+"; ");}
				else {result.append(startRange.get(i)+"-"+endRange.get(i)+"; ");}
			}
			newElement1.setText(result.toString());
			newElement.addContent(newElement1);
			//if(sample.getExpectedHaplogroup().toString().equals(""))
			newElement1 = new Element("haplogroup");
			
			//if no haplogroup is predefinied, than set our result to predefinied
			if(sample.getExpectedHaplogroup().toString().equals("")&&
					sample.getDetectedHaplogroup() != null ){ 
			sample.setExpectedHaplogroup(sample.getDetectedHaplogroup());
			sample.setState("top rank");
			}
			if(sample.getDetectedHaplogroup() != null 
					&& !sample.getDetectedHaplogroup().equals(sample.getExpectedHaplogroup())
					)
				newElement1.setText(sample.getExpectedHaplogroup().toString() 
						+ " (" +sample.getDetectedHaplogroup() .toString() + ")");
			else
			{
				newElement1.setText(sample.getExpectedHaplogroup().toString());	}
			newElement.addContent(newElement1);
			
			newElement1 = new Element ("status");
			newElement1.setText(String.valueOf(sample.getState()));
			newElement.addContent(newElement1);
			
			newElement1 = new Element ("hit");
			newElement1.setText(String.valueOf(sample.getResultQuality()));
			newElement.addContent(newElement1);
			
			//parse Polymorphisms
			ArrayList<Polymorphism> t = sample.getSample().getPolymorphismn();
			String polys = "";
			for (Polymorphism t1 : t)
				polys += t1.toString()+" ";
			newElement1 = new Element("polys");
			newElement.addContent(newElement1);
			newElement1.setText(polys);
			root.addContent(newElement);
		}
		
		return root;
		
	}
	public ArrayList<QualityIssue> getQualityIssues() {
		return qualityIssues;
	}
	
}
