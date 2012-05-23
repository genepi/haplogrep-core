package genetools.tests;

import genetools.Polymorphism;
import genetools.Sample;
import genetools.SampleFile;
import genetools.SampleRange;
import genetools.exceptions.HsdException;
import genetools.exceptions.InvalidBaseException;
import genetools.exceptions.InvalidFormatException;
import genetools.exceptions.InvalidHsdFileColumnCount;
import genetools.exceptions.InvalidHsdFileException;
import genetools.exceptions.UniqueKeyException;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;


public class SampleFileTests {
	@Test
	public void ParseHSDSampleFormatTest() throws NumberFormatException, InvalidBaseException, InvalidFormatException, IOException, InvalidHsdFileException, HsdException, InvalidHsdFileColumnCount, UniqueKeyException
	{
		ArrayList<String> sampleTokens = new ArrayList<String>();
		sampleTokens.add("1	1..576;16024..16569	H	249DEL 263G");
		
		SampleFile newSampleFile = new SampleFile(sampleTokens);
		

		Assert.assertEquals( "1",newSampleFile.getTestSamples().get(0).getSampleID());
		Assert.assertEquals( "1-576 ; 16024-16569 ;",newSampleFile.getTestSamples().get(0).getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSamples().get(0).getPredefiniedHaplogroup().toString());
		Assert.assertEquals( "249DEL 263G",newSampleFile.getTestSamples().get(0).getSample().toString());
		
	}
	
	@Test
	public void ParseHSDFileTest() throws NumberFormatException, InvalidBaseException, InvalidFormatException, IOException, InvalidHsdFileException, HsdException, InvalidHsdFileColumnCount
	{
		SampleFile newSampleFile = new SampleFile("../docs/testSamples/hsdFiles/FullGenomes.hsd",true);
		
		Assert.assertEquals( "10129189",newSampleFile.getTestSample("10129189").getSampleID());
		//Assert.assertEquals( "1..16569 ;",newSampleFile.getTestSample("10129189").getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSample("10129189").getPredefiniedHaplogroup().toString());
		Assert.assertEquals( "263G 315.1C 750G 1438G 1809C 4769G 8860G 15326G 16519C",newSampleFile.getTestSample("10129189").getSample().toString());
		
	}
	
	@Test
	public void ParseInvalidHSDFileTest() throws NumberFormatException, InvalidBaseException, InvalidFormatException, IOException, InvalidHsdFileException, HsdException, InvalidHsdFileColumnCount, UniqueKeyException
	{
		ArrayList<String> sampleTokens = new ArrayList<String>();
		sampleTokens.add("1	1..576;16024..16569	HH	249DEL 263G");
		
		SampleFile newSampleFile = new SampleFile(sampleTokens);
	
		Assert.assertEquals( "1",newSampleFile.getTestSamples().get(0).getSampleID());
		Assert.assertEquals( "1..576 ; 16024..16569 ;",newSampleFile.getTestSamples().get(0).getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSamples().get(0).getPredefiniedHaplogroup().toString());
		Assert.assertEquals( "249DEL 263G",newSampleFile.getTestSamples().get(0).getSample().toString());
		
	}
}
