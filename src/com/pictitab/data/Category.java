package com.pictitab.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

	private String name;
	private List<Category> categories;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == CONSTRUCTORS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Default constructor of the Category class.
	 **/
	public Category() {
		this.name = new String("");
		this.categories = new ArrayList<Category>();
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            (String): Category's name.
	 **/
	public Category(String name) {
		this.name = name;
		this.categories = new ArrayList<Category>();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param name
	 *            (String): Category's name.
	 * @param categories
	 *            (List<Category>): Sub-categories name.
	 **/
	public Category(String name, List<Category> categories) {
		this.name = name;
		this.categories = categories;
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == PARCELABLE == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Parcel constructor.
	 * 
	 * @param in
	 *            (Parcel): parcel
	 **/
	public Category(Parcel in) {
		this();
		readFromParcel(in);
	}

	/**
	 * Necessary creator.
	 **/
	public static final Creator<Category> CREATOR = new Creator<Category>() {

		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	/**
	 * Write category's data into parcel.
	 * 
	 * @param name
	 *            (String): Category's name.
	 * @param categories
	 *            (List<Category>): Sub-categories name.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(categories);
	}

	/**
	 * Read the category's data from parcel.
	 * 
	 * @param in
	 *            (Parcel): Parcel
	 **/
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.categories = in.readArrayList(Category.class.getClassLoader());
	}

	/**
	 * Necessary stuff.
	 **/
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == GETTERS & SETTERS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Return the category's name.
	 * 
	 * @return Category's name.
	 **/
	public String getName() {
		return name;
	}

	/**
	 * Set a name of category.
	 * 
	 * @param name
	 *            (String): Category's name.
	 */
	public void rename(String name) {
		this.name = name;
	}

	/**
	 * Return the sub-categories.
	 * 
	 * @return Sub-categories.
	 **/
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 * Set a sub-categories.
	 * 
	 * @param categories
	 *            (List<Category>): Sub-categories.
	 **/
	public void setCategories(List<Category> categories) {
		this.categories.addAll(categories);
	}

	/**
	 * Return the category of index i.
	 * 
	 * @param i
	 *            (int): Index i.
	 * @return Category.
	 **/
	public Category getCategory(int i) {
		return categories.get(i);
	}

	/**
	 * Add a sub-categories to the category instance.
	 * 
	 * @param category
	 *            (<Category>): Sub-category.
	 **/
	public void addCategory(Category category) {
		this.categories.add(category);
	}

	/**
	 * Delete all the sub-categories of the category instance.
	 **/
	public void clearCategories() {
		this.categories.clear();
	}
}
