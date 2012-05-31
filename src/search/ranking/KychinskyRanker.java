package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import phylotree.PhyloTree;
import search.SearchResultPerNode;
import search.results.ResultKylcinski;
import core.TestSample;

public class KychinskyRanker extends Ranker {
	ArrayList<ResultKylcinski> results = new ArrayList<ResultKylcinski>();
	
	public KychinskyRanker(){
		
	}
	

	public void setResults(PhyloTree phyloTree, TestSample sample, ArrayList<SearchResultPerNode> searchPhylotreeWrapper) {
		double sumWeights = sample.getSample().calcSumPhyloWeightsInRange(phyloTree);
		
		for(SearchResultPerNode currentResult : searchPhylotreeWrapper){
			results.add(new ResultKylcinski(currentResult, sumWeights,sample.getExpectedHaplogroup()));
		}
		
		Collections.sort(results);
	}
}
