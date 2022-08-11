package com.github.errayeil.betterfilechooser.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.github.errayeil.betterfilechooser.ui.tree.Root.RootTree;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.*;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class Test {

	/**
	 *
	 * @param args
	 */
	public static void main ( String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel ( new FlatDarkLaf () );

		JDialog dialog = new JDialog (  );
		JPanel contentPane = new JPanel (  );
		RootTree tree = new RootTree ( );
		JButton button = new JButton ( "Change view" );

		tree.setExpandable ( false );

		button.addActionListener ( (a) -> {
			tree.setViewMode ( RootTree.FOLDERS_AND_FILES );
		} );

		PanelMatic.begin ( contentPane )
						.add ( new JScrollPane ( tree ) )
								.add ( button );


		dialog.setContentPane ( contentPane );
		dialog.setModal ( true );
		dialog.setLocationRelativeTo ( null );

		dialog.pack ();
		dialog.setVisible ( true );
	}
}
