package search.ranking;

import java.util.ArrayList;
import java.util.Collections;

import phylotree.PhyloTree;
import search.SearchResultPerNode;
import search.results.ResultHamming;
import core.TestSample;

public class HammingRanker extends Ranker {
	
	
	public HammingRanker(){
		
	}
	
	
	public void setResults(PhyloTree phyloTree, TestSample sample, ArrayList<SearchResultPerNode> searchPhylotreeWrapper) {
		double sumWeights = sample.getSample().calcSumPhyloWeightsInRange(phyloTree) ;
		
		for(SearchResultPerNode currentResult : searchPhylotreeWrapper){
			results.add(new ResultHamming(currentResult, sumWeights,sample.getExpectedHaplogroup()));
		}
		
		
		Collections.sort(results);
	}


	
}
