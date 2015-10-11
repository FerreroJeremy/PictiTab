package com.pictitab.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Lexicon implements Parcelable {
	
	// Les informations relatives a l'entree du lexique
	private String word;			// Le nom de l'entree du lexique
	private String pictureSource;	// L'image associee
	private Category category;		// La categorie choisie
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Lexicon. **/
	public Lexicon() {
		this.word =new String("");
		this.pictureSource =new String("");
		this.category =new Category();
	}
	
	/**
	 * Constructeur de la classe Lexicon.
	 * @param mot(String): Mot du lexique.
	 * @param source(String): Source de l'image du lexique.
	 * @param categorie(Category): Categorie a laquelle appartient ce lexique.
	 */
	public Lexicon(String mot, String source, Category category) {
		this.word = mot;
		this.pictureSource = source;
		this.category = category;
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Constructeur de la classe Lexicon utilisant Parcel
	 * @param in(Parcel): Le paquet dans lequel sont contenues les donnees
	 **/
	public Lexicon(Parcel in) {
		this();
		readFromParcel(in);
	}
	
	/** Les classes qui implementent l'interface Parcelable doivent aussi avoir un champ statique appele CREATOR. **/
	public static final Creator<Lexicon> CREATOR = new Creator<Lexicon>() {

		public Lexicon createFromParcel(Parcel source) {
			return new Lexicon(source);
		}
		
		public Lexicon[] newArray(int size) {
			return new Lexicon[size];
		}
	};
	
	/**
	 * Ecriture des differents types de donnees de la classe Lexicon
	 * @param dest(Parcel): Le paquet dans lequel sont ecrites les donnees
	 * @param flags(): Des indicateurs supplementaires sur la façon dont l'objet doit etre ecrit.
	 * Peut être 0 ou PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.word);
		dest.writeString(this.pictureSource);
		dest.writeParcelable(this.category, 0);
	}
	
	/**
	 * Lecture des differents types de donnees de la classe Lexicon 
	 * @param in(Parcel): Le paquet depuis lequel sont lues les donnees
	 **/
	private void readFromParcel(Parcel in) {
		this.word = in.readString();
		this.pictureSource = in.readString();
		this.category = in.readParcelable(Category.class.getClassLoader());
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
	 * Methode retournant le nom de l'entree du lexique.
	 * @return le nom de l'entree du lexique
	 */
	public String getWord() {
		return this.word;
	}
	
	/**
	 * Renseigne le nom de l'entree du lexique.
	 * @param newWord(String): Le nom.
	 **/
	public void setWord(String newWord) {
		this.word = newWord;
	}
	
	/**
	 * Methode retournant le nom de l'entree du lexique.
	 * @return le nom de limage pour le lexique.
	 */
	public String getPictureSource() {
		return this.pictureSource;
	}
	
	/**
	 * Renseigne la source de l'image de l'entree du lexique.
	 * @param newWord(String): La source de l'image.
	 **/
	public void setPictureSource(String newSource) {
		this.pictureSource = newSource;
	}
	
	/**
	 * Methode retournant la categorie de l'entree du lexique.
	 * @return La categorie de l'entree du lexique.
	 */
	public Category getCategory() {
		return this.category;
	}
	
	/**
	 * Renseigne la categorie de l'entree du lexique.
	 * @param newWord(String): La categorie.
	 **/
	public void setCategory(Category newCat) {
		this.category = newCat;
	}
}
