package dataVisualizers;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import core.Polymorphism;
import core.TestSample;

import search.SearchResultTreeNode;

public class OverviewTree {
	private OverviewTreeInnerNode root;
	private ArrayList<OverviewTreeLeafNode> leafNodes = new ArrayList<OverviewTreeLeafNode>();

	
//	public OverviewTree(Element phyloTreePathXML, String SampleID,Element unusedPolys){
//		this.root = phyloTreePathXML;
//		
//		Element currentNode = root;
//		
//		while(currentNode.getChild("TreeNode") !=  null){
//			currentNode = currentNode.getChild("TreeNode");
//		}
//		
//		
//		Element newSampleIDElement =  new Element("TreeNode");
//		newSampleIDElement.setAttribute("type", "SampleIDNode");
//		newSampleIDElement.setAttribute("id",SampleID);
//		newSampleIDElement.addContent(unusedPolys);
//		
//		currentNode.addContent(newSampleIDElement);
//	}
	
	public OverviewTree(){
		
	}
	
	public void  addNewPath(TestSample testSample,ArrayList<SearchResultTreeNode> newPath){	
		int i = 0;
		if (root == null)
		{
			root = new OverviewTreeInnerNode(null,newPath.get(i));
			i++;
		}
		
		addNewPathNode(testSample,root,newPath,1);
	}
	
	public void addNewPathNode(TestSample currentSample,TreeNode currentTreeRootNode, ArrayList<SearchResultTreeNode> pathToAdd,int currentIteration){
		SearchResultTreeNode currentNodeToAdd = pathToAdd.get(currentIteration);
		
		boolean foundChild = false;
		for(TreeNode currentChildNode : currentTreeRootNode.getChildren()){
			if (currentChildNode instanceof OverviewTreeInnerNode &&  currentChildNode.getPhyloTreeNode().equals(pathToAdd.get(currentIteration).getPhyloTreeNode())) {
				((OverviewTreeInnerNode) currentChildNode).addDistinctFoundPolys(currentNodeToAdd.getExpectedPolys());  //sets Phylotree 
			
				if(currentIteration + 1 < pathToAdd.size())
					addNewPathNode(currentSample,currentChildNode,pathToAdd, currentIteration + 1);
				
				else{
					OverviewTreeLeafNode newTreeNode = new OverviewTreeLeafNode(currentChildNode,currentSample,pathToAdd.get(pathToAdd.size()-1)); 
//					currentTreeRootNode.addChild(newTreeNode);
					currentTreeRootNode = newTreeNode;
					leafNodes.add(newTreeNode);
					}
				foundChild = true;
				break;
			}
		}
		
		if(!foundChild){
			for(int i = currentIteration;i < pathToAdd.size();i++){
				OverviewTreeInnerNode newNode = new OverviewTreeInnerNode(currentTreeRootNode,pathToAdd.get(i));
//				currentTreeRootNode.addChild(newNode);
				newNode.addDistinctFoundPolys(pathToAdd.get(i).getFoundPolys());
				currentTreeRootNode = newNode;	
			}
			
			OverviewTreeLeafNode newTreeNode = new OverviewTreeLeafNode(currentTreeRootNode,currentSample,pathToAdd.get(pathToAdd.size()-1)); 
//			currentTreeRootNode.addChild(newTreeNode);
			currentTreeRootNode = newTreeNode;
			leafNodes.add(newTreeNode);
		}
		
		// The current result tree does NOT contain the current subpath. So we
		// add it to the tree
		// and are finished
//		if (currentTreeRootNode == null)
//		{
//			currentTreeRootNode = new OverviewTreeInnerNode(null,currentNodeToAdd); 
//			addNewPathNode(currentSample,currentTreeRootNode, pathToAdd,currentIteration+1);
//		}
////			|| currentTreeRootNode.getPhyloTreeNode().getSubHaplogroups().size() == 0) {
////			currentTreeRootNode.addChild(new OverviewTreeNode(currentNodeToAdd));
////			return true;
////		}

//		if (currentTreeRootNode.getPhyloTreeNode().equals(currentNodeToAdd)) {
//			if(currentIteration < pathToAdd.size()-1){
//				OverviewTreeInnerNode newTreeNode = new OverviewTreeInnerNode(currentTreeRootNode,currentNodeToAdd); 
//				newTreeNode.addDistinctFoundPolys(currentNodeToAdd.getFoundPolys());
//				currentTreeRootNode = newTreeNode;
//			}
//			else{
//				OverviewTreeLeafNode newTreeNode = new OverviewTreeLeafNode(currentTreeRootNode,currentSample,currentNodeToAdd); 
//				currentTreeRootNode = newTreeNode;
//				leafNodes.add(newTreeNode);
//				return true;
//			}
//			
//			ArrayList<Element> newPolys = new ArrayList<Element>();
//			for (Element currentPoly : (List<Element>) currentPathNode.getChildren("Poly")) {
//				boolean found = false;
//				for (Element currentPolyTree : (List<Element>) currentTreeRootNode.getChildren("Poly")) {
//					if (currentPoly.getText().equals(currentPolyTree.getText())) {
//						found = true;
//					}
//				}
//				if (!found) {
//					Element newPoly = new Element("Poly");
//					newPoly.setText(currentPoly.getText());
//					newPolys.add(newPoly);
//				}
//			}
		
//			for (Element c : newPolys)
//				currentTreeRootNode.addContent(c);
//
//			newPolys.clear();

			// Check if we are at the end of the subpath. If true then our
			// result tree already contains
			// the subpath completely and we leave the function immediately
//			if (currentPathNode.getChildren("TreeNode").size() == 0) {
//				return true;
//			}

			// boolean foundInsertPos = false;
			// The tree contains our current subpath so we step one node ahead
			// and make a RECURSIVE
			// call to this function for each child element
//			for (TreeNode currentTreeChild : currentTreeRootNode.getChildren()) {
//				if(pathToAdd.size() > currentIteration + 1){
//					if (addNewPathNode(currentSample,currentTreeChild, pathToAdd,currentIteration+1))
//						return true;
//				}
//				else return true;
//				
//				
//				for(int i = currentIteration;i < pathToAdd.size()-1;i++){
//					OverviewTreeInnerNode newNode = new OverviewTreeInnerNode(currentTreeRootNode,pathToAdd.get(i));
//					currentTreeRootNode.addChild(newNode);
//					currentTreeRootNode = newNode;				
//				}
//				
//				OverviewTreeLeafNode newTreeNode = new OverviewTreeLeafNode(currentTreeRootNode,currentSample,currentNodeToAdd); 
//				currentTreeRootNode = newTreeNode;
//				
//				leafNodes.add(newTreeNode);
//			}
//
////			currentPathNode.removeChildren("Poly");
////			currentTreeRootNode.addContent(currentPathNode.cloneContent());
//
//			return true;
//		}
//
//		else {
//			return false;
//		}
	}

	public void generateLeafNodes(boolean includeHotspots, boolean includeMissingPolys) {
		int i = 0;
		for(OverviewTreeLeafNode currentLeafNode : leafNodes){
			currentLeafNode.updatePolys(includeHotspots,includeMissingPolys);
			
		
		}
		
	}
	
	public OverviewTreeInnerNode getRootNode(){
		return root;
	}
	
	
}
