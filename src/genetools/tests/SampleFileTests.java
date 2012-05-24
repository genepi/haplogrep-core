package genetools.tests;

import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import exceptions.parse.samplefile.UniqueKeyException;
import genetools.SampleFile;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;


public class SampleFileTests {
	@Test
	public void ParseHSDSampleFormatTest() throws NumberFormatException, InvalidBaseException, IOException, HsdFileException, InvalidColumnCountException, UniqueKeyException
	{
		ArrayList<String> sampleTokens = new ArrayList<String>();
		sampleTokens.add("1	1..576;16024..16569	H	249DEL 263G");
		
		SampleFile newSampleFile = new SampleFile(sampleTokens);
		

		Assert.assertEquals( "1",newSampleFile.getTestSamples().get(0).getSampleID());
		Assert.assertEquals( "1-576 ; 16024-16569 ;",newSampleFile.getTestSamples().get(0).getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSamples().get(0).getExpectedHaplogroup().toString());
		Assert.assertEquals( "249DEL 263G",newSampleFile.getTestSamples().get(0).getSample().toString());
		
	}
	
	@Test
	public void ParseHSDFileTest() throws  InvalidBaseException, IOException, HsdFileException, InvalidColumnCountException
	{
		SampleFile newSampleFile = new SampleFile("../docs/testSamples/hsdFiles/FullGenomes.hsd",true);
		
		Assert.assertEquals( "10129189",newSampleFile.getTestSample("10129189").getSampleID());
		//Assert.assertEquals( "1..16569 ;",newSampleFile.getTestSample("10129189").getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSample("10129189").getExpectedHaplogroup().toString());
		Assert.assertEquals( "263G 315.1C 750G 1438G 1809C 4769G 8860G 15326G 16519C",newSampleFile.getTestSample("10129189").getSample().toString());
		
	}
	
	@Test
	public void ParseInvalidHSDFileTest() throws NumberFormatException, InvalidBaseException, IOException, HsdFileException, InvalidColumnCountException, UniqueKeyException
	{
		ArrayList<String> sampleTokens = new ArrayList<String>();
		sampleTokens.add("1	1..576;16024..16569	HH	249DEL 263G");
		
		SampleFile newSampleFile = new SampleFile(sampleTokens);
	
		Assert.assertEquals( "1",newSampleFile.getTestSamples().get(0).getSampleID());
		Assert.assertEquals( "1..576 ; 16024..16569 ;",newSampleFile.getTestSamples().get(0).getSampleRanges().toString());
		Assert.assertEquals( "H",newSampleFile.getTestSamples().get(0).getExpectedHaplogroup().toString());
		Assert.assertEquals( "249DEL 263G",newSampleFile.getTestSamples().get(0).getSample().toString());
		
	}
}
