package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.INodeObject;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BRTNode extends DefaultMutableTreeNode {

	/**
	 *
	 */
	private INodeObject node;

	/**
	 *
	 * @param node
	 */
	public BRTNode ( INodeObject node) {
		super (node);
		this.node = node;
	}

	/**
	 *
	 * @return
	 */
	public INodeObject getNodeObject () {
		return node;
	}
}
