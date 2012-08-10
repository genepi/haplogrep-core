package dataVisualizers;

import java.util.ArrayList;

import search.SearchResultTreeNode;
import core.Polymorphism;

public class OverviewTreeInnerNode extends TreeNode {
	private ArrayList<Polymorphism> foundPolys = new ArrayList<Polymorphism>();

	public OverviewTreeInnerNode(TreeNode parent,SearchResultTreeNode attachedPhylotreeNode){
		if(parent != null)
			parent.addChild(this);
		
		this.parent = parent;
		phylotreeNode = attachedPhylotreeNode.getPhyloTreeNode();
		foundPolys.addAll(attachedPhylotreeNode.getFoundPolys());
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

}
