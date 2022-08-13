package com.github.errayeil.betterfilechooser.File;

/**
 * @author Errayeil
 * @version 0.1
 * @since 0.1
 */
public interface UIFileActions {

	boolean deleteSelectedFile();

	boolean moveSelectedFileToTrash();

	boolean copySelectedFile();

	boolean renameSelectedFile();
}
