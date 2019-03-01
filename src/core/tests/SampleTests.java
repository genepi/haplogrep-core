/*package core.tests;

import org.junit.Assert;
import org.junit.Test;

import core.Sample;
import core.SampleRanges;
import exceptions.parse.sample.InvalidPolymorphismException;


public class SampleTests {
	@Test
	public void ParseSampleInsertionTest() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		
		Sample testSample = new Sample("249DEL 489C 573.1C 573.2C 573.3C 573.4A", range,0);
		
		Assert.assertEquals( 3,testSample.getPolymorphisms().size());
		Assert.assertEquals( "249DEL",testSample.getPolymorphisms().get(0).toString());
		Assert.assertEquals( "489C",testSample.getPolymorphisms().get(1).toString());
		Assert.assertEquals( "573.1CCCA",testSample.getPolymorphisms().get(2).toString());
	}

	@Test
	public void ParseLongInsertionTest() throws NumberFormatException, InvalidPolymorphismException
	{		
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("398.1T 398.2T 398.3T 398.4T 398.5T 398.6T 398.7T 398.8T 398.9T 398.10T 398.11C",range,0);
		
		Assert.assertEquals( 1,testSample.getPolymorphisms().size());
		Assert.assertEquals( "398.1TTTTTTTTTTC",testSample.getPolymorphisms().get(0).toString());
		
	}
	
	@Test
	public void ParseSampleInsertionUnorderedTest() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("249DEL 489C 573.2C 573.1C 573.4A 573.3C",range,0);
		
		Assert.assertEquals( 3,testSample.getPolymorphisms().size());
		Assert.assertEquals( "249DEL",testSample.getPolymorphisms().get(0).toString());
		Assert.assertEquals( "489C",testSample.getPolymorphisms().get(1).toString());
		Assert.assertEquals( "573.1CCCA",testSample.getPolymorphisms().get(2).toString());
	}
	
	@Test
	public void ParseSampleDeletationRangeTest() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("8281-8284d",range,0);
		
		Assert.assertEquals( 4,testSample.getPolymorphisms().size());
		Assert.assertEquals( "8281DEL",testSample.getPolymorphisms().get(0).toString());
		Assert.assertEquals( "8282DEL",testSample.getPolymorphisms().get(1).toString());
		Assert.assertEquals( "8283DEL",testSample.getPolymorphisms().get(2).toString());
		Assert.assertEquals( "8284DEL",testSample.getPolymorphisms().get(3).toString());
	}
	
	@Test
	public void ParseSampleHighMutableTest() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("309.1C 315.1C 16182C 16183C",range,0);
		
		Assert.assertEquals( 4,testSample.getPolymorphisms().size());
		Assert.assertEquals( "309.1C",testSample.getPolymorphisms().get(0).toString());
		Assert.assertEquals( "315.1C",testSample.getPolymorphisms().get(1).toString());
		Assert.assertEquals( "16182C",testSample.getPolymorphisms().get(2).toString());
		Assert.assertEquals( "16183C",testSample.getPolymorphisms().get(3).toString());
	}
	@Test
	public void ParseSampleInsTest() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("309.1CCC",range,0);
		
		Assert.assertEquals( 1,testSample.getPolymorphisms().size());
		Assert.assertEquals( "309.1CCC",testSample.getPolymorphisms().get(0).toString());
	}
	@Test
	public void ParseSampleInsTest455() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("455.2T",range,0);
		
		Assert.assertEquals( 1,testSample.getPolymorphisms().size());
		Assert.assertEquals( "455.1TT",testSample.getPolymorphisms().get(0).toString());
	}
	
	@Test
	public void ParseSample524() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("524.2C 524.1A",range,0);
		
		Assert.assertEquals( 1,testSample.getPolymorphisms().size());
		Assert.assertEquals( "524.1AC",testSample.getPolymorphisms().get(0).toString());
		//Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		//Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}
	
	@Test
	public void ParseSample5242() throws NumberFormatException, InvalidPolymorphismException
	{
		SampleRanges range = new SampleRanges();
		range.addCompleteRange();
		Sample testSample = new Sample("524.1AC",range,0);
		
		Assert.assertEquals( 1,testSample.getPolymorphisms().size());
		Assert.assertEquals( "524.1AC",testSample.getPolymorphisms().get(0).toString());
		//Assert.assertEquals( "489C",testSample.getPolymorphismn().get(1).toString());
		//Assert.assertEquals( "573.1CCCA",testSample.getPolymorphismn().get(2).toString());
	}
}
*/