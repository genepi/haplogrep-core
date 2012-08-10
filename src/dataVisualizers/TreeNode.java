package dataVisualizers;

import java.util.ArrayList;

import core.Polymorphism;
import core.TestSample;

import phylotree.PhyloTreeNode;

public class TreeNode {

	protected PhyloTreeNode phylotreeNode;
	protected TreeNode parent = null;
	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	
	public TreeNode() {
	}

	public PhyloTreeNode getPhyloTreeNode() {
		return phylotreeNode;
	}

	public void addChild(TreeNode newChild) {
		children.add(newChild);
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public TreeNode getParent() {
		return parent;
	}

}