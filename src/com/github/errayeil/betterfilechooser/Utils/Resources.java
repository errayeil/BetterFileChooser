package com.github.errayeil.betterfilechooser.Utils;

import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.BRTCell;

import javax.swing.ImageIcon;
import java.util.Objects;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class Resources {

	/**
	 *
	 */
	private static ImageIcon loadingIcon;

	private Resources ( ) {
	}

	/**
	 *
	 * @return
	 */
	public static final ImageIcon getLoadingIcon ( ) {
		if ( loadingIcon == null ) {
			loadingIcon = new ImageIcon ( Objects.requireNonNull ( Resources.class.getResource ( "/com/github/errayeil/betterfilechooser/Resources/load.png" ) ) );
		}

		return loadingIcon;
	}
}
