package search;

import java.util.ArrayList;

import core.Sample;

public class ResultKylcinski extends Result implements Comparable<ResultKylcinski>{

	public ResultKylcinski(Sample sample, ArrayList<SearchResultPerNode> phyloSearchData) {
		super(sample, phyloSearchData);
	}

	@Override
	public int compareTo(ResultKylcinski o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
