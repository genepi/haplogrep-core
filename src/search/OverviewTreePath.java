package search;

import org.jdom.Element;

public class OverviewTreePath {
	private Element root;
	
	public OverviewTreePath(Element phyloTreePathXML, String SampleID,Element unusedPolys){
		this.root = phyloTreePathXML;
		
		Element currentNode = root;
		
		while(currentNode.getChild("TreeNode") !=  null){
			currentNode = currentNode.getChild("TreeNode");
		}
		
		
		Element newSampleIDElement =  new Element("TreeNode");
		newSampleIDElement.setAttribute("type", "SampleIDNode");
		newSampleIDElement.setAttribute("id",SampleID);
		newSampleIDElement.addContent(unusedPolys);
		
		currentNode.addContent(newSampleIDElement);
	}
	
	public Element toXML(){
		return root;
	}
}
