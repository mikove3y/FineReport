package com.fr.design.gui.itree.refreshabletree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fr.design.utils.gui.GUICoreUtils;

/**
 * Enhanced, expand state and get treepath from node.
 */
public class ExpandMutableTreeNode extends DefaultMutableTreeNode {
    private boolean isExpanded = false; //the expend state
    
    //Ĭ����ʾ:�����ڼ��ء�������Ҫ��ʾtree��������Ӧuserobject
    public ExpandMutableTreeNode() {
    	this(RefreshableJTree.PENDING);
    }

    public ExpandMutableTreeNode(Object userObject) {
        this(userObject, false);
    }
    
    public ExpandMutableTreeNode(Object userObject, boolean isExpanded) {
    	super(userObject);
    	this.setExpanded(isExpanded);
    }
    public boolean isExpanded() {
        return this.isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    /**
     * չ������ExpandedΪtrue��TreeNode
     */
    public void expandCurrentTreeNode(JTree tree) {
    	if (this.isExpanded) {
        	tree.expandPath(GUICoreUtils.getTreePath(this));
            this.setExpanded(true);
            
            this.expandSubTreeNodes(tree);
    	}
    }

    /**
     * expand all children of TreeNode...
     * the Tree, that node at.
     */
    private void expandSubTreeNodes(JTree tree) {
        if (!this.isExpanded()) {
            return;
        }

        //peter:��չ.
        tree.expandPath(GUICoreUtils.getTreePath(this));
        this.setExpanded(true);

        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ExpandMutableTreeNode childNode = (ExpandMutableTreeNode) this.getChildAt(i);

            if (childNode.isExpanded()) {
                tree.expandPath(GUICoreUtils.getTreePath(childNode));
                childNode.setExpanded(true);

                childNode.expandSubTreeNodes(tree);
            }
        }
    }
    
    public void addChildTreeNodes(ExpandMutableTreeNode[] newChildNodes) {
    	for (int i = 0; i < newChildNodes.length; i ++) {
			this.add(newChildNodes[i]);
		}
    }
}