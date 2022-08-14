package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import com.github.errayeil.betterfilechooser.Utils.BetterFileUtils;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.io.File;

import static com.github.errayeil.betterfilechooser.Utils.Resources.getLoadingIcon;

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
	private String driveText;

	/**
	 *
	 */
	private Icon displayedIcon;

	/**
	 *
	 */
	private Icon retrievedIcon;

	/**
	 *
	 */
	private final ImageIcon loadingIcon;

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
		this.retrievedIcon = icon;
		this.loadingIcon = getLoadingIcon();
		displayedIcon = icon;
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
	public Icon icon ( ) {
		return displayedIcon;
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
	 * @param loading
	 */
	@Override
	public void setNodeIsLoading ( boolean loading ) {
		if ( loading ) {
			displayedIcon = loadingIcon;
		} else {
			displayedIcon = retrievedIcon;
		}
	}

	@Override
	public void setText ( String text ) {
		driveText = text;
	}

	@Override
	public void setIcon ( Icon icon ) {
		retrievedIcon = icon;
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
