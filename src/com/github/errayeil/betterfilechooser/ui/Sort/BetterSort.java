package com.github.errayeil.betterfilechooser.ui.Sort;

import java.util.Comparator;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public interface BetterSort<File> extends Comparator<File> {

	String getName();

	String getDescription();

	@Override
	int compare ( File f1 , File f2 );
}
