package com.pictitab.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

	private String name;
	private List<Category> categories;
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Category. **/
	public Category() {
		this.name = new String("");
		this.categories = new ArrayList<Category>();
	}
	
	/**
	 * Constructeur de la classe Category.
	 * @param name(String): Nom de la categorie.
	 */
	public Category(String name) {
		this.name = name;
		this.categories = new ArrayList<Category>();
	}
	
	/**
	 * Constructeur de la classe Category.
	 * @param name(String): Nom de la categorie.
	 * @param categories(List<Category>): Liste des sous-categories.
	 */
	public Category(String name, List<Category> categories) {
		this.name = name;
		this.categories = categories;
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Constructeur de la classe Category utilisant Parcel
	 * @param in(Parcel): Le paquet dans lequel sont contenues les donnees
	 **/
	public Category(Parcel in) {
		this();
		readFromParcel(in);
	}
	
	/** Les classes qui implementent l'interface Parcelable doivent aussi avoir un champ statique appele CREATOR. **/
	public static final Creator<Category> CREATOR = new Creator<Category>() {

		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}
		
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
	
	/**
	 * Ecriture des differents types de donnees de la classe Category
	 * @param dest(Parcel): Le paquet dans lequel sont ecrites les donnees
	 * @param flags(): Des indicateurs supplementaires sur la façon dont l'objet doit etre ecrit.
	 * Peut etre 0 ou PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(categories);
	}

	/**
	 * Lecture des differents types de donnees de la classe Category 
	 * @param in(Parcel): Le paquet depuis lequel sont lues les donnees
	 **/
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.name       = in.readString();
		this.categories = in.readArrayList(Category.class.getClassLoader());
	}
	
	/**
	 * Description des types d'objets speciaux contenus dans la représentation compressee de ce parcelable.
	 * @return Le type des objets speciaux
	 **/
	@Override
	public int describeContents() {
		return 0;
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Renvoie le nom de la categorie.
	 * @return Le nom de la categorie.
	 **/
	public String getName() {
		return name;
	}
	
	/**
	 * Renomme la categorie.
	 * @param name(String): Le nouveau nom de la categorie.
	 */
	public void rename(String name) {
		this.name = name;
	}
	
	/**
	 * Renvoie les sous-categories.
	 * @return Les sous-categories.
	 **/
	public List<Category> getCategories() {
		return categories;
	}
	
	/**
	 * Renseigne les sous-categories.
	 * @param categories(List<Category>): Les sous-categories.
	 **/
	public void setCategories(List<Category> categories) {
		this.categories.addAll(categories);
	}
	
	/**
	 * Renvoie la sous-categorie d'indice i.
	 * @param i(int): L'indice de la sous-categorie.
	 * @return La sous-categorie d'indice i.
	 **/
	public Category getCategory(int i) {
		return categories.get(i);
	}
	
	/**
	 * Ajoute une sous-categorie a une categorie.
	 * @param category(<Category>): La sous-categorie.
	 **/
	public void addCategory(Category category) {
		this.categories.add(category);
	}
	
	/** Supprime les sous-categories. **/
	public void clearCategories() {
		this.categories.clear();
	}
}
