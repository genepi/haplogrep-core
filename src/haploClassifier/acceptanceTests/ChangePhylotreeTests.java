package haploClassifier.acceptanceTests;

import junit.framework.Assert;
import genetools.Haplogroup;
import genetools.Polymorphism;
import genetools.exceptions.InvalidFormatException;
import genetools.exceptions.InvalidPolymorphismException;
import haploClassification.HaploSearchManager;
import haploClassification.exceptions.PolyDoesNotExistException;

import org.jdom.JDOMException;
import org.junit.Test;




public class ChangePhylotreeTests {
	HaploSearchManager searchManager = new HaploSearchManager("phylotree11.xml","fluctRates11.txt");
	
	@Test
	public void changePolyTest() throws NumberFormatException, InvalidFormatException, JDOMException, InvalidPolymorphismException, PolyDoesNotExistException
	{
	
		Assert.assertEquals("263G", searchManager.getPolysOfHg(new Haplogroup("H2a2")).get(2).getText());
		//HaploSearchManager h1 = new HaploSearchManager(phylotree);.getPolysOfHg(new Haplogroup("H2a2")).get(0).getText());
		Assert.assertEquals("8860G", searchManager.getPolysOfHg(new Haplogroup("H2a2")).get(1).getText());
		Assert.assertEquals("1A", searchManager.getPolysOfHg(new Haplogroup("H2a2")).get(2).getText());
	}
}
