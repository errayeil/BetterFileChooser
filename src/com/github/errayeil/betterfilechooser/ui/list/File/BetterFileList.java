package com.github.errayeil.betterfilechooser.ui.list.File;

import com.github.errayeil.betterfilechooser.Builder.FilterBuilder;
import com.github.errayeil.betterfilechooser.Preferences.Registry;
import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.ui.Filter.BetterFilter;
import com.github.errayeil.betterfilechooser.ui.Sort.AZSort;
import com.github.errayeil.betterfilechooser.ui.Sort.BetterSort;
import com.github.errayeil.betterfilechooser.ui.Sort.FoldersFirstSort;
import com.github.errayeil.betterfilechooser.ui.Worker.BFLFilterWorker;
import com.github.errayeil.betterfilechooser.ui.Worker.BFLSortWorker;
import com.github.errayeil.betterfilechooser.ui.list.BetterList;

import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A custom JList tailored to displaying a list of files from a specified directory.
 * </p>
 * <br>
 * This has the ability to exclude or include hidden files on a whim, in the event someone does
 * or does not want to see hidden files/folders. The list model can also be sorted by file name, size,
 * and type, both in ascending or descending order.
 * </p>
 * <br>
 * <p>
 * File filtering is also possible. Custom filters can be utilized by implementing the BetterFilter interface.
 * Custom filters can also be added to the Registry via utilizing the FilterBuilder.
 * </p>
 * <br>
 * <p>
 * This automatically communicates to the Registry class so variables such as showHidden or showFileStats
 * will be consistent between instances. See the Keys inner class to learn exactly what is automatically tracked.
 * </p>
 * <br>
 * <p>
 * <font size="-2"> A long time ago in a galaxy far, far away..... I created this beauty.</font>
 * </p
 *
 * @author Errayeil
 * @version 0.1
 * @TODO: Refactoring - I always make sure to remind my self everything can <b><i>probably</i></b> be refactored.
 * @TODO: Documentation - Need to make sure everything is documented in depth eventually.
 * @see Registry
 * @see Keys
 * @see FilterBuilder
 * @see BetterFilter
 * @see BetterSort
 * @since 0.1
 */
public class BetterFileList extends BetterList<java.io.File> {

	/**
	 * The Persistence preferences wrapper.
	 */
	private final Registry registry = Registry.instance ( );

	/**
	 * The root directory we list files from.
	 */
	private java.io.File rootDirectory;

	/**
	 * BetterFilter which helps the FinderList filter out
	 * files that do not need to be visible.
	 */
	private BetterFilter activeFilter;

	/**
	 * The currently active comparator that is utilized to sort the BetterFileList.
	 */
	private BetterSort<java.io.File> activeSort;

	/**
	 * The model that aids in displaying information from a file.
	 * This actually doesn't do anything right now.
	 * Not sure what to use the model for, tbh
	 */
	private BetterListModel<java.io.File> model;

	/**
	 * The renderer creates the custom component that displays the file icon,
	 * name, and optionally file stats.
	 */
	private final BetterFileListCellRenderer renderer;

	/**
	 * A map of hidden files and their respective index in the original list.
	 * This helps track where the hidden files need to be inserted in the BetterFileList
	 * model in the event showHidden is set to true.
	 */
	private final Map<java.io.File, Integer> hiddenFiles;

	/**
	 * A map of filter files along with their index in the list model.
	 * In case the filter is ever changed again, we can easily insert any
	 * previously filtered files back into the model, provided they are accepted
	 * by the new filter.
	 */
	private final Map<java.io.File, Integer> filteredFiles;

	/**
	 * Boolean flag to determine if the Registry should be used.
	 */
	private boolean useRegistry;

	/**
	 * Constructs a new BetterFileList with the specified rootDirectory.
	 */
	public BetterFileList ( java.io.File rootDirectory ) {
		this.rootDirectory = rootDirectory;
		model = new BetterListModel<> ( );
		renderer = new BetterFileListCellRenderer ( );
		hiddenFiles = new HashMap<> ( );
		filteredFiles = new HashMap<> ( );
		useRegistry = true;

		setModel ( model );
		setCellRenderer ( renderer );

		//This gets the filter and sort methods that were used in a previous Finder instance
		getPreviousFilter ( );
		getPreviousSort ( );

		setRoot ( rootDirectory );

		/**
		 * I added this MouseAdapter to register right clicks as a valid index selection input.
		 * The main idea was to take away that functionality from an external MouseAdapter and just
		 * leave external adapters to showing a popup, or something.
		 * @TODO: There seems to be an issue where this right click event is not fired if multiple right clicks occur.
		 */
		addMouseListener ( new MouseAdapter ( ) {
			@Override
			public void mouseClicked ( MouseEvent e ) {
				if ( SwingUtilities.isRightMouseButton ( e ) ) {
					int index = locationToIndex ( e.getPoint ( ) );

					//Helps ensure the correct index is selected.
					if ( index > -1 && getCellBounds ( index , index ).contains ( e.getPoint ( ) ) ) {
						setSelectedIndex ( index );
					}
				}
			}
		} );

		//In the future I will dynamically calculate this. TODO
		setFixedCellHeight ( 26 );
		setFixedCellWidth ( 150 );
	}

	/**
	 * Sets the Root directory to list the files from.
	 * This is called in both the constructor and setRootDirectory().
	 *
	 * @param rootDirectory
	 */
	private void setRoot ( java.io.File rootDirectory ) {
		List<java.io.File> files = new ArrayList<> ( Arrays.asList ( Objects.requireNonNull ( rootDirectory.listFiles ( ) ) ) );

		for ( int i = 0; i < files.size ( ); i++ ) {
			java.io.File file = files.get ( i );
			if ( file.isHidden ( ) ) {
				/*
				 * Save the index for if we ever need to update the model.
				 * This also allows us to remove all the hidden files if needed.
				 */
				hiddenFiles.put ( file , i );
			}
		}

		if ( !registry.getListBoolean ( Keys.listShowHiddenKey) ) {
			files.removeAll ( hiddenFiles.keySet ( ) );
		}

		if ( activeFilter != null && !activeFilter.getName ( ).equals ( Keys.filterEverythingKey ) ) {
			applyFilter ( );
			applySort ( );
		} else if ( activeFilter == null || activeFilter.getName ( ).equals ( Keys.filterEverythingKey ) ) {
			/*
			 * Sort the file list by type (directory or file) first and then sort by name.
			 * I made this type of comment because I had something else to write, but I forgot what it was.
			 * Oh well.
			 */
			files.sort ( new FoldersFirstSort ( ).thenComparing ( new AZSort ( ) ) );
		}

		model.addAll ( files );
	}

	/**
	 * @TODO
	 */
	private void createDirectoryWatcher() {

	}

	/**
	 * Gets the previously used FinderFilter.
	 */
	private void getPreviousFilter ( ) {
		String name = registry.getListString ( Keys.listFilterKey );

		if ( name != null ) {
			switch ( name ) {

			}
		}
	}

	/**
	 * Retrieves the sort method used in a previous instance.
	 */
	private void getPreviousSort ( ) {
		String name = registry.getListString ( Keys.listSortKey );

		if ( name != null ) {
			switch ( name ) {

			}
		}
	}

	/**
	 * Applies the FinderFilter.
	 */
	private void applyFilter ( ) {
		if ( activeFilter != null ) {
			new BFLFilterWorker ( activeFilter, filteredFiles, this ).execute ();
		}
	}

	/**
	 * Applies the active sort.
	 */
	private void applySort ( ) {
		if ( activeSort != null ) {
			new BFLSortWorker ( this , activeSort );
		}
	}

	/**
	 * Removes the selected file from the list and returns the File object.
	 * <br>
	 * The returned File object can be used for misc. tasks, if needed.
	 *
	 * @return Removed selected File
	 */
	public java.io.File removeSelectedFile ( ) {
		return model.remove ( getSelectedIndex ( ) );
	}

	/**
	 * Gets the currently selected file in the list.
	 *
	 * @return Selected File
	 */
	public java.io.File getSelectedFile ( ) {
		return model.get ( getSelectedIndex ( ) );
	}

	/**
	 * @return
	 *
	 * @TODO: Implement transferable for files and push to clipboard
	 */
	public java.io.File copySelectedFile ( ) {


		return model.get ( getSelectedIndex ( ) );
	}

	/**
	 * Moves selected file to the recycle bin.
	 *
	 * @return Returns true if the action is supported and completed. False otherwise.
	 */
	public boolean trashSelectedFile ( ) {
		if ( Desktop.getDesktop ( ).isSupported ( Action.MOVE_TO_TRASH ) ) {
			Desktop.getDesktop ( ).moveToTrash ( model.get ( getSelectedIndex ( ) ) );
			model.remove ( getSelectedIndex ( ) );
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the current root directory for the FinderList.
	 *
	 * @return Current root
	 */
	public java.io.File getRootDirectory ( ) {
		return rootDirectory;
	}

	/**
	 * Returns the currently used FinderFilter.
	 * Note: This can be null.
	 */
	public BetterFilter getActiveFilter ( ) {
		return activeFilter;
	}

	/**
	 * Returns the currently used Sort.
	 * Note: This can be null.
	 *
	 * @return
	 */
	public BetterSort<java.io.File> getActiveSort ( ) {
		return activeSort;
	}

	/**
	 * Sets the root directory. <br>
	 * This causes the model to update and the FinderList to repaint.
	 *
	 * @param rootDirectory
	 */
	public void setRootDirectory ( java.io.File rootDirectory ) {
		this.rootDirectory = rootDirectory;

		model = new BetterListModel<> ( );
		hiddenFiles.clear ( );

		setModel ( model );
		setRoot ( rootDirectory );
	}

	/**
	 * Sets the file filter for the list. This will apply the filter immediately and files
	 * that are not accepted will be removed from the list.
	 * Null finder filters will reset the list to show all files/folders in the root directory.
	 *
	 * @param filter
	 */
	public void setFilter ( BetterFilter filter ) {
		this.activeFilter = filter;
		applyFilter ( );
	}

	/**
	 * Sets the active sort method.
	 *
	 * @param sort The comparator used to sort the file list.
	 */
	public void setSort ( BetterSort<java.io.File> sort ) {
		this.activeSort = sort;
		applySort ( );
	}

	/**
	 * Sets if hidden files should be visible or not.
	 * <br>
	 * If true, hidden files in the directory will be inserted into the model
	 * at their respective and sorted index.
	 * If false, they will be removed but can be inserted back at the same index
	 * in the future.
	 * <p>This is why I love Maps</p>
	 *
	 * @param showHidden
	 */
	public void setShowHidden ( boolean showHidden ) {
		registry.addListBoolean ( Keys.listShowHiddenKey , showHidden );

		List<java.io.File> keys = hiddenFiles.keySet ( ).stream ( ).toList ( );
		for ( java.io.File f : keys ) {
			int index = hiddenFiles.get ( f );
			if ( showHidden ) {
				model.add ( index , f ); //Inserts the hidden file where it would have been if it was displayed.
			} else {
				model.removeElementAt ( index );
			}
		}
	}

	/**
	 * Sets if the file stats should display in the list cells.
	 * This causes the FinderList to repaint.
	 *
	 * @param showStats
	 *
	 * @TODO: Possible swing worker worthy? It be wanting a worker. ;)
	 */
	public void setShowFileStats ( boolean showStats ) {
		registry.addListBoolean ( Keys.listShowStatsKey , showStats );

		//Get each FinderListCell component to update the showFileStats label.
		for ( int i = 0; i < model.getSize ( ); i++ ) {
			java.io.File value = model.get ( i );
			BetterFileListCell cell = ( BetterFileListCell ) renderer.getListCellRendererComponent ( this , value , i , false , false );
			cell.setShowFileStats ( showStats );
		}

		repaint ( );
	}

	/**
	 * If the FinderList is supposed to be showing file stats, or not.
	 */
	public boolean isShowingStats ( ) {
		return registry.getListBoolean ( Keys.listShowStatsKey );
	}

	/**
	 * If the FinderList is supposed to be showing hidden files, or not.
	 *
	 * @return
	 */
	public boolean isShowingHidden ( ) {
		return registry.getListBoolean ( Keys.listShowHiddenKey );
	}

	/**
	 * We override setModel to ensure that the ListModel provided is an instance of BetterListModel.
	 * If the provided list model is not of BetterListModel, an IllegalArgumentException will be thrown.
	 * <br><br>
	 * Note: I will eventually change this but for now it's a stopgap to prevent other issues down the line.
	 *
	 * @TODO: Migrate any data in the provided list model to a new BetterListModel.
	 * @param model  the <code>ListModel</code> that provides the
	 *                                          list of items for display
	 */
	@Override
	public void setModel ( ListModel model ) {
		if (model instanceof BetterListModel) {
			setModel ( model );
		} else {
			throw new IllegalArgumentException ( "This list can only accept BetterListModels" );
		}
	}
}
