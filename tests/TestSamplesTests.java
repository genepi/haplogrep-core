import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;


public class TestSamplesTests {

	@Test
	public void testCreateFragments() throws InvalidPolymorphismException {
		
		ArrayList<Polymorphism> polys = new ArrayList<Polymorphism>();
		polys.add(new Polymorphism("236G"));
		polys.add(new Polymorphism("8880G"));
		polys.add(new Polymorphism("16100C"));
		polys.add(new Polymorphism("100C"));
		SampleRanges sampleRanges = new SampleRanges();
		sampleRanges.addCompleteRange();
		TestSample testSample = new TestSample("TestSample",polys,sampleRanges);
		
		SampleRanges fragmentRanges = new SampleRanges();
		fragmentRanges.addCustomRange(101, 2000);
		fragmentRanges.addCustomRange(2000, 16000);
		fragmentRanges.addCustomRange(16000, 100);
		ArrayList<TestSample> fragments = testSample.createFragments(fragmentRanges);
		
		Assert.assertNotNull(fragments);
		Assert.assertEquals(fragments.get(0).getSample().getPolymorphisms().get(0), new Polymorphism("236G"));
		Assert.assertEquals(fragments.get(1).getSample().getPolymorphisms().get(0), new Polymorphism("8880G"));
		Assert.assertEquals(fragments.get(2).getSample().getPolymorphisms().get(0), new Polymorphism("16100C"));
		Assert.assertEquals(fragments.get(2).getSample().getPolymorphisms().get(1), new Polymorphism("100C"));
		System.out.println();
		System.out.println(fragments);
		
	}
	
//	@Test
//	public void testCreateFragments2() throws InvalidPolymorphismException {
//		
//		ArrayList<Polymorphism> polys = new ArrayList<Polymorphism>();
//		polys.add(new Polymorphism("236G"));
//		polys.add(new Polymorphism("8880G"));
//		polys.add(new Polymorphism("16100C"));
//		polys.add(new Polymorphism("100C"));
//		SampleRanges sampleRanges = new SampleRanges();
//		sampleRanges.addCompleteRange();
//		TestSample testSample = new TestSample("TestSample",polys,sampleRanges);
//		
//		SampleRanges fragmentRanges  = new SampleRanges();
//		fragmentRanges.addCustomRange(2488, 10858);
//		fragmentRanges.addCustomRange(10898, 2687);
//		ArrayList<TestSample> fragments = testSample.createFragments(fragmentRanges);
//		
////		Assert.assertNotNull(fragments);
////		Assert.assertEquals(fragments.get(0).getSample().getPolymorphisms().get(0), new Polymorphism("236G"));
////		Assert.assertEquals(fragments.get(1).getSample().getPolymorphisms().get(0), new Polymorphism("8880G"));
////		Assert.assertEquals(fragments.get(2).getSample().getPolymorphisms().get(0), new Polymorphism("16100C"));
////		Assert.assertEquals(fragments.get(2).getSample().getPolymorphisms().get(1), new Polymorphism("100C"));
//		System.out.println();
//		System.out.println(fragments);
//		
//	}

	
}
