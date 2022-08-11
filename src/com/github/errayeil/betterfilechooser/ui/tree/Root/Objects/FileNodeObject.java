package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import com.github.errayeil.betterfilechooser.Utils.BetterFileUtils;

import javax.swing.Icon;
import java.io.File;

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
	private final Icon icon;

	/**
	 *
	 * @param file
	 */
	public FileNodeObject ( final File file ) {
		this ( file , BetterFileUtils.getSystemFileIcon ( file ) );
	}

	public FileNodeObject ( final File file , final Icon icon) {
		this.file = file;
		this.icon = icon;

		if (file != null) {
			name = file.getName ();
		}
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
	public Icon getIcon ( ) {
		return icon;
	}

	@Override
	public File getFile ( ) {
		return file;
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
