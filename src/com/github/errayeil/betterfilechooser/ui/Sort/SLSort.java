package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;

/**
 * Smallest to largest file size sort.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class SLSort implements BetterSort<File> {
	@Override
	public String getName ( ) {
		return Keys.sortSmallestKey;
	}

	@Override
	public String getDescription ( ) {
		return "Smallest to Largest";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		if ( f1.length ( ) > f2.length ( ) ) {
			return -1;
		} else if ( f1.length ( ) < f2.length ( ) ) {
			return 1;
		} else {
			return 0;
		}
	}
}
