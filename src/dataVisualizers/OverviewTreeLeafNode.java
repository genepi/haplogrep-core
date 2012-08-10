package dataVisualizers;

import java.util.ArrayList;

import search.SearchResultTreeNode;
import core.Polymorphism;
import core.TestSample;

public class OverviewTreeLeafNode extends TreeNode {
	private ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	private TestSample testSample = null;
	private ArrayList<Polymorphism> remainingPolys = new ArrayList<Polymorphism>();
	private ArrayList<Polymorphism> missingPolys = new ArrayList<Polymorphism>();
	
	
	public OverviewTreeLeafNode(TreeNode parent,TestSample sample,SearchResultTreeNode attachedPhylotreeNode){
		parent.addChild(this);
		this.parent = parent;
		
		phylotreeNode = attachedPhylotreeNode.getPhyloTreeNode();
		foundPolys.addAll(attachedPhylotreeNode.getFoundPolys());
		this.testSample = sample;
	}
	
	public void addDistinctFoundPolys(ArrayList<Polymorphism> foundPolysToAddDistinct) {
		for(Polymorphism currentPolyToAdd : foundPolysToAddDistinct){
			if(!foundPolys.contains(currentPolyToAdd))
				foundPolys.add(currentPolyToAdd);
		}
		
	}
	
	public ArrayList<Polymorphism> getFoundPolys() {
		return foundPolys;
	}

	public TestSample getTestSample() {
		return testSample;
	}

	public void updatePolys(boolean includeHotspots, boolean includeMissingPolys) {
		ArrayList<Polymorphism> foundPolysAllSamples = new ArrayList<Polymorphism>();
		
		OverviewTreeInnerNode c = (OverviewTreeInnerNode) parent;
		
		while(c != null){
			foundPolysAllSamples.addAll(c.getFoundPolys());
			c = (OverviewTreeInnerNode) c.getParent();
		}
		
		for(Polymorphism currentSamplePoly : testSample.getSample().getPolymorphismn()){
			if(!foundPolysAllSamples.contains(currentSamplePoly))
				if(includeHotspots || (!includeHotspots && !currentSamplePoly.isMTHotspot()))
				remainingPolys.add(currentSamplePoly);
		}
		
		for(Polymorphism currentSamplePoly : foundPolysAllSamples){
			if(!testSample.getSample().getPolymorphismn().contains(currentSamplePoly)){
				Polymorphism newBackmutation = new Polymorphism(currentSamplePoly);
				newBackmutation.setBackMutation(true);
				remainingPolys.add(newBackmutation);
			}
		}
		
		if(includeMissingPolys)
		for(Polymorphism currentExpectedPoly : testSample.getResults().get(0)
				.getSearchResult().getDetailedResult().getExpectedPolys()){
			if(!foundPolysAllSamples.contains(currentExpectedPoly))
				missingPolys.add(currentExpectedPoly);
		}
		
	}
	public ArrayList<Polymorphism> getRemainingPolys() {
		return remainingPolys;
	}
}
