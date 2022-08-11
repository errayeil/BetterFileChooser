package com.github.errayeil.betterfilechooser.ui.Worker;

import com.github.errayeil.betterfilechooser.Preferences.Registry;
import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.ui.Filter.BetterFilter;
import com.github.errayeil.betterfilechooser.ui.Sort.AZSort;
import com.github.errayeil.betterfilechooser.ui.Sort.FoldersFirstSort;
import com.github.errayeil.betterfilechooser.ui.list.File.BetterFileList;
import com.github.errayeil.betterfilechooser.ui.list.File.BetterListModel;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>A SwingWorker subclass specifically designed to modify a FinderList and FinderListModel when a filter is
 * applied to the list. The changes that need to be calculated are performed in doInBackground() and applied
 * in done(). </p>
 * <br>
 * <p>For those not familiar with Swing GUI, offloading time consuming tasks like the one below improves the
 * responsiveness of the GUI and prevents freezes.</p>
 *
 *
 * @author Errayeil
 * @version 0.1
 * @TODO: Code Refactoring for better performance.
 * @TODO: Extend SwingWorker with correct Type Parameters.
 * @TODO: Change this to work with any BetterComponent. For example, I want this to work with BetterFileList, RootTree, and SPTree.
 * @see SwingWorker
 * @see BetterFileList
 * @see BetterListModel
 * @since 0.1
 */
public class BFLFilterWorker extends SwingWorker {

	/**
	 * The Registry wrapper class that aids in storing of data we require to be persistent.
	 * In this instance, it would be the currently active Filter. So when the user opens up
	 * an instance of FinderList it'll automatically apply the previously used filter.
	 */
	final Registry registry = Registry.instance ( );

	/**
	 * The active filter applied to the FinderFilter.
	 */
	private final BetterFilter activeFilter;

	/**
	 * A map of currently filtered files. Files in this map are not
	 * visible in the FinderList.
	 */
	private final Map<File, Integer> filteredFiles;

	/**
	 * The FinderList we retrieve the FinderListModel from and update when a filter is changed.
	 */
	private final BetterFileList list;

	/**
	 * The FinderListModel that we retrieve from the FinderList.
	 * Note: FinderListModel currently has no added functionality besides its type
	 * parameters being java.io.File.
	 */
	private BetterListModel<File> model;

	/**
	 * A list of file Indexes. This is sorted in reverse order
	 * before we remove files from the model. This ensures we remove the correct
	 * index.
	 */
	private final List<Integer> indexes = new ArrayList<> ( );

	/**
	 * A list of files pulled from the FinderListModel.
	 */
	private List<File> files;

	/**
	 * Constructs a new FilterWorker with the required parameters needed
	 * to successfully complete the process.
	 * Note: Keep in mind SwingWorkers are essentially one time use.
	 */
	public BFLFilterWorker ( BetterFilter activeFilter , Map<File, Integer> filteredFiles , BetterFileList list ) {
		this.activeFilter = activeFilter;
		this.filteredFiles = filteredFiles;
		this.list = list;
		this.model = ( BetterListModel ) list.getModel ( );
	}

	/**
	 * doInBackground calculates all the changes that need to be applied to the FinderList and its model.
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	@Override
	protected Object doInBackground ( ) throws Exception {

		if ( activeFilter != null && !activeFilter.getName ( ).equals ( Keys.filterEverythingKey) ) {
			registry.addListString ( Keys.listFilterKey , activeFilter.getName ( ) );

			List<File> keys = filteredFiles.keySet ( ).stream ( ).toList ( );

			//Remove all previously filtered files and add them back to the list
			for ( File key : keys ) {
				if ( activeFilter.accept ( key ) && !key.isDirectory ( ) ) {
					model.add ( filteredFiles.get ( key ) , key );
				}
			}

			filteredFiles.clear ( );

			//Get new filtered files and remove them.
			for ( int i = 0; i < model.size ( ); i++ ) {
				File file = model.get ( i );

				if ( !activeFilter.accept ( file ) || file.isDirectory ( ) ) {
					filteredFiles.put ( file , i );
					indexes.add ( i );
				}
			}
			indexes.sort ( Collections.reverseOrder ( ) );
		} else {

			files = Collections.list ( model.elements ( ) );
			files.addAll ( filteredFiles.keySet ( ) );
			filteredFiles.clear ( );

			files.sort ( new FoldersFirstSort ().thenComparing ( new AZSort () ) );
			model = new BetterListModel<> ( );
			model.addAll ( files );
		}

		return null;
	}

	/**
	 * When doInBackground() finishes done() runs and applies the changes calculated.
	 */
	@Override
	protected void done ( ) {
		if ( activeFilter != null && !activeFilter.getName ( ).equals ( Keys.filterEverythingKey ) ) {
			for ( int i : indexes ) {
				model.remove ( i );
			}
		} else {
			list.setModel ( model );
		}
	}
}
