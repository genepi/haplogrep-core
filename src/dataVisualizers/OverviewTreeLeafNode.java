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

	public void updatePolys(boolean includeHotspots) {
		ArrayList<Polymorphism> foundPolysAllSamples = new ArrayList<Polymorphism>();
		
		OverviewTreeInnerNode c = (OverviewTreeInnerNode) parent;
		
	/*	while(c != null){
			foundPolysAllSamples.addAll(c.getExpectedPoly());
			c = (OverviewTreeInnerNode) c.getParent();
		} */
		for(Polymorphism currentRemaining : testSample.getResults().get(0).getSearchResult().getDetailedResult().getRemainingPolysInSample()){
			if(includeHotspots || (!includeHotspots && !currentRemaining.isMTHotspot()))
				remainingPolys.add(currentRemaining);
		}
		
//		for(Polymorphism currentRemaining : testSample.getResults().get(0).getSearchResult().getDetailedResult().){
//			if(includeHotspots || (!includeHotspots && !currentRemaining.isMTHotspot()))
//				remainingPolys.add(currentRemaining);
//		}
//		
		
//		testSample.getResults().get(0).getSearchResult().getDetailedResult().getFoundNotFoundPolys()
		
		for(Polymorphism currentSamplePoly : testSample.getResults().get(0).getSearchResult().getDetailedResult().getExpectedPolys()){
			if(!currentSamplePoly.isBackMutation() && !testSample.getSample().getPolymorphisms().contains(currentSamplePoly)){
//				if(includeHotspots || (!includeHotspots && !currentSamplePoly.isMTHotspot()))
//			remainingPolys.add(currentSamplePoly);
			Polymorphism newBackmutation = new Polymorphism(currentSamplePoly);
			
//			System.out.println(currentSamplePoly.getPosition());
			newBackmutation.setBackMutation(true);
//			if( !testSample.getResults().get(0).getSearchResult().getDetailedResult().getExpectedPolys().contains(newBackmutation))
			remainingPolys.add(newBackmutation);
		}
		}
		
/*		for(Polymorphism currentSamplePoly : foundPolysAllSamples){
			if(!currentSamplePoly.isBackMutation() && !testSample.getSample().getPolymorphisms().contains(currentSamplePoly)){
				Polymorphism newBackmutation = new Polymorphism(currentSamplePoly);
				
//				System.out.println(currentSamplePoly.getPosition());
				newBackmutation.setBackMutation(true);
				if( !testSample.getResults().get(0).getSearchResult().getDetailedResult().getExpectedPolys().contains(newBackmutation))
				remainingPolys.add(newBackmutation);
			}
		}
		
//		if(includeMissingPolys)
		for(Polymorphism currentExpectedPoly : testSample.getResults().get(0)
				.getSearchResult().getDetailedResult().getExpectedPolys()){
			if(!foundPolysAllSamples.contains(currentExpectedPoly)){
				Polymorphism newBackmutation = new Polymorphism(currentExpectedPoly);
				newBackmutation.setBackMutation(true);
				remainingPolys.add(newBackmutation);
			}
		}*/
		
	}
	public ArrayList<Polymorphism> getRemainingPolys() {
		return remainingPolys;
	}
	
	public ArrayList<Polymorphism> getMissingPolys() {
		return missingPolys;
	}
}
