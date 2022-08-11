package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import javax.swing.Icon;
import java.io.File;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public final class PCNodeObject implements INodeObject {

	/**
	 *
	 */
	private final String text;

	/**
	 *
	 */
	private final Icon icon;

	/**
	 *
	 * @param text
	 */
	public PCNodeObject ( final String text , Icon icon) {
		this.text = text;
		this.icon = icon;
	}

	/**
	 *
	 * @return
	 */
	public String getText ( ) {
		return text;
	}

	/**
	 *
	 * @return
	 */
	public Icon getIcon ( ) {
		return icon;
	}

	/**
	 * Returns this node objects file.
	 * In the case of PCNodeObject, this returns null.
	 * @return
	 */
	@Override
	public File getFile ( ) {
		return null;
	}

	/**
	 * Returns the node type.
	 * @return
	 */
	@Override
	public NodeType getNodeType ( ) {
		return NodeType.TOP_NODE;
	}

	/**
	 * Returns the text described in this NodeObject.
	 * @return
	 */
	@Override
	public String toString ( ) {
		return text;
	}
}
