package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import com.github.errayeil.betterfilechooser.Utils.BetterFileUtils;

import javax.swing.Icon;
import java.io.File;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public final class DriveNodeObject implements INodeObject {

	/**
	 *
	 */
	private final File driveFile;

	/**
	 *
	 */
	private final String driveText;

	/**
	 *
	 */
	private final Icon driveIcon;

	/**
	 *
	 * @param driveFile
	 * @param driveText
	 */
	public DriveNodeObject ( File driveFile , String driveText ) {
		this ( driveFile , driveText, BetterFileUtils.getSystemFileIcon ( driveFile ));
	}

	/**
	 *
	 * @param driveFile
	 * @param driveText
	 * @param icon
	 */
	public DriveNodeObject ( File driveFile , String driveText, Icon icon) {
		this.driveFile = driveFile;
		this.driveText = driveText;
		this.driveIcon = icon;
	}

	/**
	 * Returns the text assigned for this node object.
	 * This is utilized by the BRTCellRenderer.
	 * @return
	 */
	@Override
	public String getText ( ) {
		return driveText;
	}

	/**
	 * Returns the icon assigned for this node object.
	 * This is utilized by the BRTCellRenderer.
	 * @return
	 */
	@Override
	public Icon getIcon ( ) {
		return driveIcon;
	}

	/**
	 * Returns the icon assigned for this node object.
	 * This is utilized by the BRTCellRenderer.
	 * @return
	 */
	@Override
	public File getFile ( ) {
		return driveFile;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public NodeType getNodeType ( ) {
		return NodeType.DRIVE_NODE;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString ( ) {
		return driveText;
	}
}
