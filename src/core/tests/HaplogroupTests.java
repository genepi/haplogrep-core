package core.tests;

import junit.framework.Assert;

import org.junit.Test;

import phylotree.Phylotree2;
import phylotree.PhylotreeManager;

import core.Haplogroup;


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
		Phylotree2 tree = PhylotreeManager.getInstance().getPhylotree("phylotree14.xml","fluctRates14.txt");
		Haplogroup h =  new Haplogroup("C1c4");
		Assert.assertTrue("No super group", h.isSuperHaplogroup(tree,new Haplogroup("H2")));
		
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
