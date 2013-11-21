package dataVisualizers;

import java.util.ArrayList;

import search.SearchResultTreeNode;
import core.Polymorphism;

public class OverviewTreeInnerNode extends TreeNode {
	private ArrayList<Polymorphism> expectedPolys = new ArrayList<Polymorphism>();

	public OverviewTreeInnerNode(TreeNode parent,SearchResultTreeNode attachedPhylotreeNode){
		if(parent != null)
			parent.addChild(this);
		
		this.parent = parent;
		phylotreeNode = attachedPhylotreeNode.getPhyloTreeNode();
		expectedPolys.addAll(attachedPhylotreeNode.getExpectedPolys());
	}
	
	public void addDistinctFoundPolys(ArrayList<Polymorphism> foundPolysToAddDistinct) {
		for(Polymorphism currentPolyToAdd : foundPolysToAddDistinct){
			if(!expectedPolys.contains(currentPolyToAdd))
				expectedPolys.add(currentPolyToAdd);
		}
		
	}
	
	public ArrayList<Polymorphism> getExpectedPoly() {
		return expectedPolys;
	}

}
