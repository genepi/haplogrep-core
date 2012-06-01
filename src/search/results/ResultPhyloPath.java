package search.results;

import java.util.ArrayList;
import java.util.Collections;

import org.jdom.Element;

import phylotree.PhyloTree;

import search.PhyloTreeNode;
import core.Polymorphism;
import core.TestSample;

public class ResultPhyloPath {
	//TODO:Only needed for mutatins rates in XML code...try without?
	PhyloTree phylotree;
	ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> notFoundPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> remainingPolys = new ArrayList<Polymorphism>();
	ArrayList<Polymorphism> remainingPolysNotInRange = new ArrayList<Polymorphism>();
	
public ResultPhyloPath(TestSample testSample, Result result) {
		this.phylotree = result.phyloSearchData.getAttachedPhyloTreeNode().getTree();
		
		PhyloTreeNode currentPhyloTreeNode = result.getPhyloSearchData().getAttachedPhyloTreeNode();
		for (Polymorphism currentExpectedPoly : currentPhyloTreeNode.getExpectedPolys()) {		
			if (testSample.getSample().getSampleRanges().contains(currentExpectedPoly)) 
				if (currentExpectedPoly.isBackMutation()) {
					Polymorphism newPoly = new Polymorphism(currentExpectedPoly);
					newPoly.setBackMutation(false);
					
					foundPolys.remove(newPoly);
					notFoundPolys.remove(currentExpectedPoly);
				}

				
				if (testSample.getSample().contains(currentExpectedPoly)) {
					foundPolys.add(currentExpectedPoly);
				} else
					notFoundPolys.add(currentExpectedPoly);
			}

		//Find remaining polys in test sample
		for (Polymorphism currentSamplePoly : testSample.getSample().getPolymorphismn()) {		
			if (testSample.getSample().getSampleRanges().contains(currentSamplePoly)){
				if(!foundPolys.contains(currentSamplePoly) && !notFoundPolys.contains(currentSamplePoly))
						remainingPolys.add(currentSamplePoly);	
			}
			else
				remainingPolysNotInRange.add(currentSamplePoly);
	}
		
		Collections.sort(foundPolys);
		Collections.sort(notFoundPolys);
		Collections.sort(remainingPolys);
		Collections.sort(remainingPolysNotInRange);
	}
	



//TODO only use JSON for client communication.REFACTOR!
public Element toXML()
{
	
	Element results = new Element("DetailedResults");
	
	for(Polymorphism current : notFoundPolys)
	{
		
		
		//The polymorphism is contained in this haplogroup
		if(!foundPolys.contains(current))
		{
			Element result = new Element("DetailedResult");
			
			Element newExpectedPoly = new Element("expected");				
			newExpectedPoly.setText(current.toString());
			result.addContent(newExpectedPoly);
			
			Element newCorrectPoly = new Element("correct");				
			newCorrectPoly.setText("no");
			result.addContent(newCorrectPoly);
			
			results.addContent(result);
		}	
					
	}
	for(Polymorphism current : foundPolys)
	{
		
		//The polymorphism is  contained in this haplogroup
		if(foundPolys.contains(current))
		{
			Element result = new Element("DetailedResult");
			
			Element newExpectedPoly = new Element("expected");				
			newExpectedPoly.setText(current.toString());
			result.addContent(newExpectedPoly);
			
			Element newCorrectPoly = new Element("correct");				
			newCorrectPoly.setText("yes");
			result.addContent(newCorrectPoly);
			results.addContent(result);
		}	

		
	}
	
	
	
	return results;
}


public Element getUnusedPolysXML(boolean includeHotspots)
{
	Element results = new Element("DetailedResults");
	Collections.sort(remainingPolys);
	
	ArrayList<Polymorphism> expectedPolysSuperGroup = new ArrayList<Polymorphism>();
	
//	for(int i = 0; i < usedPath.size()-1;i++)
//	 expectedPolysSuperGroup.addAll(usedPath.get(i).getExpectedPolys());
//	
//	
//	ArrayList<Polymorphism> unusedPolysWithBackmutations = new ArrayList<Polymorphism>();
//	unusedPolysWithBackmutations.addAll(remainingPolys);
//	
//	for(Polymorphism currentPoly : expectedPolys){
//		if(!foundPolys.contains(currentPoly)){
//			if(expectedPolysSuperGroup.contains(currentPoly)){
//				Polymorphism p = new Polymorphism(currentPoly);
//				p.setBackMutation(true);
//				unusedPolysWithBackmutations.add(p);
//			}
//		}
//	}
	
	
	
		
	for (Polymorphism currentPoly : remainingPolys) {

		Element result = new Element("DetailedResult");
		Element newUnusedPoly = new Element("unused");
		newUnusedPoly.setText(currentPoly.toStringShortVersion());
		
		Element reasonUnusedPoly = new Element("reasonUnused");
		
		
		if(phylotree.getPhylogeneticWeight(currentPoly) == 0)
		{
			if(currentPoly.isBackMutation()){
				reasonUnusedPoly.setText("globalPrivateMutation");
				newUnusedPoly.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
				result.addContent(reasonUnusedPoly);
				result.addContent(newUnusedPoly);
				results.addContent(result);
			}
			
			else if(currentPoly.isMTHotspot()){
				
				if(includeHotspots){
				reasonUnusedPoly.setText("hotspot");
				result.addContent(reasonUnusedPoly);
				result.addContent(newUnusedPoly);
				results.addContent(result);
				}
			}
			
			else{
				reasonUnusedPoly.setText("globalPrivateMutation");
						
				result.addContent(reasonUnusedPoly);
				result.addContent(newUnusedPoly);
				results.addContent(result);
			}
			
		}
		
		else
		{
			if(this.remainingPolysNotInRange.contains(currentPoly))
				reasonUnusedPoly.setText("polyoutofrange");
			else
			reasonUnusedPoly.setText("localPrivateMutation");
			
			result.addContent(newUnusedPoly);
			result.addContent(reasonUnusedPoly);
			results.addContent(result);
		}
		
		
		
		
		
	}
	
	
	
	return results;

}
}
