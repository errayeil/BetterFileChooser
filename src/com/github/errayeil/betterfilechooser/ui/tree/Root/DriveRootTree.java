package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.Preferences.Registry;
import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.ui.tree.BetterTree;
import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.DriveNodeObject;
import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.FileNodeObject;
import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.INodeObject;
import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.PCNodeObject;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.github.errayeil.betterfilechooser.Utils.BetterFileUtils.*;
import static javax.swing.filechooser.FileSystemView.getFileSystemView;

/**
 * Creates a system storage drive root tree. Currently, this will only show folders and automatically does not show
 * hidden folders.
 *
 * @author Errayeil
 * @version 0.1
 * @TODO: Loading icon that appears in the Tree cell label when updating the view mode or opening a directory with a lot
 * of files that can slow down the createChildren function. I wanted to utilize Lottie Framework, but unfortunately that
 * is not available for Java, yet. It'll probably end up being gif images.
 * @since 0.1
 */
public class DriveRootTree extends BetterTree {

	/**
	 * Preferences wrapper class that stores data such was BetterFileChooser dialog location and
	 * size for consistency between instances.
	 */
	private final Registry registry = Registry.instance ( );

	/**
	 * The top node of the RootTree. Normally this will say "This PC".
	 */
	private final BRTNode topRootNode;

	/**
	 * A list of drive nodes. We use this to track all the users
	 * root drives for when we need to create nodes in the tree.
	 */
	private final List<BRTNode> driveNodesList;

	/**
	 * <p>
	 * A map containing hidden files wrapped into their BRTNode Objects.
	 * </p>
	 * Key: Parent node
	 * <br>
	 * Value: List of child nodes.
	 */
	private final Map<BRTNode, List<BRTNode>> hiddenNodes;

	/**
	 * <p>
	 * A map containing files that had a dollar sign in their path.
	 * </p>
	 * Key: Parent node
	 * <br>
	 * Value: List of child nodes with dollar signs in the file path.
	 *
	 * @TODO: Eventually I'll add extra criteria to determine what should
	 * and should not end up in this map, but for now it's to avoid a node
	 * being created for the Recycling Bin since that throws a ton of exceptions.
	 */
	private final Map<BRTNode, List<BRTNode>> dsNodes;

	/**
	 * <p>
	 * A Map of cached nodes. Cached nodes are BRTNodes that have
	 * been removed from the RootTree because at the time they did
	 * not qualify to be visible due to the view mode.
	 * </p>
	 * <br>
	 * Key: Parent node
	 * <br>
	 * Value: List of child nodes
	 */
	private final Map<BRTNode, List<BRTNode>> cachedNodes;

	/**
	 * A set of currently expanded paths. Any new path that is added
	 * that has its parent path currently in this set will override
	 * the parent path.
	 */
	private final Set<TreePath> expandedPaths;

	/**
	 * The Tree model currently in use by the RootTree.
	 */
	private final DefaultTreeModel currentModel;

	/**
	 * The popup menu that should display on right click.
	 */
	private JPopupMenu treePopup;

	/**
	 * The current mouse listener for the popup menu.
	 */
	private MouseListener popupListener;

	/**
	 * The currently selected node.
	 */
	private BRTNode selectedNode;

	/**
	 * The currently selected file.
	 * <br>
	 * Note: This can be null.
	 */
	private File selectedFile;

	/**
	 * The integer code that determines the current view mode.
	 */
	private int viewMode;

	/**
	 * If hidden files/folders should be shown.
	 */
	private boolean showHidden;

	/**
	 * Boolean flag to determine if a expansion events should be carried out.
	 * If set to false, this essentially makes the RootTree as a shortcut medium
	 * for the storage drives. This is by default set to true.
	 */
	private boolean expandable = true;

	/**
	 * Boolean flag to determine if a nodes children should be removed when
	 * it is collapsed. This is by default set to true.
	 */
	private boolean unloadCollapsed = true;

	/**
	 * Integer codes to help determine the file view mode of the RootTree.
	 */
	public static int FOLDERS_ONLY = -1;
	public static int FOLDERS_AND_FILES = 0;


	/**
	 * Constructs a RootTree with the default roots and sets the view mode and hidden file
	 * visibility to the provided parameters.
	 */
	public DriveRootTree ( ) {
		this.viewMode = registry.getTreeInt ( Keys.treeViewModeKey );
		this.showHidden = registry.getTreeBoolean ( Keys.treeShowHiddenKey );

		topRootNode = new BRTNode ( new PCNodeObject ( "This PC" , getSystemFileIcon ( getDesktopFile ( ) ) ) );
		currentModel = new DefaultTreeModel ( topRootNode );
		driveNodesList = new ArrayList<> ( );
		hiddenNodes = new HashMap<> ( );
		dsNodes = new HashMap<> ( );
		cachedNodes = new HashMap<> ( );
		expandedPaths = new HashSet<> ( );

		BRTNode desktopNode = new BRTNode ( new FileNodeObject ( getDesktopFile ( ) , getSystemFileIcon ( getDesktopFile ( ) ) ) );
		topRootNode.add ( desktopNode );

		List<Path> rootsList = new ArrayList<> ( listDeviceRoots ( ) );
		FileSystemView view = getFileSystemView ( );
		for ( Path p : rootsList ) {
			File f = p.toFile ( );

			DriveNodeObject dn = new DriveNodeObject ( f , view.getSystemDisplayName ( f ) , getSystemFileIcon ( f ) );
			BRTNode driveNode = new BRTNode ( dn );
			driveNodesList.add ( driveNode );
			topRootNode.add ( driveNode );
		}

		setToggleClickCount ( 0 );
		setupListeners ( );
		setEditable ( false );
		setShowsRootHandles ( false );
		setCellRenderer ( new BRTCellRenderer ( ) );
		setModel ( currentModel );
	}


	/**
	 * Creates the listeners for the RootTree.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private void setupListeners ( ) {

		addMouseListener ( new MouseAdapter ( ) {
			@Override
			public void mousePressed ( MouseEvent e ) {
				TreePath path = getPathForLocation ( e.getX ( ) , e.getY ( ) );

				if ( path != null ) {
					setSelectionPath ( path );
				}

				if ( SwingUtilities.isLeftMouseButton ( e ) && e.getClickCount ( ) == 1 ) {
					if ( DriveRootTree.this.expandable ) {
						if ( path != null ) {
							BRTNode node = ( BRTNode ) path.getLastPathComponent ( );
							if ( node != null ) {
								INodeObject obj = node.getNodeObject ( );

								if ( !( obj instanceof PCNodeObject ) ) {
									selectedFile = node.getNodeObject ( ).getFile ( );
									selectedNode = node;
									createChildren ( node , selectedFile );
								}
							}
						}
					} else {
						//TODO log
					}
				}
			}
		} );

		/**
		 * Tracking the paths expanded when the event occurs will make it easier for us
		 * to update paths when the viewmode changes.
		 */
		addTreeExpansionListener ( new TreeExpansionListener ( ) {
			@Override
			public void treeExpanded ( TreeExpansionEvent event ) {
				for ( int a = 0; a < getRowCount ( ) - 1; a++ ) {
					TreePath currentPath = getPathForRow ( a );
					TreePath nextPath = getPathForRow ( a + 1 );

					if ( currentPath.isDescendant ( nextPath ) ) {
						expandedPaths.add ( currentPath );
					}
				}

				List<TreePath> toRemove = new ArrayList<> ( );
				for ( TreePath p : expandedPaths ) {
					if ( expandedPaths.contains ( p.getParentPath ( ) ) ) {
						toRemove.add ( p.getParentPath ( ) );
					}
				}

				toRemove.forEach ( expandedPaths::remove );
			}

			@Override
			public void treeCollapsed ( TreeExpansionEvent event ) {
				if ( unloadCollapsed ) {
					BRTNode node = ( BRTNode ) event.getPath ( ).getLastPathComponent ( );
					removeChildren ( node );
				}

				expandedPaths.remove ( event.getPath ( ) );
			}
		} );
	}

	/**
	 * Removes all the children from the specified parent node and also removes
	 * any cached, hidden, or dollar sign files from the Maps.
	 *
	 * @param parentNode The parent node we're removed the children from.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private synchronized void removeChildren ( final BRTNode parentNode ) {
		SwingWorker removeWorker = new SwingWorker ( ) {

			/**
			 *
			 * @return
			 * @throws Exception
			 */
			@Override
			protected Object doInBackground ( ) throws Exception {
				/**
				 * At this point we can remove all the parent nodes
				 * cached files from the Maps. They will be loaded again
				 * when the parent node is expanded.
				 */
				dsNodes.remove ( parentNode );
				hiddenNodes.remove ( parentNode );
				cachedNodes.remove ( parentNode );

				return null;
			}

			/**
			 *
			 */
			@Override
			protected void done ( ) {

				clearSelection ( );
				parentNode.removeAllChildren ( );
			}
		};

		removeWorker.execute ( );
	}

	/**
	 * @param parentNode
	 * @param directory
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private void createChildren ( BRTNode parentNode , File directory ) {
		List<BRTNode> childNodes = new ArrayList<> ( );

		SwingWorker worker = new SwingWorker ( ) {
			@Override
			protected Object doInBackground ( ) throws Exception {
				if ( directory != null && directory.isDirectory ( ) ) {
					File[] files = directory.listFiles ( );

					if ( files != null ) {
						for ( File f : files ) {
							BRTNode childNode = new BRTNode ( new FileNodeObject ( f ) );

							childNodes.add ( childNode );
						}
					}
				}
				sortChildren ( childNodes );
				filterChildren ( parentNode , childNodes );

				return null;
			}

			@Override
			protected void done ( ) {
				for ( BRTNode n : childNodes ) {
					parentNode.add ( n );
				}

				expandPath ( getSelectionPath ( ) );
			}
		};

		worker.execute ( );
	}

	/**
	 * Sorts the provided list of nodes by folders first and then alphabetically.
	 *
	 * @param children
	 *
	 * @return
	 *
	 * @version 0.1
	 * @TODO: Provide the ability to set custom sorters.
	 * @since 0.1
	 */
	private synchronized void sortChildren ( List<BRTNode> children ) {
		children.sort ( ( ( Comparator<BRTNode> ) ( o1 , o2 ) -> {
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
		} ).thenComparing ( ( o1 , o2 ) -> {
			File f1 = o1.getNodeObject ( ).getFile ( );
			File f2 = o2.getNodeObject ( ).getFile ( );

			return String.CASE_INSENSITIVE_ORDER.compare ( f1.getName ( ) , f2.getName ( ) );
		} ) );
	}

	/**
	 * <p>
	 * Removes any BRTNode from the child node list in the event they do not match current filter criteria.
	 * For example, if the current view mode is Folders only, files will be removed. Hidden files will be removed
	 * if showHidden is set to false, also.
	 * </p>
	 * <p>
	 * Removed nodes are added to 1 of 3 Maps, depending on why they were removed. There is a Map for files
	 * with a dollar sign ($) in their path, nodes representing hidden files, and nodes that did not meet the view
	 * mode criteria.
	 * </p>
	 *
	 * @param parentNode The parent of the list of child nodes.
	 * @param childNodes The list of child nodes we need to filter.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private synchronized void filterChildren ( BRTNode parentNode , List<BRTNode> childNodes ) {
		List<BRTNode> toRemove = new ArrayList<> ( );
		for ( BRTNode n : childNodes ) {
			File f = n.getNodeObject ( ).getFile ( );

			if ( f != null ) {
				if ( !f.getAbsolutePath ( ).contains ( "$" ) ) {
					if ( f.isHidden ( ) && !showHidden ) {
						toRemove.add ( n );
						continue;
					}

					if ( f.isFile ( ) && viewMode != FOLDERS_AND_FILES ) {
						toRemove.add ( n );
					}

				} else {
					toRemove.add ( n );
				}
			}
		}

		childNodes.removeAll ( toRemove );

		/**
		 * I'm utilizing SwingWorker here because it seems like less of a headache
		 * to use over Thread.
		 * <br>
		 * Because of SwingWorker, I want to make sure this operates off the previous view mode
		 * in the event filterForViewMode is called before the current worker completes.
		 */
		final int localViewMode = viewMode;
		SwingWorker worker = new SwingWorker ( ) {
			final List<BRTNode> dsList = new ArrayList<> ( );
			final List<BRTNode> hiddenList = new ArrayList<> ( );
			final List<BRTNode> cachedList = new ArrayList<> ( );

			@Override
			protected Object doInBackground ( ) throws Exception {

				for ( BRTNode n : toRemove ) {
					File f = n.getNodeObject ( ).getFile ( );

					if ( f != null ) {
						if ( f.getAbsolutePath ( ).equals ( "$" ) ) {
							dsList.add ( n );
							continue;
						}

						if ( f.isHidden ( ) ) {
							hiddenList.add ( n );
							continue;
						}

						if ( f.isFile ( ) && localViewMode != FOLDERS_AND_FILES ) {
							cachedList.add ( n );
						}
					}
				}

				dsNodes.put ( parentNode , dsList );
				hiddenNodes.put ( parentNode , hiddenList );
				cachedNodes.put ( parentNode , cachedList );

				return null;
			}
		};

		worker.execute ( );
	}

	/**
	 * Updates the view mode for the currently selected node.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private void updateSelectedViewmode ( BRTNode selectedNode ) {
		List<BRTNode> children = new ArrayList<> ( );
		for ( int i = 0; i < selectedNode.getChildCount ( ); i++ ) {
			children.add ( ( BRTNode ) selectedNode.getChildAt ( i ) );
		}

		List<BRTNode> cached = cachedNodes.get ( selectedNode );
		if ( cached != null ) {
			children.addAll ( cached );
			cachedNodes.remove ( selectedNode );
		}

		sortChildren ( children );
		filterChildren ( selectedNode , children );

		selectedNode.removeAllChildren ( );
		for ( BRTNode n : children ) {
			selectedNode.add ( n );
		}

		reloadAndExpand ( );
	}

	/**
	 * Updates all the unselected expanded paths.
	 *
	 * @TODO: Optimize and refactor. I think this is in a good spot right now but would love for some to chime in.
	 */
	private synchronized void updateUnselectedViewMode ( ) {

		for ( TreePath p : expandedPaths ) {
			System.out.println ( p );
			for ( int i = 0; i < p.getPathCount ( ); i++ ) {
				List<BRTNode> children = new ArrayList<> ( );
				BRTNode n = ( BRTNode ) p.getPathComponent ( i );

				if ( !( n.getNodeObject ( ) instanceof PCNodeObject ) ) {
					if ( !( n.getNodeObject ( ) instanceof DriveNodeObject ) ) {
						for ( int a = 0; a < n.getChildCount ( ); a++ ) {
							children.add ( ( BRTNode ) n.getChildAt ( a ) );
						}
						List<BRTNode> cached = cachedNodes.get ( n );

						if ( cached != null ) {
							children.addAll ( cached );
							cachedNodes.remove ( n );
						}

						sortChildren ( children );
						filterChildren ( n , children );

						for ( BRTNode cn : children ) {
							if ( !n.isNodeChild ( cn ) ) {
								n.add ( cn );
							}
						}
					}
				}
			}
		}

		//Make sure to use an array here. Iterating through the set causes a ConcurrentModificationException
		TreePath[] ps = expandedPaths.toArray ( TreePath[]::new );
		for ( TreePath p : ps ) {
			expandPath ( p );
		}
	}

	/**
	 * Gets the selection path(s) before reloading the model and then expands the path
	 * again. This is so when node changes occur we can keep the selection paths expanded.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	private void reloadAndExpand ( ) {
		TreePath[] paths = getSelectionPaths ( );
		currentModel.reload ( );

		if ( paths != null ) {
			for ( TreePath p : paths ) {
				expandPath ( p );
			}
		}
	}

	/**
	 * Returns the selected file in the RootTree.
	 * <br>
	 * There's a possibility this could return null in the event the selected Tree node does
	 * not have a file associated with it. You should test for null when calling this method.
	 *
	 * @return
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public File getSelectedFile ( ) {
		return selectedFile;
	}

	/**
	 * Returns if this RootTree nodes can be expanded.
	 *
	 * @return
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public boolean isExpandable ( ) {
		return expandable;
	}

	/**
	 * Returns if this RootTree will unload children nodes
	 * when their parent is collapsed.
	 *
	 * @return
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public boolean isUnloadingOnCollapse ( ) {
		return unloadCollapsed;
	}

	/**
	 * Sets the popup menu on right click for the RootTree.
	 *
	 * @param popup - the popup that will be assigned to this component
	 *              If a null popup is assigned the previous assigned
	 *              popup will be removed and its mouse adapter will be
	 *              removed from the RootTree.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	@Override
	public void setComponentPopupMenu ( JPopupMenu popup ) {
		if ( popup != null ) {
			this.treePopup = popup;
		} else {
			if ( popupListener != null ) {
				removeMouseListener ( popupListener );
				popupListener = null;
				return;
			}
		}

		if ( popupListener == null ) {
			popupListener = new MouseAdapter ( ) {
				@Override
				public void mouseClicked ( MouseEvent e ) {
					if ( SwingUtilities.isRightMouseButton ( e ) ) {
						if ( treePopup != null ) {
							treePopup.show ( e.getComponent ( ) , e.getX ( ) , e.getY ( ) );
						}
					}
				}
			};
		}
	}


	/**
	 * Sets the text of the top node of the Tree.
	 *
	 * @apiNote Not currently working
	 * @param text
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public void setTopRootText ( final String text ) {
		topRootNode.setUserObject ( new PCNodeObject ( text , getSystemFileIcon ( getDesktopFile ( ) ) ) );

		currentModel.nodeChanged ( topRootNode );
	}

	/**
	 * Sets the icon for the top root node.
	 *
	 * @param icon
	 * @apiNote Not currently working
	 * @version 0.1
	 * @since 0.1
	 */
	public void setTopRootIcon ( final Icon icon ) {
		PCNodeObject object = ( PCNodeObject ) topRootNode.getNodeObject ( );
		topRootNode.setUserObject ( new PCNodeObject ( object.getText ( ) , icon ) );
		reloadAndExpand ( );
	}

	/**
	 * Sets the view mode of the JTree.
	 * <br>
	 * The options are FOLDERS_ONLY (-1) and FOLDERS_AND_FILES (0)..
	 *
	 * @param viewMode
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public void setViewMode ( int viewMode ) {
		this.viewMode = viewMode;
		registry.addTreeInt ( Keys.treeViewModeKey , viewMode );

		TreePath path = getSelectionPath ( );

		if ( path != null ) {
			BRTNode node = ( BRTNode ) path.getLastPathComponent ( );
			if ( node != null ) {
				INodeObject obj = node.getNodeObject ( );

				//Make sure the last path node isn't the root node.
				if ( !( obj instanceof PCNodeObject ) ) {
					updateSelectedViewmode ( node );
				}
			}
		}

		//Node we can do the unselected nodes.
		updateUnselectedViewMode ( );
	}

	/**
	 * Sets if the RootTree should show hidden files.
	 *
	 * @param showHidden
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public void setShowHidden ( boolean showHidden ) {
		this.showHidden = showHidden;
		registry.addTreeBoolean ( Keys.treeShowHiddenKey , showHidden );

	}

	/**
	 * @param expandable
	 */
	public void setExpandable ( boolean expandable ) {
		this.expandable = expandable;
	}

	/**
	 * @param unload
	 */
	public void setUnloadOnCollapse ( boolean unload ) {
		this.unloadCollapsed = unload;
	}
}

