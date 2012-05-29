package search;

import exceptions.parse.sample.InvalidPolymorphismException;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import core.TestSample;

public interface IHaploSearch {

	public  List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException,
			InvalidPolymorphismException;

}