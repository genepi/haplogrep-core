package genetools.tests;

import junit.framework.Assert;
import genetools.Haplogroup;

import org.junit.Test;


public class HaplogroupTests {
	
	@Test
	public void TestConstructor()
	{
		Haplogroup h =  new Haplogroup("M16a");
		Assert.assertEquals(h.toString(), "M16a");
		
	}
	
	@Test
	public void TestShortForm()
	{
		Haplogroup h =  new Haplogroup("H1bce");
		Assert.assertEquals(h.toString(), "H1b'c'e");
		
	}
	
	@Test
	public void TestShortForm2()
	{
		Haplogroup h =  new Haplogroup("H1b'c'e");
		Assert.assertEquals(h.toString(), "H1b'c'e");
		
	}
	
	@Test
	public void TestUnnamedBranchForm()
	{
		Haplogroup h =  new Haplogroup("H1**152");
		Assert.assertEquals(h.toString(), "H1**152");
		
	}
	@Test
	public void IsSuperhaplogroupTest()
	{
		Haplogroup h =  new Haplogroup("M16");
		Assert.assertTrue( h.isSuperHaplogroup(new Haplogroup("M16a")));
		
	}
	
	@Test
	public void TestHVGroup()
	{
		Haplogroup h =  new Haplogroup("HV3");
		Assert.assertEquals(h.toString(), "HV3");
		
	}
	
	@Test
	public void TestCZGroup()
	{
		Haplogroup h =  new Haplogroup("CZ");
		Assert.assertEquals(h.toString(), "CZ");
	}
	
}
