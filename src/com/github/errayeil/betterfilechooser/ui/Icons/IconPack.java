package com.github.errayeil.betterfilechooser.ui.Icons;

import javax.swing.Icon;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public interface IconPack {

	/**
	 * Returns the path of the json file
	 * this pack was built from.
	 *
	 * @return
	 */
	String getPackManifestPath ( );

	/**
	 * Returns the name of the icon pack.
	 *
	 * @return
	 */
	String getPackName ( );

	/**
	 * Returns the number of icons contained in this
	 * pack.
	 *
	 * @return
	 */
	int getIconCount ( );

	/**
	 *
	 * @return
	 */
	String[] getKeys ( );

	/**
	 * Returns the icon assigned to the UI key provided, if one is available.
	 *
	 * @param key
	 *
	 * @return
	 */
	Icon getIcon ( String key );

}
