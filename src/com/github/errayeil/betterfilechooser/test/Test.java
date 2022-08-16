package com.github.errayeil.betterfilechooser.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.github.errayeil.betterfilechooser.ui.tree.Root.DriveRootTree;
import io.codeworth.panelmatic.PanelMatic;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		DriveRootTree tree = new DriveRootTree ( );
		JButton button = new JButton ( "HIT ME TO TEST MOTHER FUCKER" );
		JPopupMenu menu = new JPopupMenu (  );

		menu.add ( new JMenuItem ( "Hello There." ) );
		menu.add ( new JMenuItem ( "You were the chosen one, Anakin!" ) );
		menu.add ( new JMenuItem ( "I HATE YOU!!!!" ) );
		menu.add ( new JMenuItem ( "I loved you, you were my brother!" ) );
		menu.add ( new JMenuItem ( "It goes something like that... I can't remember the lines exactly." ) );

		button.addActionListener ( (a) -> {
			tree.setTopRootText ( "Text" );
		} );

		tree.setPopupMenu ( menu );

		tree.setDoubleClickListener ( new MouseAdapter ( ) {
			@Override
			public void mousePressed ( MouseEvent e ) {
				System.out.println ( "Ouch, that hurt!" );
			}
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
