package com.github.errayeil.betterfilechooser.Utils;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BetterFileUtils {

	/**
	 *
	 */
	private BetterFileUtils ( ) {

	}

	/**
	 * Gets an list of all the drives connected to the users' device.
	 * <br>
	 * This will include any type of drive plugged in via USB.
	 *
	 * @return
	 */
	public static List<Path> listDeviceRoots ( ) {
		FileSystem system = FileSystems.getDefault ( );
		Iterator<Path> it = system.getRootDirectories ( ).iterator ( );
		List<Path> roots = new ArrayList<> ( );
		it.forEachRemaining ( roots::add );

		return roots;
	}

	/**
	 * Returns a file pointing to the user desktop file.
	 *
	 * @return
	 */
	public static File getDesktopFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Desktop" );
	}

	/**
	 * Returns a file pointing to the user documents folder.
	 *
	 * @return
	 */
	public static File getDocumentsFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Documents" );
	}

	/**
	 * Returns a file pointing to the users downloads folder.
	 *
	 * @return
	 */
	public static File getDownloadsFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Downloads" );
	}

	/**
	 * Returns a file pointing to the user's music folder.
	 *
	 * @return
	 */
	public static File getMusicFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Music" );
	}

	/**
	 * Returns a file pointing to the users Pictures folder.
	 *
	 * @return
	 */
	public static File getPicturesFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Pictures" );
	}

	/**
	 * Returns a file pointing to the users Videos folder.
	 *
	 * @return
	 */
	public static File getVideosFile ( ) {
		return new File ( System.getProperty ( "user.home" ) + File.separator + "Videos" );
	}

	/**
	 * @param path
	 *
	 * @return
	 */
	public static String getSystemDisplayName ( final String path ) {
		return FileSystemView.getFileSystemView ( ).getSystemDisplayName ( new File ( path ) );
	}

	/**
	 *
	 * @param file
	 * @return
	 */
	public static String getSystemDisplayName ( final File file ) {
		return FileSystemView.getFileSystemView ( ).getSystemDisplayName ( file );
	}

	/**
	 * Gets the file systems icon for the specified file.
	 *
	 * @param path The path of the file we need the icon for.
	 *
	 * @return The files icon. This is system dependant.
	 */
	public static Icon getSystemFileIcon ( final String path ) {
		return FileSystemView.getFileSystemView ( ).getSystemIcon ( new File ( path ) );
	}

	/**
	 * @param file
	 *
	 * @return
	 */
	public static Icon getSystemFileIcon ( final File file ) {
		return FileSystemView.getFileSystemView ( ).getSystemIcon ( file );
	}

}
