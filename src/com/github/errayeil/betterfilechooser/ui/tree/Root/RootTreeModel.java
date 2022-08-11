package com.github.errayeil.betterfilechooser.ui.tree.Root;

import com.formdev.flatlaf.ui.FlatListCellBorder.Default;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public class RootTreeModel extends DefaultTreeModel {

	public RootTreeModel ( TreeNode root ) {
		super ( root );
	}

	@Override
	public Object getChild ( Object parent , int index ) {
		return super.getChild ( parent , index );
	}
}
