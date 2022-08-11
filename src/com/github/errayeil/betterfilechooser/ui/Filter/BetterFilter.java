package com.github.errayeil.betterfilechooser.ui.Filter;

import java.io.File;
import java.io.FileFilter;

public interface BetterFilter extends FileFilter {

	String getName ( );

	String getDescription ( );

	String[] getExtensions ( );

	@Override
	boolean accept ( File pathname );
}
