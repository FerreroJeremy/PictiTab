package com.pictitab.utils;

import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;

public class UITools {
	
	/**
	 * Define the column number of a GridLayout according to the screen size.
	 * @param display(Display): Display
	 * @param orientation(int): Code of the orientation
	 * @param pictureWidth(int): Width of the pictures
	 * @param padding(int): Padding
	 * @return Column number in the grid
	 */
	public static int getNbColumn(Display display, int orientation, int pictureWitdth, int padding) {
		int nbCols;
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		// Column number according to the orientation of the screen.
		if(orientation==Configuration.ORIENTATION_PORTRAIT) {
			nbCols = (int) Math.ceil(width / (pictureWitdth + 2*padding) - 1);
		}
		else {
			nbCols = (int) Math.ceil(height / (pictureWitdth + 2*padding) - 1);
		}
		return nbCols;
	}
	
	/**
	 * Define the new padding between each pictures.
	 * @param display(Display): Display
	 * @param orientation(int): Code of the orientation
	 * @param pictureWidth(int): Width of the pictures
	 * @param nbCols(int): Column number in the grid
	 * @return Padding
	 */
	public static int getNewPadding(Display display, int orientation, int pictureWidth, int nbCols) {
		int newPadding;
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		// Column number according to the orientation of the screen.
		if(orientation==Configuration.ORIENTATION_PORTRAIT) {
			newPadding = (width - (nbCols * pictureWidth)) / (2*nbCols);
		}
		else {
			newPadding = (height - (nbCols * pictureWidth)) / (2*nbCols);
		}
		
		return newPadding;
	}
	
	/**
	 * Return the width of the screen.
	 * @param display(Display): Display of the screen.
	 * @return Width of the screen.
	 */
	public static int getWidth(Display display) {
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
}
