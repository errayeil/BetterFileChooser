package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;

/**
 * Z to A file name sort.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class ZASort implements BetterSort<File> {
	@Override
	public String getName ( ) {
		return Keys.sortZAKey;
	}

	@Override
	public String getDescription ( ) {
		return "Z to A";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		return String.CASE_INSENSITIVE_ORDER.compare ( f2.getName () , f1.getName () );
	}
}
