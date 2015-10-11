package com.pictitab.utils;

import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;

public class UITools {
	
	/**
	 * Methode permettant de definir le nombre de colonnes d'un GridLayout d'images
	 * en fonction de la taille de l'ecran.
	 * @param display(Display): L'ecran dans lequel sera affiche le GridLayout
	 * @param orientation(int): Code indiquant le mode d'orientation de l'image
	 * @param pictureWidth(int): Largeur des images
	 * @param padding(int): La marge entre les images
	 * @return Le nombre de colonnes du GridLayout
	 */
	public static int getNbColumn(Display display, int orientation, int pictureWitdth, int padding) {
		int nbCols;
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		// On defini le nombre de colonnes selon l'orientation de l'ecran.
		if(orientation==Configuration.ORIENTATION_PORTRAIT) {
			nbCols = (int) Math.ceil(width / (pictureWitdth + 2*padding) - 1);
		}
		else {
			nbCols = (int) Math.ceil(height / (pictureWitdth + 2*padding) - 1);
		}
		return nbCols;
	}
	
	/**
	 * Methode permettant de definir le nouveau padding entre les images.
	 * Elles seront ainsi toutes separees par une marge egale en fonction de la taille de l'ecran.
	 * @param display(Display): L'ecran dans lequel sera affiche le GridLayout
	 * @param orientation(int): Code indiquant le mode d'orientation de l'image
	 * @param pictureWidth(int): Largeur des images
	 * @param nbCols(int): Le nombre de colonnes de la grille
	 * @return La valeur du nouveau padding
	 */
	public static int getNewPadding(Display display, int orientation, int pictureWidth, int nbCols) {
		int newPadding;
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		// On defini le nombre de colonnes selon l'orientation de l'ecran.
		if(orientation==Configuration.ORIENTATION_PORTRAIT) {
			newPadding = (width - (nbCols * pictureWidth)) / (2*nbCols);
		}
		else {
			newPadding = (height - (nbCols * pictureWidth)) / (2*nbCols);
		}
		
		return newPadding;
	}
	
	/**
	 * Methode permettant de recuperer la largeur de l'ecran.
	 * @param display(Display): Le display de l'ecran.
	 * @return La largeur de l'ecran.
	 */
	public static int getWidth(Display display) {
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
}
