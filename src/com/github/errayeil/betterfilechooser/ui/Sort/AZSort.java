package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;

/**
 * A to Z file name sort.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class AZSort implements BetterSort<File> {

	@Override
	public String getName ( ) {
		return Keys.sortAZKey;
	}

	@Override
	public String getDescription ( ) {
		return "A to Z";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		return String.CASE_INSENSITIVE_ORDER.compare ( f1.getName () , f2.getName () );
	}
}
