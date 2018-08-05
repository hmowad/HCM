package com.code.ui.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;

public abstract class TreeNodeImpl implements TreeNode, Serializable {
    private Long id;
    private Long parentId;
    private TreeNodeImpl parent;
    private List<TreeNodeImpl> children;
    private boolean visited;
    private boolean leaf;
    private boolean expanded;

    public TreeNodeImpl() {
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Long getParentId() {
	return parentId;
    }

    public void setParentId(Long parentId) {
	this.parentId = parentId;
    }

    public boolean isVisited() {
	return visited;
    }

    public void setVisited(boolean visited) {
	this.visited = visited;
    }

    public boolean isLeaf() {
	return leaf;
    }

    public void setLeaf(boolean leaf) {
	this.leaf = leaf;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
	if(children == null)
	    return null;
	return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
	if (children == null)
	    return 0;
	return children.size();
    }

    @Override
    public TreeNodeImpl getParent() {
	return parent;
    }

    public void setParent(TreeNodeImpl parent) {
	this.parent = parent;
    }

    @Override
    public int getIndex(TreeNode node) {
	return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
	return leaf;
    }

    @Override
    public Enumeration<TreeNodeImpl> children() {
	if (children == null) {
	    children = new ArrayList<TreeNodeImpl>();
	}
	return Collections.enumeration(children);
    }

    public void addChild(TreeNodeImpl child) {
	leaf = false;
	if (children == null) {
	    children = new ArrayList<TreeNodeImpl>();
	}
	if (visited) {
	    for (int i = 0; i < children.size(); i++) {
		TreeNodeImpl c = children.get(i);
		if (c.getId().equals(child.getId())) {
		    children.remove(i);
		    children.add(i, child);
		    return;
		}
	    }
	    children.add(child);
	}
    }

    public void removeChild(TreeNodeImpl child) {
	// children.remove(child);
	for (Iterator<TreeNodeImpl> iterator = children.iterator(); iterator.hasNext();) {
	    TreeNodeImpl node = iterator.next();
	    if (node.getId().equals(child.getId())) {
		iterator.remove();
		break;
	    }
	}
	if (children.isEmpty())
	    leaf = true;
    }

    public TreeNodeImpl getChild(Integer index) {
	return children.get(index);
    }

    public List<TreeNodeImpl> getChildrenList() {
	if (children == null) {
	    children = new ArrayList<TreeNodeImpl>();
	}
	return children;
    }

    public boolean isExpanded() {
	return expanded;
    }

    public void setExpanded(boolean expanded) {
	this.expanded = expanded;
    }
}
