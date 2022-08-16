package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.ui.tree.Abstract.FileNodeResource;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

import static com.github.errayeil.betterfilechooser.Utils.BetterCompUtils.fill;

/**
 *
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class BRTCellRenderer extends DefaultTreeCellRenderer {

	/**
	 *
	 */
	private JPanel panel;

	/**
	 *
	 */
	private JLabel cellLabel;


	/**
	 *
	 */
	public BRTCellRenderer () {
		panel = new JPanel (  );
		cellLabel = new JLabel (  );

		panel.setOpaque ( false );
		PanelMatic.begin ( panel )
				.add ( fill ( 0 , 0 ) )
				.add ( cellLabel )
				.get ( );
	}

	/**
	 *
	 * @param tree      the receiver is being configured for
	 * @param value     the value to render
	 * @param sel  whether node is selected
	 * @param expanded  whether node is expanded
	 * @param leaf      whether node is a lead node
	 * @param row       row index
	 * @param hasFocus  whether node has focus
	 * @return
	 */
	@Override
	public Component getTreeCellRendererComponent ( JTree tree , Object value , boolean sel , boolean expanded , boolean leaf , int row , boolean hasFocus ) {
		if ( value instanceof BRTNode node ) {
			FileNodeResource obj = node.getNodeResource ();
			cellLabel.setText ( obj.getText () );
			cellLabel.setIcon ( obj.getIcon () );
		}

		return panel;
	}

	/**
	 * @param index
	 *
	 * @return
	 */
	public boolean isEvenRow ( int index ) {
		return ( index & 1 ) == 0;
	}
}
