package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.ui.tree.Abstract.FileNodeResource;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BRTNode extends DefaultMutableTreeNode {

	/**
	 * This node's resource wrapper, which contains the data
	 * displayed in the Tree.
	 */
	private FileNodeResource resource;

	/**
	 * Creates a new BRTNode with the specified resource.
	 * @param resource
	 */
	public BRTNode ( FileNodeResource resource) {
		super (resource);
		this.resource = resource;
	}

	/**
	 * Overridden to assign the provided user object to the resource
	 * value in this class, provided the userObject is an instance of
	 * INodeResource.
	 *
	 * @param userObject      the Object that constitutes this node's
	 *                          user-specified data
	 */
	@Override
	public void setUserObject ( Object userObject ) {
		super.setUserObject ( userObject );

		if ( userObject instanceof FileNodeResource ) {
			this.resource = ( FileNodeResource ) userObject;
		}
	}

	/**
	 * Returns this node resource.
	 * @return
	 */
	public FileNodeResource getNodeResource () {
		return resource;
	}
}
