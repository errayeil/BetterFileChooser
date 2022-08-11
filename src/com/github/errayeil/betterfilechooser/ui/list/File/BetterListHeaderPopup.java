package com.github.errayeil.betterfilechooser.ui.list.File;

import com.github.errayeil.betterfilechooser.Preferences.Registry.Keys;
import com.github.errayeil.betterfilechooser.ui.Filter.BetterFilter;
import com.github.errayeil.betterfilechooser.ui.Sort.*;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BetterListHeaderPopup {

	/**
	 *
	 */
	private final BetterListHeader header;

	/**
	 *
	 */
	private final JPopupMenu menu;

	/**
	 *
	 */
	private final JMenu filterMenu;

	/**
	 *
	 */
	private final JMenu viewMenu;

	/**
	 *
	 */
	private final JMenu sortMenu;

	/**
	 * @param header           The FinderListHeader that contains the JButton that displays this popup.
	 * @param availableFilters An array of FinderFilters that will be available to the FinderList.
	 */
	public BetterListHeaderPopup ( BetterListHeader header , BetterFilter[] availableFilters ) {
		this.header = header;
		menu = new JPopupMenu ( );
		filterMenu = new JMenu ( "Filter" );
		viewMenu = new JMenu ( "View" );
		sortMenu = new JMenu ( "Sort" );

		JCheckBoxMenuItem allItem = new JCheckBoxMenuItem ( "All Files" );
		JCheckBoxMenuItem showHiddenItem = new JCheckBoxMenuItem ( "Show Hidden Files" );
		JCheckBoxMenuItem showFileStatsItem = new JCheckBoxMenuItem ( "Show File Stats" );

		JCheckBoxMenuItem defaultItem = new JCheckBoxMenuItem ( "Default" );
		JCheckBoxMenuItem sizeDescendItem = new JCheckBoxMenuItem ( "Largest to Smallest" );
		JCheckBoxMenuItem sizeAscendItem = new JCheckBoxMenuItem ( "Smallest to Largest" );
		JCheckBoxMenuItem nameDescendItem = new JCheckBoxMenuItem ( "A to Z" );
		JCheckBoxMenuItem nameAscendItem = new JCheckBoxMenuItem ( "Z to A" );
		JCheckBoxMenuItem filesFirstItem = new JCheckBoxMenuItem ( "Files First" );
		JCheckBoxMenuItem foldersFirstItem = new JCheckBoxMenuItem ( "Folders First" );

//		allItem.setName ( Keys.allFilterKey );
//		gdfItem.setName ( Keys.gdfFilterKey );
//		textItem.setName ( Keys.ptfFilterKey );
//		sizeDescendItem.setName ( Keys.sortSizeDescendKey );
//		sizeAscendItem.setName ( Keys.sortSizeAscendKey );
//		nameDescendItem.setName ( Keys.sortNameDescendKey );
//		nameAscendItem.setName ( Keys.sortNameAscendKey );
//		filesFirstItem.setName ( Keys.sortFilesKey );
//		foldersFirstItem.setName ( Keys.sortFoldersKey );

		//TODO: Condense, this is NOT the way..... (yet)
		defaultItem.addActionListener ( e -> {
			header.setActiveSort ( null ); //TODO change to all filter
			deselectItem ( sortMenu.getMenuComponents ( ) , defaultItem );
		} );
		sizeDescendItem.addActionListener ( e -> {
			header.setActiveSort ( new SLSort () );
			deselectItem ( sortMenu.getMenuComponents ( ) , sizeDescendItem );
		} );
		sizeAscendItem.addActionListener ( e -> {
			header.setActiveSort ( new LSSort ( ) );
			deselectItem ( sortMenu.getMenuComponents ( ) , sizeAscendItem );
		} );
		nameDescendItem.addActionListener ( e -> {
			header.setActiveSort ( new AZSort ( ) );
			deselectItem ( sortMenu.getMenuComponents ( ) , nameDescendItem );
		} );
		nameAscendItem.addActionListener ( e -> {
			header.setActiveSort ( new ZASort ( ) );
			deselectItem ( sortMenu.getMenuComponents ( ) , nameAscendItem );
		} );
		filesFirstItem.addActionListener ( e -> {
			header.setActiveSort ( new FilesFirstSort ( ).thenComparing ( new AZSort ( ) ) );
			deselectItem ( sortMenu.getMenuComponents ( ) , filesFirstItem );
		} );
		foldersFirstItem.addActionListener ( e -> {
			header.setActiveSort ( new FoldersFirstSort ( ).thenComparing ( new AZSort ( ) ) );
			deselectItem ( sortMenu.getMenuComponents ( ) , foldersFirstItem );
		} );
		showHiddenItem.addActionListener ( e -> {
			header.setShowHidden ( showHiddenItem.isSelected ( ) );
		} );
		showFileStatsItem.addActionListener ( e -> {
			header.setShowFileStats ( showFileStatsItem.isSelected ( ) );
		} );

		allItem.addActionListener ( e -> {
			header.setActiveFilter ( null );
			deselectItem ( filterMenu.getMenuComponents ( ) , allItem );
		} );


		filterMenu.add ( allItem );
		filterMenu.addSeparator ( );
		filterMenu.addSeparator ( );
		createFilterItems ( availableFilters );

		viewMenu.add ( showHiddenItem );
		viewMenu.add ( showFileStatsItem );

		sortMenu.add ( defaultItem );
		sortMenu.addSeparator ( );
		sortMenu.add ( sizeDescendItem );
		sortMenu.add ( sizeAscendItem );
		sortMenu.addSeparator ( );
		sortMenu.add ( nameDescendItem );
		sortMenu.add ( nameAscendItem );
		sortMenu.addSeparator ( );
		sortMenu.add ( filesFirstItem );
		sortMenu.add ( foldersFirstItem );

		menu.add ( filterMenu );
		menu.add ( viewMenu );
		menu.add ( sortMenu );

		setItemSelected ( filterMenu.getMenuComponents ( ) , header.getActiveFilter ( ) );
		setItemSelected ( sortMenu.getMenuComponents ( ) , header.getActiveSort ());
		showHiddenItem.setSelected ( header.isShowingHidden ( ) );
		showFileStatsItem.setSelected ( header.isShowingFileStats ( ) );
	}

	/**
	 * @return
	 */
	public JPopupMenu getPopup ( ) {
		return menu;
	}

	/**
	 * Adds the provided FinderFilter to the filter menu.
	 *
	 * @param filter
	 */
	protected void addFinderFilter ( final BetterFilter filter ) {
		createFilterItem ( filter );
	}

	/**
	 * Creates and adds a JCheckBoxMenuItem to the filter menu.
	 * <br>
	 * TODO: Insert into the menu based off alphabetical order.
	 *
	 * @param filter
	 */
	private void createFilterItem ( final BetterFilter filter ) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem ( );
		item.setText ( filter.getDescription ( ) );
		item.setName ( filter.getName ( ) );

		item.addActionListener ( e -> {
			header.setActiveFilter ( filter );
		} );
		item.addActionListener ( e -> {
			deselectItem ( filterMenu.getMenuComponents ( ) , item );
		} );

		filterMenu.addSeparator ( );
		filterMenu.add ( item );
	}

	/**
	 * Creates all the JCheckBoxMenuItems needed from the filters array.
	 *
	 * @param filters
	 */
	private void createFilterItems ( BetterFilter[] filters ) {
		//Sort the list first so the filters are alphabetized.
		List<BetterFilter> sorted = new ArrayList<> ( Arrays.asList ( filters ) );
		sorted.sort ( ( o1 , o2 ) -> String.CASE_INSENSITIVE_ORDER.compare ( o1.getDescription ( ) , o2.getDescription ( ) ) );

		for ( BetterFilter filter : sorted ) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem ( );
			item.setText ( filter.getDescription ( ) );
			item.setName ( filter.getName ( ) );

			item.addActionListener ( e -> {
				header.setActiveFilter ( filter );
				deselectItem ( filterMenu.getMenuComponents ( ) , item );
			} );

			filterMenu.add ( item );
		}
	}

	/**
	 * Sets the correct JCheckBoxMenuItem to a selected state based off the
	 * provided filter. This is used for persistence purposes.
	 *
	 * @param filter
	 */
	private void setItemSelected ( Component[] menuComponents , BetterFilter filter ) {
		for ( Component comp : menuComponents ) {
			if ( comp instanceof JCheckBoxMenuItem ) {
				String name = comp.getName ( );
				if ( name != null ) {
					if ( filter != null ) {
						if ( comp.getName ( ).equals ( filter.getName ( ) ) ) {
							( ( JCheckBoxMenuItem ) comp ).setSelected ( true );
						}
					} else {
						if ( comp.getName ( ).equals ( Keys.filterEverythingKey ) ) {
							( ( JCheckBoxMenuItem ) comp ).setSelected ( true );
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * @param menuComponents
	 * @param sort
	 */
	private void setItemSelected ( Component[] menuComponents , BetterSort sort ) {
		for ( Component comp : menuComponents ) {
			if ( comp instanceof JCheckBoxMenuItem ) {
				String name = comp.getName ( );
				if ( name != null ) {
					if ( sort != null ) {
						if ( comp.getName ( ).equals ( sort.getName ( ) ) ) {
							( ( JCheckBoxMenuItem ) comp ).setSelected ( true );
						}
					} else {
						if ( comp.getName ( ).equals ( Keys.sortFoldersKey ) ) {
							( ( JCheckBoxMenuItem ) comp ).setSelected ( true );
						}
					}
				}
			}
		}
	}

	/**
	 * Deselects any other selected JCheckBoxMenuItem's in the menu.
	 *
	 * @param source
	 */
	private void deselectItem ( Component[] menuComponents , JCheckBoxMenuItem source ) {
		for ( Component comp : menuComponents ) {
			if ( comp instanceof JCheckBoxMenuItem ) {
				if ( comp != source ) {
					( ( JCheckBoxMenuItem ) comp ).setSelected ( false );
				}
			}
		}
	}
}
