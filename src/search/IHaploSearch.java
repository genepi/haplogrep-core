package search;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import core.TestSample;
import exceptions.parse.sample.InvalidPolymorphismException;

public interface IHaploSearch {

	public  List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException,
			InvalidPolymorphismException;

}