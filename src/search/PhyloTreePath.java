package search;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jdom.Element;

import core.Polymorphism;

public class PhyloTreePath implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8785488762712398581L;
	private ArrayList<SearchResultTreeNode> path = new ArrayList<SearchResultTreeNode>();
	
	public PhyloTreePath(PhyloTreePath usedPath) {
		
		path.addAll(usedPath.getNodes());
	}
	
	public PhyloTreePath() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<SearchResultTreeNode> getNodes()
	{
		return path;
	}

	public Element toXML(boolean includeMissingPolys)	{
		if(path.size() == 0)
			return null;
		
		Element root =null;
		Element currentEndNode = null;
		
		for(SearchResultTreeNode currentNode : path){
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
	public void add(SearchResultTreeNode newNode) {
		
		path.add(newNode);		
	}

	public boolean equals(Object other){
		if(!(other instanceof PhyloTreePath))
			return false;
		Collections.reverse(path);
		if(!Arrays.equals(path.toArray(), ((PhyloTreePath)other).path.toArray()))
			return false;	
		Collections.reverse(path);	
		return true;
	}
	
}
