package com.github.errayeil.betterfilechooser.Utils;

import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BetterCompUtils {

	/**
	 *
	 */
	private BetterCompUtils () {

	}

	/**
	 * @return
	 */
	public static JSeparator verticalSeparator ( ) {
		JSeparator sep = new JSeparator ( SwingConstants.VERTICAL );
		sep.setBackground ( new Color ( 0 , 0 , 0 , 0 ) );
		return sep;
	}

	/**
	 * Creates a space filling JComponent to add some padding in between components
	 * in panels when building with PanelMatic.
	 *
	 * @param width  The width the filler should have.
	 * @param height The height the filler should have.
	 *
	 * @return The newly create Box.Filler JComponent.
	 *
	 * @see io.codeworth.panelmatic.PanelMatic
	 */
	public static Box.Filler fill ( int width , int height ) {
		Dimension dim = new Dimension ( width , height );
		return new Box.Filler ( dim , dim , dim );
	}
}
