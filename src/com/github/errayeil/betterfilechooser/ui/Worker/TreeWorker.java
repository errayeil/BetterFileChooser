package com.github.errayeil.betterfilechooser.ui.Worker;

import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.FileNodeObject;
import com.github.errayeil.betterfilechooser.ui.tree.Root.BRTNode;

import javax.swing.SwingWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class TreeWorker extends SwingWorker {

	/**
	 *
	 */
	private final BRTNode parentNode;

	/**
	 *
	 */
	private final File directory;

	/**
	 *
	 */
	private List<BRTNode> nodesToAdd = new ArrayList<> (  );

	/**
	 *
	 * @param parentNode
	 * @param directory
	 */
	public TreeWorker ( final BRTNode parentNode, final File directory) {
		this.parentNode = parentNode;
		this.directory = directory;
	}


	@Override
	protected Object doInBackground ( ) throws Exception {
		if ( directory != null && directory.isDirectory ( ) ) {
			File[] files = directory.listFiles ( );

			List<BRTNode> nodesToAdd = new ArrayList<> (  );

			if ( files != null ) {
				for ( File f : files ) {
					//We don't want to show the recycling bin or other folders with the dollar sign in the path
					//TODO Test for isShowingHidden and view mode
					if ( !f.getAbsolutePath ( ).contains ( "$" )  ) {
						if (f.isDirectory ()) {
							BRTNode n = new BRTNode ( new FileNodeObject ( f ) );
							nodesToAdd.add ( n );
						}
					}
				}

				nodesToAdd.sort ( (( Comparator<BRTNode> ) ( o1 , o2 ) -> {
					File f1 = o1.getNodeObject ( ).getFile ( );
					File f2 = o2.getNodeObject ( ).getFile ( );

					if ( f1.isDirectory ( ) && f2.isFile ( ) ) {
						return -1;
					} else if ( f1.isDirectory ( ) && f2.isDirectory ( ) ) {
						return 1;
					} else if ( f1.isFile ( ) && f2.isFile ( ) ) {
						return 0;
					}

					return 1;
				}).thenComparing ( ( o1 , o2 ) -> {
					File f1 = o1.getNodeObject ( ).getFile ( );
					File f2 = o2.getNodeObject ( ).getFile ( );;

					return String.CASE_INSENSITIVE_ORDER.compare ( f1.getName () , f2.getName ());
				} ) );
			}
		}


		return null;
	}

	@Override
	protected void done ( ) {
		for (BRTNode n : nodesToAdd) {
			parentNode.add ( n );
		}
	}
}
