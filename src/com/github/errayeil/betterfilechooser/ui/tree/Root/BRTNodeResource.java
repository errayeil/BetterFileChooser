package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.ui.tree.Abstract.FileNodeResource;

import javax.swing.Icon;
import java.io.File;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BRTNodeResource implements FileNodeResource {

	/**
	 * The text that is displayed in the tree.
	 */
	private String nodeText;

	/**
	 * The icon that is displayed in the Tree.
	 */
	private Icon nodeIcon;

	/**
	 * The file the node represents, if a file is used.
	 * This will be null on the Root node.
	 */
	private File nodeFile;

	/**
	 * What type of node this is.
	 */
	private final BRTNodeType nodeType;

	/**
	 *
	 * @param text
	 * @param icon
	 */
	public BRTNodeResource ( String text , Icon icon, BRTNodeType type) {
		this ( null , text , icon , type );
	}

	/**
	 *
	 * @param file
	 * @param text
	 * @param icon
	 * @param type
	 */
	public BRTNodeResource ( File file , String text , Icon icon , BRTNodeType type ) {
		this.nodeFile = file;
		this.nodeText = text;
		this.nodeIcon = icon;
		this.nodeType = type;
	}

	/**
	 * Returns the text that is used for the BRTNode.
	 * @return
	 */
	@Override
	public String getText ( ) {
		return nodeText;
	}

	/**
	 * Returns the icon that is used for the BRTNode.
	 *
	 * @return
	 */
	@Override
	public Icon getIcon ( ) {
		return nodeIcon;
	}

	/**
	 * Returns the file that the node represents.
	 *
	 * @apiNote This can be null. You should test for null if you do not test for NodeType.
	 * @return
	 */
	@Override
	public File getFile ( ) {
		return nodeFile;
	}

	/**
	 * Returns this nodes type.
	 *
	 * @see BRTNodeType
	 * @return
	 */
	@Override
	public String getNodeType ( ) {
		return nodeType.getNodeType ( nodeType);
	}

	/**
	 * Sets the text of this resource.
	 * @apiNote This will not cause the model of the Tree to update the node. You must call model.nodeChanged ( myNode )
	 * @param text
	 */
	@Override
	public void setText ( String text ) {
		this.nodeText = text;
	}

	/**
	 * Sets the icon of this resource.
	 *
	 * @apiNote This will not cause the model of the Tree to update the node. You must call model.nodeChanged ( myNode )
	 * @param icon
	 */
	@Override
	public void setIcon ( Icon icon ) {
		this.nodeIcon = icon;
	}
}
