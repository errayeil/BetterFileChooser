package com.github.errayeil.betterfilechooser.ui.list.File;

import com.github.errayeil.betterfilechooser.Utils.BetterFileUtils;
import com.github.errayeil.betterfilechooser.Utils.BetterStringUtils;
import io.codeworth.panelmatic.PanelMatic;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.errayeil.betterfilechooser.Utils.BetterCompUtils.fill;
import static io.codeworth.panelmatic.componentbehavior.Modifiers.L_END;
import static io.codeworth.panelmatic.util.Groupings.lineGroup;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
class BetterFileListCell extends JPanel {

	/**
	 *
	 */
	private File cellFile;

	/**
	 *
	 */
	private JLabel fileNameLabel;

	/**
	 *
	 */
	private JLabel lastModifiedLabel;

	/**
	 *
	 */
	private JLabel extensionLabel;

	/**
	 *
	 */
	private JLabel fileSizeLabel;

	/**
	 *
	 */
	private JPanel fileStatsPanel;

	/**
	 *
	 */
	protected BetterFileListCell ( ) {

	}

	/**
	 * Constructs a new FinderListCell component.
	 *
	 * @param file          The file we are creating the cell for.
	 * @param showFileStats If the file stats panel should be visible
	 * @param isFocusable   If the cell is focusable. This should be true most of the time.
	 * @param isSelectable  If the cell is selectable. This should be true most of the time.
	 */
	protected BetterFileListCell ( final File file , boolean showFileStats , boolean isFocusable , boolean isSelectable ) {
		cellFile = file;

		fileNameLabel = new JLabel ( );
		lastModifiedLabel = new JLabel ( );
		extensionLabel = new JLabel ( );
		fileSizeLabel = new JLabel ( );
		fileStatsPanel = new JPanel ( );
		Font font = UIManager.getFont ( "Label.font" ).deriveFont ( 24 ); //This doesn't do anything, idk why (yet)

		/*
		 * Mouse adapter to pass mouse events to the FinderList so if one of the FinderCell components
		 * receives an input the FinderList can detect it.
		 */
		MouseAdapter adapter = new MouseAdapter ( ) {
			@Override
			public void mouseClicked ( MouseEvent e ) {
				getParent ( ).dispatchEvent ( e );
			}
		};

		fileNameLabel.addMouseListener ( adapter );
		lastModifiedLabel.addMouseListener ( adapter );
		extensionLabel.addMouseListener ( adapter );
		fileStatsPanel.addMouseListener ( adapter );
		fileSizeLabel.addMouseListener ( adapter );

		fileSizeLabel.setFont ( font );
		fileNameLabel.setFont ( font );
		lastModifiedLabel.setFont ( font );
		extensionLabel.setFont ( font );

		/*
		 * If the file is a directory we want the file type label to say "Folder" and size label to say "Files" instead.
		 */
		if ( file.isDirectory ( ) ) {
			File[] files = file.listFiles ( );
			assert files != null;
			extensionLabel.setText ( "Folder" );
			fileSizeLabel.setText ( files.length + " files" );
		} else {
			fileSizeLabel.setText ( BetterStringUtils.getHumanReadableByteCount ( file.length ( ) ) );
			extensionLabel.setText ( FilenameUtils.getExtension ( file.getName ( ) ) + " file" );
		}

		/**
		 * @TODO: Look into programmatically setting sizing.
		 */
		fileNameLabel.setMinimumSize ( new Dimension ( 400 , 15 ) );
		fileSizeLabel.setMinimumSize ( new Dimension ( 80 , 15 ) );
		extensionLabel.setMinimumSize ( new Dimension ( 80 , 15 ) );
		lastModifiedLabel.setMinimumSize ( new Dimension ( 125 , 15 ) );
		fileNameLabel.setPreferredSize ( new Dimension ( 400 , 15 ) );
		fileSizeLabel.setPreferredSize ( new Dimension ( 80 , 15 ) );
		extensionLabel.setPreferredSize ( new Dimension ( 80 , 15 ) );
		lastModifiedLabel.setPreferredSize ( new Dimension ( 125 , 15 ) );

		fileSizeLabel.setHorizontalAlignment ( SwingConstants.CENTER );
		lastModifiedLabel.setHorizontalAlignment ( SwingConstants.CENTER );
		extensionLabel.setHorizontalAlignment ( SwingConstants.CENTER );

		lastModifiedLabel.setText ( new SimpleDateFormat ( "MM/dd/yyyy h:mm a" ).format ( new Date ( cellFile.lastModified ( ) ) ) );
		fileNameLabel.setText ( file.getName ( ) );
		fileNameLabel.setIcon ( BetterFileUtils.getSystemFileIcon ( file.getAbsolutePath ( ) ) );

		fileStatsPanel.setOpaque ( true );
		fileStatsPanel.setBackground ( new Color ( 0 , 0 , 0 , 0 ) );
		fileStatsPanel.setVisible ( showFileStats );

		setToolTipText ( file.getAbsolutePath ( ) );
		setEnabled ( isSelectable );
		setFocusable ( isFocusable );


		PanelMatic.begin ( fileStatsPanel )
				.add ( lineGroup ( verticalSeparator ( ) ,
						fill ( 5 , 0 ) , lastModifiedLabel ,
						fill ( 5 , 0 ) , verticalSeparator ( ) ,
						fill ( 5 , 0 ) , extensionLabel ,
						fill ( 5 , 0 ) , verticalSeparator ( ) ,
						fill ( 5 , 0 ) , fileSizeLabel ,
						fill ( 5 , 0 ) , verticalSeparator ( ) ,
						fill ( 5 , 0 ) ) , L_END )
				.get ( );


		//GOT DAMN do I love PanelMatic
		PanelMatic.begin ( this )
				.add ( lineGroup ( fileNameLabel , ( JComponent ) Box.createHorizontalGlue ( ) , fileStatsPanel ) )
				.get ( );
	}

	/**
	 * @return
	 */
	protected JSeparator verticalSeparator ( ) {
		JSeparator sep = new JSeparator ( SwingConstants.VERTICAL );
		sep.setBackground ( new Color ( 0 , 0 , 0 , 0 ) );
		return sep;
	}

	/**
	 *
	 */
	protected void setShowFileStats ( boolean showFileStats ) {
		fileStatsPanel.setVisible ( showFileStats );
	}

	/**
	 * @return
	 */
	protected final File getCellFile ( ) {
		return cellFile;
	}
}
