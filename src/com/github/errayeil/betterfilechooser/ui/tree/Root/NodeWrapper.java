package com.github.errayeil.betterfilechooser.ui.tree.Root;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class NodeWrapper {

	/**
	 *
	 */
	private final BRTNode child;

	/**
	 *
	 */
	private final BRTNode parent;

	/**
	 *
	 */
	private int index;

	/**
	 * @param child
	 * @param parent
	 */
	public NodeWrapper ( BRTNode child , BRTNode parent ) {
		this.child = child;
		this.parent = parent;
	}

	/**
	 *
	 * @param index
	 */
	public void setIndex ( int index ) {
		this.index = index;
	}

	/**
	 *
	 * @return
	 */
	public BRTNode getChild ( ) {
		return child;
	}

	/**
	 * @return
	 */
	public BRTNode getParent ( ) {
		return parent;
	}

	/**
	 * @return
	 */
	public int getIndex ( ) {
		return index;
	}
}
