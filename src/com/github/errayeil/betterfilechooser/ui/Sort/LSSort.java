package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;

/**
 * Largest to smallest file size sort.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class LSSort implements BetterSort<File> {

	@Override
	public String getName ( ) {
		return Keys.sortLargestKey;
	}

	@Override
	public String getDescription ( ) {
		return "Largest to Smallest";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		if ( f1.length ( ) < f2.length ( ) ) {
			return -1;
		} else if ( f1.length ( ) > f2.length ( ) ) {
			return 1;
		} else {
			return 0;
		}
	}
}
