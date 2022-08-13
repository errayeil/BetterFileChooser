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
import java.awt.MouseInfo;
import java.awt.Point;
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
	 *
	 */
	private MouseListener doubleClickListener;

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
	 * The click count needed to expand a selected path.
	 */
	private int toggleClickCount = 1;

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
	 * Boolean flag to determine if there is no TreePopup currently assigned that if
	 * right clicks should select the path just like left clicks would.
	 */
	private boolean rightClickSelects = false;

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
		this.viewMode = -1; //registry.getTreeInt ( Keys.treeViewModeKey );
		this.showHidden = false; //registry.getTreeBoolean ( Keys.treeShowHiddenKey );

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
		for ( Path p : rootsList ) {
			File f = p.toFile ( );

			DriveNodeObject dn = new DriveNodeObject ( f , getSystemDisplayName ( f ) , getSystemFileIcon ( f ) );
			BRTNode driveNode = new BRTNode ( dn );
			driveNodesList.add ( driveNode );
			topRootNode.add ( driveNode );
		}

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

				if (SwingUtilities.isLeftMouseButton ( e )) {
					if ( path != null ) {
						setSelectionPath ( path );
					}

					if ( e.getClickCount ( ) == 1 ) {
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
						} else if ( e.getClickCount ( ) == 2 ) {
							if ( doubleClickListener != null ) {
								fireDoubleClickEvent ( );
							}
						}
					}
				} else if ( SwingUtilities.isRightMouseButton ( e ) ) {
					if (treePopup != null ) {
						if ( path != null ) {
							setSelectionPath ( path );
						}
					} else {
						if ( rightClickSelects ) {
							if ( path != null ) {
								setSelectionPath ( path );
							}
						}
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
				/*
				 * When a node expands we take some time to determine if that nodes TreePath
				 * has already been added to the expandedPaths set. We do this to remove
				 * potential duplicates. The Set will not allow duplicates inherently, however,
				 * we want to avoid paths such as:
				 * [ FolderA ]
				 * [ FolderA , FolderB ]
				 * If both were added it will cause some issues when the view mode is updated.
				 * Since the second path is valid we'll remove the first one since we do not need it anymore.
				 */
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

				//Remove the TreePath from the expandedPaths array, as it's no longer needed.
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
	 * When a node is expanded createChildren is called to get files within the folder of the parent node.
	 *
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

		//Removes all the children nodes that did not meet the current criteria for the current view settings
		childNodes.removeAll ( toRemove );

		/*
		  I'm utilizing SwingWorker here because it seems like less of a headache
		  to use over Thread.
		  <br>
		  Because of SwingWorker, I want to make sure this operates off the previous view mode
		  in the event filterForViewMode is called before the current worker completes.
		  <br>
		  I don't actually know if this would do anything, but I'm making an educated guess until
		  I take the time to use my google-fu to clarify.
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
	private void updateSelectedVisibleNodes ( BRTNode selectedNode ) {
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
	 * @TODO: Optimize and refactor. I think this is in a good spot right now but would love for some one to chime in.
	 */
	private synchronized void updateUnselectedVisibleNodes ( ) {

		for ( TreePath p : expandedPaths ) {
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
	 * Starts the update process when the view mode or show hidden criteria has changed.
	 */
	private void startViewUpdate ( ) {
		TreePath path = getSelectionPath ( );

		if ( path != null ) {
			BRTNode node = ( BRTNode ) path.getLastPathComponent ( );
			if ( node != null ) {
				INodeObject obj = node.getNodeObject ( );

				//Make sure the last path node isn't the root node.
				if ( !( obj instanceof PCNodeObject ) ) {
					updateSelectedVisibleNodes ( node );
				}
			}
		}

		//Now we can do the unselected nodes.
		updateUnselectedVisibleNodes ( );
	}

	/**
	 * Gets the selection path(s) before reloading the model and then expands the path
	 * again. This is so when node changes occur we can keep the selection paths expanded.
	 *
	 * @TODO: Change the reload call to only reload change nodes, not the entire model.
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
	 * Fires the double click event for the attached double click listener.
	 */
	private void fireDoubleClickEvent ( ) {
		Point mp = MouseInfo.getPointerInfo ( ).getLocation ( );
		Point cp = getLocationOnScreen ( );
		MouseEvent event = new MouseEvent ( this , MouseEvent.MOUSE_PRESSED , System.currentTimeMillis ( ) , 0 , mp.x - cp.x , mp.y - cp.y , 2 , false );

		doubleClickListener.mousePressed ( event );
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
	 * Overridden to aid in the custom expansion/collapse process.
	 * <br>
	 * The super tree has its toggle count set to 0 no matter what since we handle the click count
	 * in DriveRootTree.
	 *
	 * @param clickCount the number of mouse clicks to get a node expanded or closed
	 */
	@Override
	public void setToggleClickCount ( int clickCount ) {
		super.setToggleClickCount ( 0 );
	}

	/**
	 * Overridden to prevent editing of cells, until a custom implementation can be created.
	 *
	 * @param flag a boolean value, true if the tree is editable
	 *
	 * @TODO: Editing a cell renames the file.
	 */
	@Override
	public void setEditable ( boolean flag ) {
		super.setEditable ( false );
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
	 * Sets the mouse adapter that should receive double click events from the tree.
	 * This could be used for automatically retrieving the currently selected file, for example.
	 *
	 * @param adapter
	 *
	 * @apiNote Currently only mousePressed events are fired. This can be expanded in the future if needed.
	 */
	public void setDoubleClickListener ( MouseAdapter adapter ) {
		this.doubleClickListener = adapter;
	}


	/**
	 * Sets the text of the top node of the Tree.
	 *
	 * @param text
	 *
	 * @apiNote Not currently working
	 * @version 0.1
	 * @since 0.1
	 */
	public void setTopRootText ( final String text ) {
		BRTNode node = (BRTNode ) currentModel.getRoot ( );
		node.setUserObject ( new PCNodeObject ( text , getSystemFileIcon ( getDesktopFile ( ) ) ) );

		currentModel.reload ( node );
	}

	/**
	 * Sets the icon for the top root node.
	 *
	 * @param icon
	 *
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
	 * The options are FOLDERS_ONLY (-1) and FOLDERS_AND_FILES (0).
	 *
	 * @param viewMode
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public void setViewMode ( int viewMode ) {
		this.viewMode = viewMode;
		registry.addTreeInt ( Keys.treeViewModeKey , viewMode );

		startViewUpdate ( );
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

		startViewUpdate ( );
	}

	/**
	 * <p>
	 * Sets if the trees nodes can be expanded. If collapseNow is set to true, all
	 * currently expanded paths will be collapsed and if unloadCollapsed is set to true
	 * all nodes with children will have their children unloaded.
	 * </p>
	 * <p>
	 * The expandedPaths list will be cleared as well, as there will be no paths expanded to track.
	 * All caches will be cleared as well.
	 * </p>
	 *
	 * @param expandable  If nodes containing children should be allowed to expand.
	 * @param collapseNow if all the currently expanded nodes should be collapsed.
	 */
	public void setExpandable ( boolean expandable , boolean collapseNow ) {
		this.expandable = expandable;

		if ( collapseNow ) {
			for ( TreePath p : expandedPaths ) {
				collapsePath ( p );
			}
		}
	}

	/**
	 * <p>
	 * Sets if nodes containing children should have their children removed when the parent
	 * is collapsed.
	 * </p>
	 * <p>
	 * No cache maps will be cleared.
	 * </p>
	 * @param unload If nodes that are collapsed should have their children removed.
	 */
	public void setUnloadOnCollapse ( boolean unload ) {
		this.unloadCollapsed = unload;
	}

	/**
	 * Sets if right click events should select paths just like left clicks in the
	 * event the tree popup menu has not been assigned. If the menu has been assigned, right
	 * click selecting paths is the default behavior.
	 * @param selects
	 */
	public void setRightClickSelects ( boolean selects ) {
		this.rightClickSelects = selects;
	}
}

