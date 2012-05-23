package haploClassification;

import genetools.TestSample;
import genetools.exceptions.InvalidFormatException;
import genetools.exceptions.InvalidPolymorphismException;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

public interface IHaploSearch {

	public  List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException,
			InvalidPolymorphismException, InvalidFormatException;

}