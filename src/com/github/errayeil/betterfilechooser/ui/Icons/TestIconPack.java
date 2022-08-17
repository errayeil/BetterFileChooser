package com.github.errayeil.betterfilechooser.ui.Icons;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class TestIconPack implements IconPack {

	/**
	 *
	 */
	private String packManifestPath;

	/**
	 *
	 */
	private String packName;

	/**
	 *
	 */
	private int iconCount;

	/**
	 *
	 */
	private Map<String , String > icons;

	/**
	 *
	 * @param pathToManifest
	 */
	public TestIconPack ( String pathToManifest ) {
		this.packManifestPath = pathToManifest;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getPackManifestPath ( ) {
		return packManifestPath;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getPackName ( ) {
		return packName;
	}
	/**
	 * Returns the number of icons available in this pack.
	 * @return
	 */
	@Override
	public int getIconCount ( ) {
		return icons.values ().stream ().mapToInt ( Integer::parseInt ).sum ();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String[] getKeys ( ) {
		return icons.keySet( ).toArray( new String[ 0 ] );
	}

	/**
	 *
	 * @param key
	 *
	 * @return
	 */
	@Override
	public Icon getIcon ( String key ) {
		String path = icons.get ( key );

		if ( path != null ) {
			try {
				return new ImageIcon ( ImageIO.read ( new File ( path ) ) );
			} catch ( IOException e ) {
				throw new RuntimeException ( e );
			}
		}

		return null;
	}
}
