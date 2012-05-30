package search;


import java.util.ArrayList;
import java.util.Collections;

import org.jdom.Element;

import core.Polymorphism;

public class SearchResultPath {
	private ArrayList<PhyloTreeNodeSearchResult> path = new ArrayList<PhyloTreeNodeSearchResult>();
	
	public SearchResultPath() {
	}
	
	public SearchResultPath(SearchResultPath usedPath) {	
		path.addAll(usedPath.getNodes());
	}
	
	public ArrayList<PhyloTreeNodeSearchResult> getNodes()
	{
		return path;
	}

	public Element toXML(boolean includeMissingPolys)	{
		if(path.size() == 0)
			return null;
		
		Element root =null;
		Element currentEndNode = null;
		
		for(PhyloTreeNodeSearchResult currentNode : path){
			if(root == null){
				currentEndNode = root = new Element("TreeNode");
				root.setAttribute("name", currentNode.getHaplogroup().toString());
				root.setAttribute("type", "Haplogroup");
				//root.addContent(currentNode.getHaplogroup().toString());
				}
			else{
				Element newChildElement =  new Element("TreeNode");
				newChildElement.setAttribute("name", currentNode.getHaplogroup().toString());
				newChildElement.setAttribute("type", "Haplogroup");
				//newChildElement.setText(currentNode.getHaplogroup().toString());
				currentEndNode.addContent(newChildElement);
				currentEndNode = newChildElement;
				
			}
			
			Collections.sort(currentNode.getExpectedPolys());
			
			
//			for(Polymorphism currentPoly :currentNode.getFoundPolys()){
//				Element newChildElement =  new Element("Poly");
//				newChildElement.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
//				currentEndNode.addContent(newChildElement);
//			}
//			
			for(Polymorphism currentPoly :currentNode.getExpectedPolys()){
				if(!currentNode.getFoundPolys().contains(currentPoly)){
					if(includeMissingPolys){
					Element newChildElement =  new Element("Poly");
					newChildElement.setText("mis" + currentPoly.toStringShortVersion());
					currentEndNode.addContent(newChildElement);
					}
				}
				else{
					Element newChildElement =  new Element("Poly");
					newChildElement.setText(Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
					currentEndNode.addContent(newChildElement);
				}
			}
			
			
			
			}
		
		
		
		
		return root;
		
	}
	public void add(PhyloTreeNodeSearchResult newNode) {
		path.add(newNode);		
	}

	
	
}
