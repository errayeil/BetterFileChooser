package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import javax.swing.Icon;
import java.io.File;

/**
 * @param text
 * @param icon
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class PCNodeObject implements INodeObject {

	/**
	 *
	 */
	private String text;

	/**
	 *
	 */
	private Icon icon;

	/**
	 * @param text
	 */
	public PCNodeObject (String text , Icon icon) {
		this.text = text;
		this.icon = icon;
	}

	/**
	 * @return
	 */
	@Override
	public String getText ( ) {
		return text;
	}

	/**
	 * @return
	 */
	@Override
	public Icon icon ( ) {
		return icon;
	}

	/**
	 * Returns this node objects file.
	 * In the case of PCNodeObject, this returns null.
	 *
	 * @return
	 */
	@Override
	public File getFile ( ) {
		return null;
	}

	@Override
	public void setNodeIsLoading ( boolean loading ) {

	}

	@Override
	public void setText ( String text ) {
		this.text = text;
	}

	@Override
	public void setIcon ( Icon icon ) {
		this.icon = icon;
	}

	/**
	 * Returns the node type.
	 *
	 * @return
	 */
	@Override
	public NodeType getNodeType ( ) {
		return NodeType.TOP_NODE;
	}

	/**
	 * Returns the text described in this NodeObject.
	 *
	 * @return
	 */
	@Override
	public String toString ( ) {
		return text;
	}
}
