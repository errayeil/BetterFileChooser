package com.github.errayeil.betterfilechooser.ui.tree.Abstract;

import com.github.errayeil.betterfilechooser.ui.tree.Root.BRTNodeType;

import javax.swing.Icon;
import java.io.File;

/**
 * An interface for the objects passed to RootTreeNode.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public interface FileNodeResource {

	/**
	 * <p>
	 * Returns the text assigned to this NodeObject.
	 * This is utilized by the BRTCellRenderer.
	 * </p>
	 *
	 * @apiNote This should never be null as the text will
	 * be displayed in the Cell component of BRTCellRenderer.
	 */
	String getText ( );

	/**
	 * <p>
	 * Returns the icon assigned to this NodeObject.
	 * This is utilized by the BRTCellRenderer.
	 * </p>
	 *
	 * @apiNote This can be null. You should test for null when using the result.
	 * This also depends on the subclass implementation.
	 */
	Icon getIcon ( );

	/**
	 * <p>
	 * Returns the file assigned to this NodeObject.
	 * This is utilized by the BRTCellRenderer.
	 * </p>
	 *
	 * @apiNote This can be null. You should test for null when using the result.
	 * This also depends on the subclass implementation.
	 */
	File getFile ( );


	/**
	 * @param text
	 */
	void setText ( String text );

	/**
	 *
	 * @param icon
	 */
	void setIcon ( Icon icon );

	/**
	 * Returns the NodeType.
	 *
	 * @see BRTNodeType
	 */
	String getNodeType ( );
}
