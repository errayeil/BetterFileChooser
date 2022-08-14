package com.github.errayeil.betterfilechooser.Preferences;

import com.github.errayeil.betterfilechooser.ui.Chooser.BetterFileChooser;
import com.github.errayeil.betterfilechooser.ui.list.BetterList;
import com.github.errayeil.betterfilechooser.ui.list.File.BetterFileList;
import com.github.errayeil.betterfilechooser.ui.tree.BetterTree;
import com.github.errayeil.betterfilechooser.ui.tree.Root.DriveRootTree;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public final class Registry implements PreferenceChangeListener {

	/**
	 * Various String keys used to more easily add and retrieve preference values
	 * from the Registry.
	 *
	 * @author Errayeil
	 * @version 0.1
	 * @since 0.1
	 */
	public static class Keys {

		public static final String viewModeBothKey = "ViewMode.foldersAndFiles";
		public static final String viewModeFolderKey = "ViewMode.foldersOnly";
		public static final String viewModeFileKey = "ViewMode.filesOnly";

		public static final String sortAZKey = "Sort.sortAtoZ";
		public static final String sortZAKey = "Sort.sortZtoA";
		public static final String sortLargestKey = "Sort.largestFirst";
		public static final String sortSmallestKey = "Sort.smallestFirst";
		public static final String sortFoldersKey = "Sort.showFoldersFist";
		public static final String sortFilesKey = "Sort.showFilesFirst";
		public static final String sortCustomKey = "Sort.customSort";

		public static final String filterEverythingKey = "Filter.allFiles";
		public static final String filterPTFKey = "Filter.allPlainTextFiles";
		public static final String filterImageKey = "Filter.allImageFiles";
		public static final String filterMSKey = "Filter.allMicrosoftOfficeFiles";
		public static final String filterAudioKey = "Filter.allAudioFiles";
		public static final String filterSourceKey = "Filter.allSourceFiles";
		public static final String filterCustomKey = "Filter.customFilter";

		public static final String listSelectModeKey = "List.selectionMode";
		public static final String listShowHiddenKey = "List.showHidden";
		public static final String listShowStatsKey = "List.showStats";
		public static final String listViewModeKey = "List.viewMode";
		public static final String listSortKey = "List.activeSort";
		public static final String listFilterKey = "List.activeFilter";

		public static final String treeSelectModeKey = "Tree.selectionMode";
		public static final String treeShowHiddenKey = "Tree.showHidden";
		public static final String treeShowStatsKey = "Tree.showStats";
		public static final String treeViewModeKey = "Tree.viewMode";
		public static final String treeSortKey = "Tree.activeSort";
		public static final String treeFilterKey = "Tree.activeFilter";
		public static final String treeAutoExpandKey = "Tree.autoExpand";

		public static final String rTreeShowDesktopKey = "RTree.showDesktop";
		public static final String rTreeHideDriveKey = "RTree.hideDrive";
		public static final String rTreeAllowExpansionKey = "RTree.allowExpansion";

		public static final String spTreeDesktopKey = "SPTree.showDesktop";
		public static final String spTreeDocumentKey = "SPTree.showDocuments";
		public static final String spTreeDownloadKey = "SPTree.showDownloads";
		public static final String spTreeRecentsKey = "SPTree.showRecents";

		public static final String chooserCurrentDirKey = "Chooser.currentDirectory";
		public static final String chooserHiddenKey = "Chooser.showHidden";
		public static final String chooserSidepaneKey = "Chooser.showSidepane";
		public static final String chooserPreviewKey = "Chooser.showPreviewPane";
		public static final String chooserShortcutKey = "Chooser.showShortcuts";
		public static final String chooserGlobalSortKey = "Chooser.activeGlobalSort";
		public static final String chooserGlobalFilterKey = "Chooser.activeGlobalFilter";

		public static final String chooserRecentsKey = "Chooser.recentlySelectedFiles";
		public static final String separator = "-!-";

		public static final String chooserOnTopKey = "Chooser.alwaysOnTop";
		public static final String chooserXCoordKey = "Chooser.xCoordinate";
		public static final String chooserYCoordKey = "Chooser.yCoordinate";
		public static final String chooserWidthKey = "Chooser.width";
		public static final String chooserHeightKey = "Chooser.height";

	}

	/**
	 * The instance of this Registry class.
	 */
	private static Registry instance;

	/**
	 * The Preference node for the BetterFileList.
	 */
	private Preferences listPrefs;

	/**
	 * The Preference node for the BetterFileTree.
	 */
	private Preferences treePrefs;

	/**
	 * The Preference node for the BetterFileChooser.
	 */
	private Preferences chooserPrefs;

	/**
	 * A list of PreferenceChangeListeners for the BetterFileList
	 * preferences node.
	 */
	private List<PreferenceChangeListener> listListeners;

	/**
	 * A list of PreferenceChangeListeners for the BetterFileTree
	 * preferences node.
	 */
	private List<PreferenceChangeListener> treeListeners;

	/**
	 * A list of PreferenceChangeListeners for the BetterFileChooser
	 * preference node.
	 */
	private List<PreferenceChangeListener> chooserListeners;

	/**
	 * boolean flag to determine if the preference nodes
	 * should be flushed.
	 */
	private boolean allowUpdates;

	/**
	 * The default return value if a key does not have a value to return.
	 */
	private boolean defaultBool = false;

	/**
	 * The default return value if a key does not have a value to return.
	 */
	private String defaultStr = "";

	/**
	 *
	 */
	private int defaultInt = 0;

	/**
	 *
	 */
	private String[] defaultStrArray;

	/**
	 *
	 */
	private byte[] defaultBytes;


	/**
	 *
	 */
	private Registry ( ) {
		listPrefs = Preferences.userNodeForPackage ( BetterList.class );
		treePrefs = Preferences.userNodeForPackage ( BetterTree.class );
		chooserPrefs = Preferences.userNodeForPackage ( BetterFileChooser.class );

		listPrefs.addPreferenceChangeListener ( this );
		treePrefs.addPreferenceChangeListener ( this );
		chooserPrefs.addPreferenceChangeListener ( this );
	}

	/**
	 * Returns the instance of the registry.
	 *
	 * @return
	 */
	public static Registry instance ( ) {
		if ( instance == null ) {
			instance = new Registry ( );
		}

		return instance;
	}

	/**
	 * Allows you to override the default Preference user nodes. Currently, BetterList.class, BetterTree.class, and
	 * BetterFileChooser.class all have Preference user nodes created from them. Any subclasses such
	 * as BetterFileList and RootTree, for example, utilize those Preference nodes in conjunction. If you would like
	 * to utilize your own Preference user node but keep the Registry functionality you can call this to override
	 * any one of the three Preferences.
	 *
	 * @param packageClass
	 * @param newPreferences
	 *
	 * @throws InvalidParameterException Thrown when the provided Class type is not of BetterList, BetterTree, or BetterFileChooser
	 * <i>or</i> if the specified preferences is null.
	 */
	public void overrideRegistry ( Class<?> packageClass , Preferences newPreferences ) throws InvalidParameterException {
		if ( newPreferences == null ) {
			throw new InvalidParameterException ( "The provided Preferences object is null" );
		}

		if ( packageClass == BetterList.class ) {
			listPrefs = newPreferences;
		} else if ( packageClass == BetterTree.class ) {
			treePrefs = newPreferences;
		} else if ( packageClass == BetterFileChooser.class ) {
			chooserPrefs = newPreferences;
		} else {
			throw new InvalidParameterException ( "The class could not be identified as a valid parameter." );
		}
	}

	/**
	 * @param packageClass  The class used for the Preference node we need to listen to.
	 * @param listenerToAdd
	 */
	public void addPreferenceTreeListener ( Class<?> packageClass , PreferenceChangeListener listenerToAdd ) {
		if ( packageClass == BetterFileList.class ) {
			if ( listListeners == null ) {
				listListeners = new ArrayList<> ( );
			}

			listListeners.add ( listenerToAdd );
		} else if ( packageClass == DriveRootTree.class ) {
			if ( treeListeners == null ) {
				treeListeners = new ArrayList<> ( );
			}

			treeListeners.add ( listenerToAdd );
		} else if ( packageClass == BetterFileChooser.class ) {
			if ( chooserListeners == null ) {
				chooserListeners = new ArrayList<> ( );
			}

			chooserListeners.add ( listenerToAdd );
		}
	}

	/**
	 * @param packageClass
	 * @param listenerToRemove
	 */
	public void removePreferenceChangeListener ( Class<?> packageClass , PreferenceChangeListener listenerToRemove ) {
		if ( packageClass == BetterFileList.class ) {
			if ( listListeners != null ) {
				listListeners.remove ( listenerToRemove );
			}
		} else if ( packageClass == DriveRootTree.class ) {
			if ( treeListeners != null ) {
				treeListeners.remove ( listenerToRemove );
			}
		} else if ( packageClass == BetterFileChooser.class ) {
			if ( chooserListeners != null ) {
				chooserListeners.remove ( listenerToRemove );
			}
		}
	}

	/**
	 * Sets the default return boolean in the event retrieving a value for a key
	 * does not have an entry registered.
	 *
	 * @param defaultBool
	 */
	public void setDefaultReturnValue ( boolean defaultBool ) {
		this.defaultBool = defaultBool;
	}

	/**
	 * Sets the default return String in the event retrieving a value for a key
	 * does not have an entry registered.
	 * <p>Note: Passing a null value will cause this method to do nothing.</p>
	 *
	 * @param defaultStr
	 */
	public void setDefaultReturnValue ( String defaultStr ) {
		if (defaultStr == null )
			return;
		this.defaultStr = defaultStr;
	}

	/**
	 * Sets the default return String in the event retrieving a value for a key
	 * does not have an entry registered.
	 *
	 * @param defaultInt
	 */
	public void setDefaultReturnValue ( int defaultInt ) {
		this.defaultInt = defaultInt;
	}

	/**
	 * Sets the default return byte array in the event retrieving a value for a key
	 * does not have a value registered.
	 * <p>Note: Providing a null value will cause this method to do nothing.</p>
	 * @param array
	 */
	public void setDefaultReturnValue ( byte[] array ) {
		if (array == null)
			return;

		this.defaultBytes = array;
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return Returns true if this action was successful.
	 */
	public boolean addListBoolean ( String key , boolean value ) {
		listPrefs.putBoolean ( key , value );
		return push ( listPrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return Returns true if this action was successful.
	 */
	public boolean addListString ( String key , String value ) {
		listPrefs.put ( key , value );
		return push ( listPrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public boolean addTreeBoolean ( String key , boolean value ) {
		treePrefs.putBoolean ( key , value );
		return push ( treePrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public boolean addTreeString ( String key , String value ) {
		treePrefs.put ( key , value );
		return push ( treePrefs );
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addTreeInt ( String key , int value ) {
		treePrefs.putInt ( key , value );
		return push ( treePrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public boolean addChooserBoolean ( String key , boolean value ) {
		chooserPrefs.putBoolean ( key , value );
		return push ( chooserPrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public boolean addChooserString ( String key , String value ) {
		chooserPrefs.put ( key , value );
		return push ( chooserPrefs );
	}

	/**
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public boolean addChooserInt ( String key , int value ) {
		chooserPrefs.putInt ( key , value );
		return push ( chooserPrefs );
	}

	/**
	 * @param key
	 * @param array
	 *
	 * @return
	 */
	public boolean addChooserArray ( String key , byte[] array ) {
		chooserPrefs.putByteArray ( key , array );
		return push ( chooserPrefs );
	}

	/**
	 * @param key
	 * @param array
	 *
	 * @return
	 */
	public boolean addChooserArray ( String key , String[] array ) {

		return push ( chooserPrefs );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public boolean getListBoolean ( String key ) {
		return listPrefs.getBoolean ( key , defaultBool );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public String getListString ( String key ) {
		return listPrefs.get ( key , defaultStr );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public boolean getTreeBoolean ( String key ) {
		return treePrefs.getBoolean ( key , defaultBool );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public String getTreeString ( String key ) {
		return treePrefs.get ( key , defaultStr );
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public int getTreeInt( String key ) {
		return treePrefs.getInt ( key, defaultInt );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public boolean getChooserBoolean ( String key ) {
		return chooserPrefs.getBoolean ( key , defaultBool );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public String getChooserString ( String key ) {
		return chooserPrefs.get ( key , defaultStr );
	}

	/**
	 * @param key
	 *
	 * @return
	 */
	public int getChooserInt ( String key ) {
		return chooserPrefs.getInt ( key , defaultInt );
	}

	/**
	 * Stores the specified byte array to the chooser preferences node.
	 *
	 * @param key
	 *
	 * @return
	 */
	public byte[] getChooserByteArray ( String key ) {
		return chooserPrefs.getByteArray ( key , defaultBytes );
	}

	/**
	 * Returns a stored byte array that has been converted to a String array.
	 *
	 * @param key
	 *
	 * @return
	 */
	public String[] getChooserStringArray ( String key ) {
		//TODO
		return null;
	}


	/**
	 * @param prefs
	 *
	 * @return Returns true if the store flush was successful, false otherwise.
	 */
	private boolean push ( Preferences prefs ) {
		if ( allowUpdates ) {
			try {
				prefs.flush ( );
				return true;
			} catch ( BackingStoreException e ) {
				//TODO log
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @param evt A PreferenceChangeEvent object describing the event source
	 *            and the preference that has changed.
	 */
	@Override
	public void preferenceChange ( PreferenceChangeEvent evt ) {
		if ( evt.getNode ( ) == listPrefs ) {
			if ( listListeners != null ) {
				for ( PreferenceChangeListener l : listListeners ) {
					l.preferenceChange ( evt );
				}
			}
		} else if ( evt.getNode ( ) == treePrefs ) {
			if ( treeListeners != null ) {
				for ( PreferenceChangeListener l : treeListeners ) {
					l.preferenceChange ( evt );
				}
			}
		} else if ( evt.getNode ( ) == chooserPrefs ) {
			if ( chooserListeners != null ) {
				for ( PreferenceChangeListener l : chooserListeners ) {
					l.preferenceChange ( evt );
				}
			}
		}
	}
}
