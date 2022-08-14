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
public class FileNodeObject implements INodeObject {

	/**
	 *
	 */
	private final File file;

	/**
	 *
	 */
	private String name;

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
	private ImageIcon loadingIcon;

	/**
	 *
	 * @param file
	 */
	public FileNodeObject ( final File file ) {
		this ( file , BetterFileUtils.getSystemFileIcon ( file ) );
	}

	/**
	 *
	 * @param file
	 * @param icon
	 */
	public FileNodeObject ( final File file , final Icon icon) {
		this.file = file;
		this.retrievedIcon = icon;
		this.loadingIcon = getLoadingIcon();

		if (file != null) {
			name = file.getName ();
		}

		displayedIcon = icon;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getText ( ) {
		return name;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Icon icon ( ) {
		return displayedIcon;
	}

	@Override
	public File getFile ( ) {
		return file;
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
		this.name = text;
	}

	@Override
	public void setIcon ( Icon icon ) {
		this.retrievedIcon = icon;
	}

	@Override
	public NodeType getNodeType ( ) {
		return NodeType.FILE_NODE;
	}

	@Override
	public String toString ( ) {
		if (file != null) {
			if (name.equals ( "" )) {
				return file.getAbsolutePath ();
			} else {
				return name;
			}
		} else {
			return "Not available";
		}
	}
}
