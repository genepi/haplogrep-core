package haploClassifier.acceptanceTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jdom.JDOMException;
import org.junit.Test;

import genetools.Mutations;
import genetools.Polymorphism;
import genetools.exceptions.InvalidPolymorphismException;
import genetools.exceptions.InvalidFormatException;


public class QueryTestSamples extends GenericTest {
	
	@Test
	public void query10132860() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("10132860");
	}
	
	@Test
	public void query10533593() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("10533593");
	}
	
	@Test
	public void queryGRC10045985() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("GRC10045985");
	}
	@Test
	public void QueryBurma001_F1ac() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("GRC10045956");
	}
	
	@Test
	public void QueryGRC10045974() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("GRC10045974");
	}
	
	@Test
	public void queryBurma007() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma007");
	}
	
	@Test
	public void queryBurma009() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma009");
	}
	@Test
	public void queryBurma022() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma022");
	}
	
	@Test
	public void queryBurma018_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma018");
	}
	
	@Test
	public void queryBurma025_R9b1a2() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma025");
	}
	@Test
	public void QueryBurma029_R9b1a1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma029");
	}
	
	
	
	@Test
	public void QueryBurma037_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma037");
	}
	@Test
	public void QueryBurma042_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma042");
	}
	
	@Test
	public void QueryBurma044_M37a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma044");
	}
	@Test
	public void QueryBurma055_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma055");
	}
	
	@Test
	public void QueryBurma057_M7bd() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma057");
	}
	
	@Test
	public void QueryBurma067_D4b1a1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma067");
	}
	
	@Test
	public void QueryBurma080_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma080");
	}
	
	@Test
	public void QueryBurma081_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma081");
	}
	
	@Test
	public void QueryBurma099_F4b() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma099");
	}
	@Test
	public void queryBurma102_T2() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma102");
	}
	
	@Test
	public void QueryBurma105_D4g2a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma105");
	}
	
	@Test
	public void QueryBurma107_G2a1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma107");
	}
	
	@Test
	public void QueryBurma115_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma115");
	}
	
	@Test
	public void queryBurma142_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma142");
	}
	
	@Test
	public void queryBurma126_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma126");
	}
	@Test
	public void QueryBurma148_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma148");
	}
	@Test
	public void QueryBurma154_D4g1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma154");
	}
	
	@Test
	public void QueryBurma162_M36() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma162");
	}
	
	@Test
	public void QueryBurma164_D4g1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma164");
	}
	
	@Test
	public void QueryBurma187_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma187");
	}
	
	@Test
	public void QueryBurma168_N() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma168");
	}
	
	@Test
	public void QueryBurma165_M34a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma165");
	}
	
	@Test
	public void QueryBurma180_M7a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma180");
	}
	
	@Test
	public void QueryBurma190_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma190");
	}
	
	@Test
	public void QueryBurma203F() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma203");
	}
	
	@Test
	public void QueryBurma205_D4g1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma205");
	}
	
	@Test
	public void QueryBurma212_F() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma212");
	}
	
	@Test
	public void queryBurma218_U2b() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma218");
	}

	@Test
	public void queryBurma225() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma225");
	}
	
	@Test
	public void queryBurma228() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma228");
	}
	
	@Test
	public void QueryBurma244_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma244");
	}
	
	@Test
	public void queryBurma301_U2() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma301");
	}
	@Test
	public void queryBurma239_D() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma239");
	}
	@Test
	public void QueryBurma252_G2a1() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma252");
	}
	
	@Test
	public void queryBurma264_D4g2a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma264");
	}
	
	@Test
	public void queryBurma270_D4g2a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma270");
	}
	
	@Test
	public void queryBurma274_Z() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma274");
	}
	
	@Test
	public void queryBurma276_M7a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma276");
	}
	
	
	
	@Test
	public void queryBurma281() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma281");
	}
	

	@Test
	public void queryBurma301_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma301");
	}
	
	@Test
	public void queryBurma302_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma302");
	}
	
	//Schwierige Probe!
	@Test
	public void queryBurma305_M() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma305");
	}
	
	@Test
	public void QueryBurma318_R() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		//
		TestSampleDetailed("Burma318");
	}
	
	@Test
	public void QueryBurma323_D4e1a() throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException, InvalidFormatException
	{
		TestSampleDetailed("Burma323");
	}
}
