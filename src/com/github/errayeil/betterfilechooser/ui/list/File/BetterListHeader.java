package com.github.errayeil.betterfilechooser.ui.list.File;


import com.github.errayeil.betterfilechooser.ui.Filter.BetterFilter;
import com.github.errayeil.betterfilechooser.ui.Sort.BetterSort;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static com.github.errayeil.betterfilechooser.Utils.BetterCompUtils.fill;
import static com.github.errayeil.betterfilechooser.Utils.BetterCompUtils.verticalSeparator;
import static io.codeworth.panelmatic.componentbehavior.Modifiers.L_END;
import static io.codeworth.panelmatic.util.Groupings.lineGroup;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BetterListHeader extends JPanel {

	/**
	 *
	 */
	private final BetterFileList list;

	/**
	 *
	 */
	private final JPanel fileStatsPanel;

	/**
	 *
	 */
	private JPopupMenu popup;

	/**
	 *
	 */
	private BetterFilter[] filters;

	/**
	 * @TODO: Fix this mess
	 * <br>
	 * So uncivilized
	 * <br>
	 * For some reason, retrieving the scrollbar width returns 0 so the offset doesn't work.
	 */
	protected BetterListHeader ( final BetterFileList list , int scrollBarOffset ) {
		this.list = list;
		fileStatsPanel = new JPanel ( );

		JLabel fileNameLabel = new JLabel ( "File name" , SwingConstants.CENTER );
		JLabel lastModifiedLabel = new JLabel ( "Last Modified" , SwingConstants.CENTER );
		JLabel extensionLabel = new JLabel ( "Type" , SwingConstants.CENTER );
		JLabel fileSizeLabel = new JLabel ( "Size" , SwingConstants.CENTER );
		JButton viewButton = new JButton ( "..." );

		viewButton.addMouseListener ( new MouseAdapter ( ) {
			@Override
			public void mouseClicked ( MouseEvent e ) {
				if ( popup != null ) {
					if ( !popup.isShowing ( ) ) {
						popup.show ( viewButton , viewButton.getX ( ) + ( viewButton.getWidth ( ) / 2 ) , viewButton.getY ( ) + viewButton.getHeight ( ) );
					} else {
						popup.setVisible ( false );
					}
				}
			}
		} );

		fileNameLabel.setMinimumSize ( new Dimension ( 489 , 15 ) );
		fileSizeLabel.setMinimumSize ( new Dimension ( 80 , 15 ) );
		extensionLabel.setMinimumSize ( new Dimension ( 80 , 15 ) );
		lastModifiedLabel.setMinimumSize ( new Dimension ( 125 , 15 ) );
		fileNameLabel.setPreferredSize ( new Dimension ( 489 , 15 ) );
		fileSizeLabel.setPreferredSize ( new Dimension ( 80 , 15 ) );
		extensionLabel.setPreferredSize ( new Dimension ( 80 , 15 ) );
		lastModifiedLabel.setPreferredSize ( new Dimension ( 125 , 15 ) );

		viewButton.setOpaque ( true );
		popup = new BetterListHeaderPopup ( this , null ).getPopup ( );

		fileStatsPanel.setOpaque ( true );
		fileStatsPanel.setBackground ( new Color ( 0 , 0 , 0 , 0 ) );
		fileStatsPanel.setVisible ( list.isShowingStats ( ) );

		setEnabled ( false );
		setFocusable ( false );

		PanelMatic.begin ( fileStatsPanel )
				.add ( lineGroup ( verticalSeparator ( ) ,
								fill ( 5 , 0 ) , lastModifiedLabel ,
								fill ( 5 , 0 ) , verticalSeparator ( ) ,
								fill ( 5 , 0 ) , extensionLabel ,
								fill ( 5 , 0 ) , verticalSeparator ( ) ,
								fill ( 5 , 0 ) , fileSizeLabel ,
								fill ( 5 , 0 ) , verticalSeparator ( ) ,
								fill ( 6 + scrollBarOffset , 0 ) ) ,
						L_END )
				.get ( );

		PanelMatic.begin ( this )
				.add ( lineGroup ( viewButton , fill ( 5 , 0 ) , fileNameLabel , ( JComponent ) Box.createHorizontalGlue ( ) , fileStatsPanel ) )
				.get ( );
	}

	/**
	 *
	 * @param filter
	 */
	protected void addFinderFilter ( final BetterFilter filter ) {

	}

	/**
	 * @param filter
	 */
	protected void setActiveFilter ( BetterFilter filter ) {
		list.setFilter ( filter );
	}

	/**
	 * @param sort
	 */
	protected void setActiveSort ( BetterSort<File> sort ) {
		list.setSort ( sort );
	}

	/**
	 * @param filters
	 */
	protected void setFinderFilters ( BetterFilter[] filters ) {
		this.filters = filters;
	}

	/**
	 * @param popup
	 */
	protected void setButtonPopup ( JPopupMenu popup ) {
		this.popup = popup;
	}

	/**
	 * Shows the file stats panel in the header and also tells its FinderList
	 * child to show the individual cell files.
	 */
	protected void setShowFileStats ( boolean showFileStats ) {
		//This work but doesn't seem right. My brain isn't working 100% right now.
		if ( showFileStats ) {
			if ( !fileStatsPanel.isVisible ( ) ) {
				fileStatsPanel.setVisible ( showFileStats );
				list.setShowFileStats ( showFileStats );
			}
		} else {
			if ( fileStatsPanel.isVisible ( ) ) {
				fileStatsPanel.setVisible ( showFileStats );
				list.setShowFileStats ( showFileStats );
			}
		}
	}

	/**
	 * Tells its FinderList child to show hidden files.
	 *
	 * @param showHidden
	 */
	protected void setShowHidden ( boolean showHidden ) {
		list.setShowHidden ( showHidden );
	}

	/**
	 * @return
	 */
	protected boolean isShowingHidden ( ) {
		return list.isShowingHidden ( );
	}

	/**
	 * @return
	 */
	protected boolean isShowingFileStats ( ) {
		return list.isShowingStats ( );
	}

	/**
	 * @return
	 */
	protected BetterFilter getActiveFilter ( ) {
		return list.getActiveFilter ( );
	}

	/**
	 *
	 * @return
	 */
	protected BetterSort getActiveSort ( ) {
		return list.getActiveSort ();
	}

}
