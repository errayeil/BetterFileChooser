package com.github.errayeil.betterfilechooser.ui.Worker;

import com.github.errayeil.betterfilechooser.Preferences.Registry;
import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.ui.Sort.BetterSort;
import com.github.errayeil.betterfilechooser.ui.list.File.BetterFileList;
import com.github.errayeil.betterfilechooser.ui.list.File.BetterListModel;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>A SwingWorker subclass specifically designed to sort the files of a FinderList.
 * Time consuming changes are calculated in doInBackground() and applied in the done() methods. </p>
 * <br>
 * <p>For those not familiar with Swing GUI, offloading time consuming tasks like the one below improves the
 * * responsiveness of the GUI and prevents freezes</p>
 *
 * @author Errayeil
 * @version 0.1
 * @TODO: Add Persistence
 * @TODO: Extend SwingWorker with correct type Parameters.
 * @see SwingWorker
 * @see BetterFileList
 * @see BetterListModel
 * @since 0.1
 */
public class BFLSortWorker extends SwingWorker {

	/**
	 *
	 */
	private final Registry registry = Registry.instance ();

	/**
	 * The
	 */
	private final BetterFileList list;

	/**
	 *
	 */
	private BetterListModel<File> model;

	/**
	 *
	 */
	private final BetterSort<File> activeSort;

	/**
	 *
	 */
	private List<File> files;

	/**
	 *
	 */
	public BFLSortWorker ( BetterFileList list , BetterSort<File> activeSort ) {
		this.list = list;
		this.model = ( BetterListModel<File> ) list.getModel ( );
		this.activeSort = activeSort;
	}

	@Override
	protected Object doInBackground ( ) throws Exception {
		registry.addListString ( Keys.listSortKey, activeSort.getName ( )  );

		files = new ArrayList<> ( Collections.list ( model.elements ( ) ) );
		files.sort ( activeSort );
		model = new BetterListModel<> ( );
		model.addAll ( files );
		return null;
	}

	@Override
	protected void done ( ) {
		list.setModel ( model );
	}
}
