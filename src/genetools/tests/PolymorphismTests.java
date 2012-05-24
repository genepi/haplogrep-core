package genetools.tests;

import exceptions.parse.sample.InvalidPolymorphismException;
import genetools.Mutations;
import genetools.Polymorphism;

import org.junit.Assert;
import org.junit.Test;


public class PolymorphismTests {
	@Test
	public void ParseInsertionTest() throws  NumberFormatException, InvalidPolymorphismException
	{
		Polymorphism poly = new Polymorphism("573.1CCC");
		Assert.assertNotNull(poly);
		/*Assert.assertEquals( "573.1C",polys.get(0).toString());
		Assert.assertEquals( "573.2C",polys.get(1).toString());
		Assert.assertEquals( "573.3C",polys.get(2).toString());
		Assert.assertEquals( "573.4C",polys.get(3).toString());*/
		Assert.assertEquals( "573.1CCC",poly.toString());
	}
	
	@Test
	public void ParseInsertionTest56() throws  NumberFormatException, InvalidPolymorphismException
	{
		Polymorphism poly = new Polymorphism("573.1CCCC");
		Assert.assertNotNull(poly);
		Assert.assertEquals( "573.1CCCC",poly.toString());
	}
	
	
	@Test
	public void ParseInsertionTest2() throws NumberFormatException, InvalidPolymorphismException
	{
		Polymorphism polys = new Polymorphism("573.1C");
		Assert.assertEquals( "573.1C",polys.toString());
	}
	
	@Test
	public void ConvertPolyInsert() throws NumberFormatException, InvalidPolymorphismException
	{
		Polymorphism result = new Polymorphism("2232.1A");
		
		
			Assert.assertNotNull(result);
			Assert.assertEquals(result.getPosition(), 2232);
			Assert.assertEquals(result.getInsertedPolys(), "A");
			Assert.assertEquals(result.getMutation(), Mutations.INS);
	}
	@Test
	public void ConvertPolyBackMutation() throws  NumberFormatException, InvalidPolymorphismException
	{
		/*ArrayList<*/Polymorphism result = new Polymorphism("2706!");
		
		
			Assert.assertNotNull(result);
			Assert.assertEquals(result.getPosition(), 2706);
			Assert.assertTrue( result.isBackMutation());
	}
	
	@Test
	public void TestEquals() throws  NumberFormatException, InvalidPolymorphismException
	{
		/*ArrayList<*/Polymorphism result = new Polymorphism("2706");
		
		
			//Assert.assertNotNull(result);
			//Assert.assertEquals(result.getPosition(), 2706);
			Assert.assertFalse(result.equals(new Polymorphism("2706!")));
	}
	
	@Test
	public void ConvertPolyDel() throws  NumberFormatException, InvalidPolymorphismException
	{
		Polymorphism result = new Polymorphism("5752del");
		
		
			Assert.assertNotNull(result);
			Assert.assertEquals(result.getPosition(), 5752);
			Assert.assertEquals(result.getMutation(), Mutations.DEL);
	}
	
	
	@Test(expected=InvalidPolymorphismException.class)
	public void CheckMalformedPolyException() throws InvalidPolymorphismException
	{
		new Polymorphism("1X");
	}
	
	@Test(expected=InvalidPolymorphismException.class)
	public void CheckMalformedInsertPolyException() throws InvalidPolymorphismException
	{
		new Polymorphism("1.1X");
	}
	
	@Test(expected=NumberFormatException.class)
	public void CheckMalformedPolys() throws InvalidPolymorphismException
	{
		new Polymorphism("215T316A");
	}
	
}
