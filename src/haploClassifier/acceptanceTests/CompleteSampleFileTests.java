package haploClassifier.acceptanceTests;

import genetools.exceptions.InvalidPolymorphismException;
import genetools.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.Scanner;

import org.jdom.JDOMException;
import org.junit.Test;

public class CompleteSampleFileTests extends GenericTest {
	/*@Test
	public void ClassifyBigFile() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException, InvalidFormatException {
		this.runCompleteTestfile("evalHG_64000.hsd");
		Scanner in = new Scanner(System.in);
		in.nextInt();
	}*/

	@Test
	public void ClassifyBurmaTestSamples() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException, InvalidFormatException {
		this.runCompleteTestfile("alldata.hsd");
	}

	@Test
	public void ClassifyFullRangeSamples() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException, InvalidFormatException {
		//this.runCompleteTestfile("wir.hsd");
	}

	@Test
	public void ClassifyBurmaFinal() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException, InvalidFormatException {
	//	this.runCompleteTestfile("Burma_Final.hsd");
	}

	@Test
	public void ClasifyAllTestSamples() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException, InvalidFormatException {
	//	this.runCompleteTestfile("*.*");
	}

}
