package com.github.errayeil.betterfilechooser.ui.tree.Root;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public enum BRTNodeType {

	ROOT_NODE,
	DESKTOP_NODE,
	DRIVE_NODE ,
	FILE_NODE ,
	FOLDER_NODE;

	/**
	 *
	 * @param nodeType
	 * @return
	 */
	public String getNodeType( BRTNodeType nodeType ) {
		String type;
		switch ( nodeType ) {
			case ROOT_NODE -> type = "ROOT";
			case DESKTOP_NODE -> type = "DESKTOP";
			case DRIVE_NODE -> type = "DRIVE";
			case FILE_NODE -> type = "FILE";
			case FOLDER_NODE -> type = "FOLDER";
			default -> type = "UNKNOWN";
		}

		return type;
	}
}
