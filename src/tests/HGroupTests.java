package tests;

import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import core.TestSample;

import search.ClusteredSearchResult;
import search.HaploSearchHamming;
import search.HaploSearchManager;
import search.IHaploSearch;

public class HGroupTests {

	private static IHaploSearch newSearch = null;

	@BeforeClass
	public static void Init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException
	{
		
		HaploSearchManager h1 = new HaploSearchManager("phylotree11.xml","fluctRates11.txt");
		newSearch = new HaploSearchHamming(h1);
		
	}
	
	@Test
	public void TestH2a2() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	H2a2	263G 8860G 15326G"));
		
		Assert.assertEquals("H2a2", result.get(0).getHaplogroup());
	}
	
	@Test
	public void TestH2a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	H2a2	263G 8860G 15326G 750G"));
		
		Assert.assertEquals("H2a", result.get(0).getHaplogroup());
	}


	
	@Test
	public void TestH() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G"));
		
		Assert.assertEquals("H", result.get(0).getHaplogroup());
	}
	
	@Test
	public void TestHV() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G 2706G 7028T"));
		
		Assert.assertEquals("HV", result.get(0).getHaplogroup());
	}
	
	@Test
	public void TestR0() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G 2706G 7028T 14766T"));
		
		Assert.assertEquals("R0", result.get(0).getHaplogroup());
	}
	@Test
	public void TestL5a1a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	73G	152C	182T	189G	195C	247A	263G	315.1C	455.1T	455.2T	455.3C	522del	523del	709A	750G	769A	825A	851G	930A	1018A	1438G	1822C	2706G	3423C	3594T	4104G	4496T	4769G	5004C	5111T	5147A	5656G	6182A	6297C	7028T	256T	7424G	7521A	7873T	7972G	8155A	8188G	8582T	8655T	8701G	8754T	8860G	9305A	9329A	9540C	9899C	10398G	10688A	10810C	10873C	11015G	11025C	11719A	11881T	12236A	12432T	12705T	12950G	13105G	13506T	13650T	13722G	14212C	14239T	14581C	14766T	14905A	14971C	15217A	15326G	15884A	16129A	16148T	16166G	16187T	16189C	16223T	16278T	16311C	16355T	16362C"));
		
		Assert.assertEquals("L5a1a", result.get(0).getHaplogroup());
	}
	@Test
	public void TestH13a2a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("1	1-16569	H13a2a	263G 309.1C 315.1C 709A 750G 1008G 1438G 1768A 2259T 4769G 8860G 14872T 15326G 16519C"));

		Assert.assertEquals("H13a2a", result.get(0).getHaplogroup());
	}
	@Test
	public void TestL3e1c() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("549	1-16569	L3e1c	73G	150T	189G	200G	263G	309.1C	315.1C	750G	1438G	2352C	2706G	3106N	3675G	4769G	5460A	6221C	6587T	7028T	8289.1C	8289.2C	8289.3C	8289.4C	8289.5C	8289.6T	8289.7C	8289.8T	8289.9A	8289.10C	8289.11C	8289.12C	8289.13C	8289.14C	8289.15T	8289.16C	8289.17T	8289.18A	8860G	9540C	10398G	10819G	10873C	11719A	12705T	14152G	14212C	14323A	14766T	15301A	15326G	15670C	15942C	16327T "));
		

		
		Assert.assertEquals("L3e1c", result.get(0).getHaplogroup());
	}
	@Test
	public void TestH13a2b1() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("549	1-16569	H13a2b1	263G  315.1C  709A  750G  1438G  2259T  4639C  4769G  5899.1C  7322G  8860G  13762G  14872T  15001C  15326G  16311C  16519C "));
		

		
		Assert.assertEquals("H13a2b1", result.get(0).getHaplogroup());
	}
	
	


	@Test
	public void TestH2a2b() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<ClusteredSearchResult> result =  newSearch.search(TestSample.parse("549	1-16569	H2a2b	263G	309.1C	309.2T	4080C	8860G	15326G	16291T"));
		

		
		Assert.assertEquals("H2a2b", result.get(0).getHaplogroup());
	}
	

}
