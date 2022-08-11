package com.github.errayeil.betterfilechooser.ui.Sort;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;

import java.io.File;
import java.util.Objects;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class FoldersFirstSort implements BetterSort<File> {

	public BetterSort<File> thenComparing ( BetterSort<File> other ) {
		Objects.requireNonNull(other);
		return new BetterSort<> ( ) {
			@Override
			public String getName ( ) {
				return FoldersFirstSort.this.getName ( );
			}

			@Override
			public String getDescription ( ) {
				return FoldersFirstSort.this.getDescription ( );
			}

			@Override
			public int compare ( File f1 , File f2 ) {
				int res = FoldersFirstSort.this.compare ( f1 , f2 );
				return ( res != 0 ) ? res : other.compare ( f1 , f2 );
			}
		};
	}

	@Override
	public String getName ( ) {
		return Keys.sortFoldersKey;
	}

	@Override
	public String getDescription ( ) {
		return "Folders First";
	}

	@Override
	public int compare ( File f1 , File f2 ) {
		if ( f1.isDirectory ( ) && f2.isFile ( ) ) {
			return -1;
		} else if ( f1.isDirectory ( ) && f2.isDirectory ( ) ) {
			return 1;
		} else if ( f1.isFile ( ) && f2.isFile ( ) ) {
			return 0;
		}

		return 1;
	}
}
