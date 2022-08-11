package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;
import java.util.Objects;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class FilesFirstSort implements BetterSort<File> {

	/**
	 * I'm so ashamed on how long it took me to figure this out.
	 * Literally pulled that confusing ass code from class Comparator for the comparing
	 * and didn't understand how it worked. Made an educated guess and the result below
	 * was right.
	 * @param other
	 * @return
	 */
	public BetterSort<File> thenComparing ( BetterSort<File> other ) {
		Objects.requireNonNull ( other );
		return new BetterSort<> ( ) {
			@Override
			public String getName ( ) {
				//I don't know if this is what I'm supposed to do?
				return FilesFirstSort.this.getName ( );
			}

			@Override
			public String getDescription ( ) {
				return FilesFirstSort.this.getDescription ( );
			}

			@Override
			public int compare ( File f1 , File f2 ) {
				int res = FilesFirstSort.this.compare ( f1 , f2 );
				return ( res != 0 ) ? res : other.compare ( f1 , f2 );
			}
		};
	}

	@Override
	public String getName ( ) {
		return Keys.sortFilesKey;
	}

	@Override
	public String getDescription ( ) {
		return "Files First";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		if ( f1.isFile ( ) && f2.isDirectory ( ) ) {
			return -1;
		} else if ( f1.isFile ( ) && f2.isFile ( ) ) {
			return 1;
		} else if ( f1.isDirectory ( ) && f2.isDirectory ( ) ) {
			return 0;
		}

		return 1;
	}
}
