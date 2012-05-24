package search;

import exceptions.parse.sample.InvalidPolymorphismException;
import genetools.TestSample;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

public interface IHaploSearch {

	public  List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException,
			InvalidPolymorphismException;

}