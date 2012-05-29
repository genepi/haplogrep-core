package tests;

import exceptions.parse.sample.InvalidPolymorphismException;

import org.junit.Assert;
import org.junit.Test;

import core.Sample;


public class SampleTests {
	@Test
	public void ParseSampleInsertionTest() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("249DEL 489C 573.1C 573.2C 573.3C 573.4A",0);
		
		Assert.assertEquals( 3,testSample.getPolymorphismn().size());
		Assert.assertEquals( "249DEL",testSample.getPolymorphismn().get(0).toString());
		Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}

	@Test
	public void ParseLongInsertionTest() throws NumberFormatException, InvalidPolymorphismException
	{		
		Sample testSample = new Sample("398.1T 398.2T 398.3T 398.4T 398.5T 398.6T 398.7T 398.8T 398.9T 398.10T 398.11C",0);
		
		Assert.assertEquals( 1,testSample.getPolymorphismn().size());
		Assert.assertEquals( "398.1TTTTTTTTTTC",testSample.getPolymorphismn().get(0).toString());
		
	}
	
	@Test
	public void ParseSampleInsertionUnorderedTest() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("249DEL 489C 573.2C 573.1C 573.4A 573.3C",0);
		
		Assert.assertEquals( 3,testSample.getPolymorphismn().size());
		Assert.assertEquals( "249DEL",testSample.getPolymorphismn().get(0).toString());
		Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}
	
	@Test
	public void ParseSampleDeletationRangeTest() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("8281-8284d",0);
		
		Assert.assertEquals( 4,testSample.getPolymorphismn().size());
		Assert.assertEquals( "8281DEL",testSample.getPolymorphismn().get(0).toString());
		Assert.assertEquals( "8282DEL",testSample.getPolymorphismn().get(1).toString());
		Assert.assertEquals( "8283DEL",testSample.getPolymorphismn().get(2).toString());
		Assert.assertEquals( "8284DEL",testSample.getPolymorphismn().get(3).toString());
	}
	
	@Test
	public void ParseSampleHighMutableTest() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("309.1C 315.1C 16182C 16183C",0);
		
		Assert.assertEquals( 4,testSample.getPolymorphismn().size());
		Assert.assertEquals( "309.1C",testSample.getPolymorphismn().get(0).toString());
		Assert.assertEquals( "315.1C",testSample.getPolymorphismn().get(1).toString());
		Assert.assertEquals( "16182C",testSample.getPolymorphismn().get(2).toString());
		Assert.assertEquals( "16183C",testSample.getPolymorphismn().get(3).toString());
	}
	@Test
	public void ParseSampleInsTest() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("309.1CCC",0);
		
		Assert.assertEquals( 1,testSample.getPolymorphismn().size());
		Assert.assertEquals( "309.1CCC",testSample.getPolymorphismn().get(0).toString());
	}
	@Test
	public void ParseSampleInsTest455() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("455.2T",0);
		
		Assert.assertEquals( 1,testSample.getPolymorphismn().size());
		Assert.assertEquals( "455.1TT",testSample.getPolymorphismn().get(0).toString());
	}
	
	@Test
	public void ParseSample524() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("524.2C 524.1A",0);
		
		Assert.assertEquals( 1,testSample.getPolymorphismn().size());
		Assert.assertEquals( "524.1AC",testSample.getPolymorphismn().get(0).toString());
		//Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		//Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}
	
	@Test
	public void ParseSample5242() throws NumberFormatException, InvalidPolymorphismException
	{
		Sample testSample = new Sample("524.1AC",0);
		
		Assert.assertEquals( 1,testSample.getPolymorphismn().size());
		Assert.assertEquals( "524.1AC",testSample.getPolymorphismn().get(0).toString());
		//Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		//Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}
}
