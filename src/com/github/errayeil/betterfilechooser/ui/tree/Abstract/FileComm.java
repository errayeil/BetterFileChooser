package com.github.errayeil.betterfilechooser.ui.tree.Abstract;

import javax.swing.Icon;
import java.io.File;

/**
 * A badly named interface for the custom file components. This makes them consistent
 * by implementing an interface for a few actions they all will have.
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public interface FileComm {

	/**
	 * Gets the file Icon.
	 * @param file
	 * @return
	 */
	Icon getFileIcon ( File file );

	/**
	 * Attempts to retrieve the extension of the provided file.
	 * @param file
	 * @return
	 */
	String getFileExtension ( File file );

	/**
	 * Attempts to retrieve the extension of the provided file.
	 * If includeAll is true all "." followed by lettering should be
	 * concluded to be an extension.
	 * @param file
	 * @param includeAll
	 * @return
	 */
	String getFileExtension ( File file , boolean includeAll );
}
