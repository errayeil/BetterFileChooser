package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.Preferences.Registry;
import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.Utils.Resources;
import com.github.errayeil.betterfilechooser.ui.Listener.ClickListener;
import com.github.errayeil.betterfilechooser.ui.tree.Abstract.FileComm;
import com.github.errayeil.betterfilechooser.ui.tree.Abstract.FileNodeResource;
import com.github.errayeil.betterfilechooser.ui.tree.BetterTree;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.*;

import static com.github.errayeil.betterfilechooser.Utils.BetterFileUtils.*;

/**
 * Creates a system storage drive root tree. Currently, this will only show folders and automatically does not show
 * hidden folders.
 *
 * @author Errayeil
 * @version 0.1
 * @TODO Provide ability to set custom loading icon
 * @TODO Provide ability to set custom icons for nodes
 * @since 0.1
 */
public class DriveRootTree extends BetterTree implements FileComm {

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
	 * <p>
	 * A map containing icons that should be used for specific
	 * extensions. If this map does not contain an extension for a
	 * file the default system icon will be used..
	 * </p>
	 * <br>
	 * Key: File extension
	 * <br>
	 * Value: The icon used for the extension.
	 */
	private final Map<String, Icon> fileIcons;

	/**
	 * <p>
	 * A map containing icons that should be used for specific folders.
	 * If this map does not contain an icon for a folder the default
	 * system icon will be used.
	 * </p>
	 * <br>
	 * Key: The file that will use the custom Icon.
	 * <br>
	 * Value: The icon for the folder.
	 */
	private final Map<File, Icon> folderIcons;

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
	 * The mouse listener that is sent events on double click.
	 */
	private MouseListener doubleClickListener;

	/**
	 * The icon used when the tree is loading
	 * a selected node's children.
	 */
	private Icon loadingIcon;

	/**
	 * The icon that is used for the root node.
	 */
	private Icon rootIcon;

	/**
	 * The icon that is used for the desktop node.
	 */
	private Icon desktopIcon;

	/**
	 * The icon that is used for drive nodes.
	 */
	private Icon driveIcon;

	/**
	 * The icon that is used for folder nodes.
	 */
	private Icon folderIcon;

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
	 * Boolean flag to determine if the desktop path should be displayed in the tree.
	 */
	private boolean showDesktopFolder;

	/**
	 * Boolean flag to determine if an expansion events should be carried out.
	 * If set to false, this essentially makes the RootTree as a shortcut medium
	 * for the storage drives.
	 */
	private boolean allowExpansion;

	/**
	 * Boolean flag to determine if a nodes children should be removed when
	 * it is collapsed. This is by default set to true.
	 */
	private boolean unloadCollapsed = true;

	/**
	 * Boolean flag to determine if there is no TreePopup currently assigned that if
	 * right clicks should select the path just like left clicks would.
	 */
	private boolean rightClickSelect = false;

	/**
	 * Integer codes to help determine the file view mode of the RootTree.
	 * This will be moved from this class to BetterFileChooser, as the integer codes
	 * will be the same.
	 */
	public static int FOLDERS_ONLY = -1;
	public static int FOLDERS_AND_FILES = 0;

	/**
	 *
	 */
	private BRTNodeType nodeTypes;


	/**
	 * Constructs a RootTree with the default roots and sets the view mode and hidden file
	 * visibility to the provided parameters.
	 */
	public DriveRootTree ( ) {
		this.viewMode = 0; //registry.getTreeInt ( Keys.treeViewModeKey );
		this.showHidden = false; //registry.getTreeBoolean ( Keys.treeShowHiddenKey );
		this.showDesktopFolder = false;//registry.getTreeBoolean ( Keys.rTreeShowDesktopKey );
		this.allowExpansion = true; //registry.getTreeBoolean ( Keys.rTreeAllowExpansionKey );

		topRootNode = new BRTNode ( new BRTNodeResource ( "This PC" , getSystemFileIcon ( getDesktopFile ( ) ) , BRTNodeType.ROOT_NODE ) );
		currentModel = new DefaultTreeModel ( topRootNode );
		driveNodesList = new ArrayList<> ( );
		hiddenNodes = new HashMap<> ( );
		dsNodes = new HashMap<> ( );
		cachedNodes = new HashMap<> ( );
		fileIcons = new HashMap<> ( );
		folderIcons = new HashMap<> ( );
		expandedPaths = new HashSet<> ( );

		this.rootIcon = getSystemFileIcon ( getDesktopFile ( ) );
		this.desktopIcon = rootIcon;

		for ( Path p : listDeviceRoots ( ) ) {
			File f = p.toFile ( );

			if ( driveIcon == null ) {
				driveIcon = getSystemFileIcon ( f );
			}

			BRTNodeResource res = new BRTNodeResource ( f , getSystemDisplayName ( f ) , driveIcon , BRTNodeType.DRIVE_NODE );
			driveNodesList.add ( new BRTNode ( res ) );
		}

		if ( showDesktopFolder ) {
			topRootNode.add ( new BRTNode ( new BRTNodeResource ( getDesktopFile ( ) , "Desktop" , rootIcon , BRTNodeType.DESKTOP_NODE ) ) );
		}

		for ( BRTNode n : driveNodesList ) {
			topRootNode.add ( n );
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
		/*
		 * Implements a custom mouse listener to differentiate being single or double clicks.
		 * Because of the way this is handled, this can add a noticeable delay since it utilizes
		 * the OS double-click speed property. The slower that property is set to the greater
		 * the delay.
		 * @TODO: See if I can improve the delay somehow.
		 */
		addMouseListener ( new ClickListener ( ) {

			@Override
			public void singleClick ( MouseEvent e ) {
				TreePath path = getPathForLocation ( e.getX ( ) , e.getY ( ) );

				if ( SwingUtilities.isLeftMouseButton ( e ) ) {
					if ( path != null ) {
						setSelectionPath ( path );
					}

					if ( DriveRootTree.this.allowExpansion ) {
						if ( path != null ) {
							BRTNode node = ( BRTNode ) path.getLastPathComponent ( );
							if ( !isExpanded ( path ) ) {
								if ( node != null ) {
									FileNodeResource res = node.getNodeResource ( );

									if ( !res.getNodeType ( ).equals ( "ROOT" ) ) {
										selectedFile = node.getNodeResource ( ).getFile ( );
										selectedNode = node;
										folderIcon = getSystemFileIcon ( selectedFile );

										SwingUtilities.invokeLater ( ( ) -> {

											res.setIcon ( Resources.getLoadingIcon ( ) );
											selectedNode.setUserObject ( res );
											currentModel.nodeChanged ( selectedNode );
										} );

										createChildren ( selectedNode , res.getFile ( ) );
									}
								}
							}
						}
					}
				} else if ( SwingUtilities.isRightMouseButton ( e ) ) {
					if ( treePopup != null ) {
						if ( path != null ) {
							SwingUtilities.invokeLater ( ( ) -> {
								setSelectionPath ( path );
								//TODO fix popup showing before path selection
								treePopup.show ( DriveRootTree.this , e.getX ( ) , e.getY ( ) );
							} );
						}
					} else {
						if ( rightClickSelect ) {
							if ( path != null ) {
								SwingUtilities.invokeLater ( ( ) -> {
									setSelectionPath ( path );
								} );
							}
						}
					}
				}
			}

			@Override
			public void doubleClick ( MouseEvent e ) {
				if ( SwingUtilities.isLeftMouseButton ( e ) ) {
					if ( doubleClickListener != null ) {
						fireDoubleClickEvent ( );
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
	private synchronized void removeChildren ( @NotNull final BRTNode parentNode ) {
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
	private void createChildren ( @NotNull BRTNode parentNode , @NotNull File directory ) {
		FileNodeResource res = parentNode.getNodeResource ( );
		List<BRTNode> childNodes = new ArrayList<> ( );

		SwingWorker worker = new SwingWorker ( ) {

			@Override
			protected Object doInBackground ( ) throws Exception {
				if ( directory != null && directory.isDirectory ( ) ) {
					File[] files = directory.listFiles ( );

					if ( files != null ) {
						for ( File f : files ) {
							BRTNode childNode = new BRTNode ( new BRTNodeResource ( f , getSystemDisplayName ( f ) , getFileIcon ( f ) , BRTNodeType.FILE_NODE ) );
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

				res.setIcon ( getFileIcon ( res.getFile ( ) ) );
				parentNode.setUserObject ( res );
				currentModel.nodeChanged ( parentNode );
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
	private synchronized void sortChildren ( @NotNull List<BRTNode> children ) {
		children.sort ( ( ( Comparator<BRTNode> ) ( o1 , o2 ) -> {
			File f1 = o1.getNodeResource ( ).getFile ( );
			File f2 = o2.getNodeResource ( ).getFile ( );

			if ( f1.isDirectory ( ) && f2.isFile ( ) ) {
				return -1;
			} else if ( f1.isDirectory ( ) && f2.isDirectory ( ) ) {
				return 1;
			} else if ( f1.isFile ( ) && f2.isFile ( ) ) {
				return 0;
			}

			return 1;
		} ).thenComparing ( ( o1 , o2 ) -> {
			File f1 = o1.getNodeResource ( ).getFile ( );
			File f2 = o2.getNodeResource ( ).getFile ( );

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
	private synchronized void filterChildren ( @NotNull BRTNode parentNode , @NotNull List<BRTNode> childNodes ) {
		List<BRTNode> toRemove = new ArrayList<> ( );
		for ( BRTNode n : childNodes ) {
			File f = n.getNodeResource ( ).getFile ( );

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
					File f = n.getNodeResource ( ).getFile ( );

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
	private void updateSelectedVisibleNodes ( @NotNull BRTNode selectedNode ) {
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

				if ( !n.getNodeResource ( ).getNodeType ( ).equals ( "ROOT" ) ) {

					if ( !n.getNodeResource ( ).getNodeType ( ).equals ( "DRIVE" ) ) {

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
				FileNodeResource obj = node.getNodeResource ( );

				//Make sure the last path node isn't the root node.
				if ( node.getNodeResource ( ).getNodeType ( ).equals ( "ROOT" ) ) {
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
		MouseEvent event = new MouseEvent ( this ,
				MouseEvent.MOUSE_PRESSED ,
				System.currentTimeMillis ( ) ,
				0 , mp.x - cp.x ,
				mp.y - cp.y ,
				2 ,
				false );

		doubleClickListener.mousePressed ( event );
	}

	/**
	 * @return
	 */
	@Override
	public JPopupMenu getComponentPopupMenu ( ) {
		return treePopup;
	}

	/**
	 * @return
	 */
	public MouseListener getDoubleClickListener ( ) {
		return doubleClickListener;
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
	public final File getSelectedFile ( ) {
		return selectedFile;
	}

	/**
	 * @return
	 */
	public final String getRootText ( ) {
		return topRootNode.getNodeResource ( ).getText ( );
	}

	/**
	 * Returns a list of the drives currently visible in the three.
	 */
	public final List<File> getListedDrives ( ) {
		List<File> roots = new ArrayList<> ( );
		for ( BRTNode n : driveNodesList ) {
			roots.add ( n.getNodeResource ( ).getFile ( ) );
		}
		return roots;
	}


	/**
	 * @return
	 */
	public @Unmodifiable
	final int getCurrentViewMode ( ) {
		return viewMode;
	}

	/**
	 * Returns if this RootTree nodes can be expanded.
	 *
	 * @return
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	public boolean isAllowExpansion ( ) {
		return allowExpansion;
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
	 * @return
	 */
	public boolean isShowingHidden ( ) {
		return showHidden;
	}

	/**
	 * @return
	 */
	public boolean isRightClickSelectEnabled ( ) {
		return rightClickSelect;
	}

	/**
	 * @return
	 */
	public boolean isShowingDesktopFolder ( ) {
		return showDesktopFolder;
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
	 * Does nothing, use setPopupMenu instead.
	 *
	 * @version 0.1
	 * @since 0.1
	 */
	@Override
	public void setComponentPopupMenu ( JPopupMenu popup ) {

	}

	/**
	 * @param file
	 *
	 * @return
	 */
	@Override
	public @NotNull Icon getFileIcon ( @NotNull File file ) {

		if ( file.isDirectory ( ) ) {
			if ( folderIcons.containsKey ( file ) ) {
				return folderIcons.get ( file );
			}
		} else if ( file.isFile ( ) ) {
			if ( fileIcons.containsKey ( file ) ) {
				return fileIcons.get ( file );
			}
		}


		return getSystemFileIcon ( file );
	}

	/**
	 * @param file
	 *
	 * @return
	 */
	@Override
	public String getFileExtension ( File file ) {
		//For now this will utilize Apache file utils, in the future I will migrate away from that.
		return FilenameUtils.getExtension ( file.getName ( ) );
	}

	/**
	 * @param file
	 * @param includeAll
	 *
	 * @return
	 */
	@Override
	public String getFileExtension ( File file , boolean includeAll ) {
		return FilenameUtils.getExtension ( file.getName ( ) );
	}

	/**
	 * @param popup
	 */
	public void setPopupMenu ( JPopupMenu popup ) {
		if ( popup != null ) {
			treePopup = popup;
		} else {
			treePopup = null;
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
		SwingUtilities.invokeLater ( ( ) -> {
			FileNodeResource obj = topRootNode.getNodeResource ( );
			obj.setText ( text );
			topRootNode.setUserObject ( obj );
			currentModel.nodeChanged ( topRootNode );
		} );
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
	public void setTopRootIcon ( @NotNull final Icon icon ) {
		SwingUtilities.invokeLater ( ( ) -> {
			BRTNodeResource res = ( BRTNodeResource ) topRootNode.getNodeResource ( );
			res.setIcon ( icon );
			topRootNode.setUserObject ( res );
			currentModel.nodeChanged ( topRootNode );
		} );
	}

	/**
	 * Sets the loading icon.
	 *
	 * @param icon
	 *
	 * @apiNote Not functional yet
	 */
	public void setLoadingIcon ( @NotNull final Icon icon ) {
		this.loadingIcon = icon;
	}

	/**
	 * Sets the icon that should be displayed on the drive nodes.
	 *
	 * @param icon
	 *
	 * @apiNote Not functional yet
	 */
	public void setDriveIcon ( @NotNull final Icon icon ) {
		this.driveIcon = icon;

		for ( int i = 0; i < topRootNode.getChildCount ( ); i++ ) {
			BRTNode node = ( BRTNode ) topRootNode.getChildAt ( i );
			BRTNodeResource res = ( BRTNodeResource ) node.getNodeResource ( );

			if ( res.getNodeType ( ).equals ( "DRIVE" ) ) {
				res.setIcon ( icon );
				node.setUserObject ( res );
				SwingUtilities.invokeLater ( ( ) -> {
					currentModel.nodeChanged ( node );
				} );
			}
		}
	}

	/**
	 * Sets the icon for the specific drive provided, provided the drive
	 * is visible within the tree.
	 *
	 * @param icon
	 * @param driveFile
	 *
	 * @apiNote Not functional yet
	 */
	public void setDriveIcon ( @NotNull final Icon icon , @NotNull File driveFile ) {
		this.driveIcon = icon;

		for ( int i = 0; i < topRootNode.getChildCount ( ); i++ ) {
			BRTNode node = ( BRTNode ) topRootNode.getChildAt ( i );
			BRTNodeResource res = ( BRTNodeResource ) topRootNode.getChildAt ( i );

			if ( res.getNodeType ( ).equals ( "DRIVE" ) ) {
				File nodeFile = res.getFile ( );

				if ( nodeFile != null ) {
					if ( nodeFile == driveFile ) {
						res.setIcon ( icon );
						node.setUserObject ( res );
						currentModel.nodeChanged ( node );
						break; //Break the loop early
					}
				}
			}
		}
	}

	/**
	 * Sets the icon that should be displayed on folder tree nodes, going forward..
	 *
	 * @param icon
	 *
	 * @apiNote Not functional yet
	 */
	public void setFolderIcon ( @NotNull final Icon icon ) {
		this.folderIcon = icon;
	}

	/**
	 * Sets the icon for the specified folder. For example, this could be
	 * the desktop folder. This will update that folder immediately.
	 *
	 * @param icon
	 * @param folder
	 *
	 * @apiNote Not Functional yet
	 */
	public void setFolderIcon ( @NotNull final Icon icon , @NotNull File folder ) {
		folderIcons.put ( folder , icon );

		SwingWorker worker = new SwingWorker ( ) {

			BRTNode node;

			@Override
			protected Object doInBackground ( ) throws Exception {
				node = crawl ( topRootNode );

				return null;
			}

			@Override
			protected void done ( ) {
				if ( node != null ) {
					FileNodeResource res = node.getNodeResource ( );
					res.setIcon ( icon );

					node.setUserObject ( res );
					currentModel.nodeChanged ( node );
				}
			}

			/**
			 * Crawls the tree for a node that represents the provided folder.
			 * If one is not found, this returns null. If this returns null then that folder
			 * is not yet visible in the tree.
			 * @param parent
			 * @return Returns the BRTNode needed, if it is not found this will return null.
			 */
			private @Nullable BRTNode crawl ( @NotNull BRTNode parent ) {
				int count = parent.getChildCount ( );

				if ( count > 0 ) { //Quick check to make sure it has children
					for ( int i = 0; i < count; i++ ) {
						BRTNode child = ( BRTNode ) parent.getChildAt ( i );
						FileNodeResource res = child.getNodeResource ( );
						File resFile = res.getFile ( );

						if ( resFile == folder ) {
							return child;
						} else {
							crawl ( child );
						}
					}
				}

				return null;
			}
		};

		worker.execute ( );
	}

	/**
	 * Sets the icon for files with the provided extension. This will
	 * update all currently loaded file nodes.
	 * This is useful for if you want to use your own icons for the tree.
	 *
	 * @param extension
	 * @param icon
	 */
	public void setFileIcon ( @NotNull final Icon icon , @NotNull final String extension ) {
		fileIcons.put ( extension , icon );

		SwingWorker worker = new SwingWorker ( ) {

			List<BRTNode> nodes;

			@Override
			protected Object doInBackground ( ) throws Exception {
				nodes = crawl ( topRootNode );

				return null;
			}

			@Override
			protected void done ( ) {
				for ( BRTNode n : nodes ) {
					FileNodeResource res = n.getNodeResource ( );
					res.setIcon ( icon );
					currentModel.nodeChanged ( n );
				}
			}

			/**
			 * Crawls the tree for nodes that contains the provided extension.
			 * @param parent
			 * @return Returns a list of BRTNodes that should be updated.
			 */
			private @NotNull List<BRTNode> crawl ( BRTNode parent ) {
				int count = parent.getChildCount ( );
				List<BRTNode> nodesToUpdate = new ArrayList<> ( );

				if ( count > 0 ) { //Quick check to make sure it has children, it should, but yanno, why not?
					for ( int i = 0; i < count; i++ ) {
						BRTNode child = ( BRTNode ) parent.getChildAt ( i );
						FileNodeResource res = child.getNodeResource ( );
						File resFile = res.getFile ( );

						if ( resFile.isFile ( ) ) {
							String ext = getFileExtension ( resFile );

							if ( ext.equals ( extension ) ) {
								nodesToUpdate.add ( child );
							}
						} else if ( resFile.isDirectory ( ) ) {
							crawl ( child );
						}
					}
				}

				return nodesToUpdate;
			}
		};

		worker.execute ( );
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
		if ( viewMode == -1 || viewMode == 0 ) {
			throw new InvalidParameterException ( "Integer provided is not a valid view mode code." );
		} else {
			this.viewMode = viewMode;
			registry.addTreeInt ( Keys.treeViewModeKey , viewMode );

			startViewUpdate ( );
		}
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
	 * Sets the showHidden flag and viewMode to the specified values.
	 *
	 * @param showHidden
	 * @param viewMode
	 */
	public void setHiddenAndViewMode ( boolean showHidden , int viewMode ) {
		if ( viewMode == -1 || viewMode == 0 ) {
			throw new InvalidParameterException ( "Integer provided is not a valid view mode code." );
		}

		this.showHidden = showHidden;
		this.viewMode = viewMode;
		registry.addTreeBoolean ( Keys.treeShowHiddenKey , showHidden );
		registry.addTreeInt ( Keys.treeViewModeKey , viewMode );

		startViewUpdate ( );
	}

	/**
	 * Sets if the desktop folder should be visible.
	 *
	 * @param showDesktop
	 */
	public void setShowDesktopFolder ( boolean showDesktop ) {
		this.showDesktopFolder = showDesktop;
		registry.addTreeBoolean ( Keys.rTreeShowDesktopKey , showDesktop );

		SwingUtilities.invokeLater ( ( ) -> {
			if ( showDesktop ) {
				BRTNode node = ( BRTNode ) topRootNode.getChildAt ( 0 );

				if ( !node.getNodeResource ( ).getNodeType ( ).equals ( "DESKTOP" ) ) {
					topRootNode.insert ( new BRTNode ( new BRTNodeResource ( getDesktopFile ( ) , "Desktop" , getSystemFileIcon ( getDesktopFile ( ) ) , BRTNodeType.DESKTOP_NODE ) ) , 0 );
				}

			} else {
				topRootNode.remove ( 0 );
			}

			currentModel.reload ( topRootNode );
		} );
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
		this.allowExpansion = expandable;

		SwingUtilities.invokeLater ( ( ) -> {
			if ( collapseNow ) {
				for ( TreePath p : expandedPaths ) {
					collapsePath ( p );
				}
			}
		} );
	}

	/**
	 * <p>
	 * Sets if nodes containing children should have their children removed when the parent
	 * is collapsed.
	 * </p>
	 * <p>
	 * No cache maps will be cleared.
	 * </p>
	 *
	 * @param unload If nodes that are collapsed should have their children removed.
	 */
	public void setUnloadOnCollapse ( boolean unload ) {
		this.unloadCollapsed = unload;
	}

	/**
	 * Sets if right click events should select paths just like left clicks in the
	 * event the tree popup menu has not been assigned. If the menu has been assigned, right
	 * click selecting paths is the default behavior.
	 *
	 * @param selects
	 */
	public void setRightClickSelect ( boolean selects ) {
		this.rightClickSelect = selects;
	}
}

