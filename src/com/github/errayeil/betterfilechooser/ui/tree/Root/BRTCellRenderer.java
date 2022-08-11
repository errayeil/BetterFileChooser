package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.github.errayeil.betterfilechooser.ui.tree.Root.Objects.INodeObject;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
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
	 * The panel that contains the JLabel.
	 * TODO: More components I have planned.
	 */
	private JPanel panel;

	/**
	 * The JLabel that displays the file name.
	 */
	private JLabel cellLabel;

	/**
	 *
	 */
	public BRTCellRenderer () {

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
		panel = new JPanel (  );
		cellLabel = new JLabel ( );

		if ( value instanceof BRTNode node ) {
			INodeObject no = node.getNodeObject ();

			cellLabel.setText ( no.getText () );
			cellLabel.setIcon ( no.getIcon () );
		}

		panel.setOpaque ( false );

		PanelMatic.begin ( panel )
				.add ( fill ( 0 , 0) )
				.add ( cellLabel )
				.get (  );

		return panel;
	}
}
