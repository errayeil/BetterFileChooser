package com.github.errayeil.betterfilechooser.ui.tree.Root.Objects;

import com.github.errayeil.betterfilechooser.ui.tree.Root.BRTNode;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static com.github.errayeil.betterfilechooser.Utils.BetterCompUtils.fill;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BRTCell extends JPanel {

	private JLabel cellLabel;

	/**
	 *
	 */
	private Icon fileIcon;

	/**
	 *
	 */
	private ImageIcon loadingIcon;

	/**
	 * @param node
	 */
	public BRTCell ( BRTNode node ) {
		cellLabel = new JLabel (  );

		final INodeObject no = node.getNodeObject ( );
		fileIcon = no.icon ( );
		//loadingIcon = new ImageIcon ( BRTCell.class.getResource ( "/com/github/errayeil/betterfilechooser/Resources/loading.gif" ) );

		cellLabel.setText ( no.getText ( ) );
		cellLabel.setIcon ( fileIcon );

		setOpaque ( false );

		PanelMatic.begin ( this )
				.add ( fill ( 0 , 0 ) )
				.add ( cellLabel )
				.get ( );
	}

	/**
	 * @param loading
	 */
	public void setIsLoading ( boolean loading ) {
		if ( loading ) {
			cellLabel.setIcon ( loadingIcon );
		} else {
			cellLabel.setIcon ( fileIcon );
		}
	}
}
