package com.pictitab.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Lexicon implements Parcelable {
	
	private String word;
	private String pictureSource;
	private Category category;
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTORS                                                 ==*/
	/*====================================================================================================================*/
	
	/** 
     * Default constructor.
     **/
	public Lexicon() {
		this.word =new String("");
		this.pictureSource =new String("");
		this.category =new Category();
	}
	
	/**
	 * Copy constructor.
	 * @param mot(String): word.
	 * @param source(String): picture.
	 * @param categorie(Category): category.
	 **/
	public Lexicon(String mot, String source, Category category) {
		this.word = mot;
		this.pictureSource = source;
		this.category = category;
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Parcel constructor.
	 * @param in(Parcel): parcel
	 **/
	public Lexicon(Parcel in) {
		this();
		readFromParcel(in);
	}
	
    /**
     * Necessary creator.
     **/
	public static final Creator<Lexicon> CREATOR = new Creator<Lexicon>() {

		public Lexicon createFromParcel(Parcel source) {
			return new Lexicon(source);
		}
		
		public Lexicon[] newArray(int size) {
			return new Lexicon[size];
		}
	};
	
    /**
     * Write the different data of the class AppData
     * @param dest(Parcel): Data in package
     * @param flags(): supplementary information about the writing.
     * May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
     **/
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.word);
		dest.writeString(this.pictureSource);
		dest.writeParcelable(this.category, 0);
	}
	
    /**
    * Read the different data of the class AppData
    * @param in(Parcel): Data in package
    **/
	private void readFromParcel(Parcel in) {
		this.word = in.readString();
		this.pictureSource = in.readString();
		this.category = in.readParcelable(Category.class.getClassLoader());
	}

    /**
     * Necessary stuff.
     **/
	@Override
	public int describeContents() {
		return 0;
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Return the name of the lexicon entry.
	 * @return Name
	 **/
	public String getWord() {
		return this.word;
	}
	
	/**
	 * Set a name.
	 * @param newWord(String): Name
	 **/
	public void setWord(String newWord) {
		this.word = newWord;
	}
	
	/**
	 * Return the picture's name.
	 * @return Picture's name
	 **/
	public String getPictureSource() {
		return this.pictureSource;
	}
	
	/**
	 * Set a picture's name.
	 * @param newWord(String): Picture's name
	 **/
	public void setPictureSource(String newSource) {
		this.pictureSource = newSource;
	}
	
	/**
	 * Return the category.
	 * @return Category
	 **/
	public Category getCategory() {
		return this.category;
	}
	
	/**
	 * Set a category.
	 * @param newWord(String): Category
	 **/
	public void setCategory(Category newCat) {
		this.category = newCat;
	}
}
