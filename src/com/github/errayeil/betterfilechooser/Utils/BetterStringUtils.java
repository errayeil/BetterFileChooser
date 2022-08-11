package com.github.errayeil.betterfilechooser.Utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BetterStringUtils {

	/**
	 *
	 */
	private BetterStringUtils() {

	}

	/**
	 * Converts the provided File size in bytes to a readable String format.
	 *
	 * @see <a href="https://stackoverflow.com/a/3758880"> Method source </a>
	 * @param bytes
	 * @return
	 */
	public static String getHumanReadableByteCount(long bytes) {
		if ( -1000 < bytes && bytes < 1000 ) {
			return bytes + " B";
		}
		CharacterIterator ci = new StringCharacterIterator ( "kMGTPE" );
		while ( bytes <= -999_950 || bytes >= 999_950 ) {
			bytes /= 1000;
			ci.next ( );
		}
		return String.format ( "%.1f %cB" , bytes / 1000.0 , ci.current ( ) );
	}
}
