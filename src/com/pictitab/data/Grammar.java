package com.pictitab.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Grammar implements Parcelable {

	private String name; // Grammar name
	private ArrayList<ArrayList<Category>> rules; // Associated rules

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
	 * Default constructor.
	 **/
	public Grammar() {
		this.name = new String("");
		this.rules = new ArrayList<ArrayList<Category>>();
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            (String): Grammars's name.
	 **/
	public Grammar(String name) {
		this.name = name;
		this.rules = new ArrayList<ArrayList<Category>>();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param name
	 *            (String): Grammars's name.
	 * @param rules
	 *            (ArrayList<ArrayList<Category>>): List of rules.
	 **/
	public Grammar(String name, ArrayList<ArrayList<Category>> rules) {
		this.name = name;
		this.rules = rules;
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
	 * Parcel constructor
	 * 
	 * @param in
	 *            (Parcel): parcel
	 **/
	public Grammar(Parcel in) {
		this();
		readFromParcel(in);
	}

	/**
	 * Necessary creator.
	 **/
	public static final Creator<Grammar> CREATOR = new Creator<Grammar>() {

		public Grammar createFromParcel(Parcel source) {
			return new Grammar(source);
		}

		public Grammar[] newArray(int size) {
			return new Grammar[size];
		}
	};

	/**
	 * Write grammar's data into parcel.
	 * 
	 * @param dest
	 *            (Parcel): parcel
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(rules);
	}

	/**
	 * Read grammar's data from parcel
	 * 
	 * @param in
	 *            (Parcel): parcel
	 **/
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.rules = in.readArrayList(Grammar.class.getClassLoader());
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
	 * Return the name of the grammar.
	 * 
	 * @return Name of the grammar.
	 **/
	public String getName() {
		return this.name;
	}

	/**
	 * Rename the grammar.
	 * 
	 * @param newName
	 *            (String): New name.
	 **/
	public void rename(String newName) {
		this.name = newName;
	}

	/**
	 * Return the rules of the grammar.
	 * 
	 * @return Rules.
	 **/
	public ArrayList<ArrayList<Category>> getRules() {
		return this.rules;
	}

	/**
	 * Return the rule of index i.
	 * 
	 * @param i
	 *            (int) : Index.
	 * @return Rule.
	 **/
	public ArrayList<Category> getRuleAt(int i) {
		return rules.get(i);
	}

	/**
	 * Set a rule of index i.
	 * 
	 * @param i
	 *            (int): Index.
	 * @param rule
	 *            (ArrayList<Category>): Rule.
	 **/
	public void setRuleAt(int i, ArrayList<Category> rule) {
		this.rules.set(i, rule);
	}

	/**
	 * Add a rule in the grammar instance.
	 * 
	 * @param rule
	 *            (ArrayList<Category>): Rule.
	 **/
	public void addRule(ArrayList<Category> rule) {
		this.rules.add(rule);
	}

	/**
	 * Delete the rule of index i.
	 * 
	 * @param i
	 *            (int) : Index.
	 **/
	public void deleteRule(int i) {
		rules.remove(i);
	}

	/**
	 * Return the category of index j in the rule of index i.
	 * 
	 * @param i
	 *            (int): Rule's index.
	 * @param j
	 *            (int): Category's index.
	 * @return Category.
	 **/
	public Category getCategoryAt(int i, int j) {
		return this.rules.get(i).get(j);
	}

	/**
	 * Set a category of index j in the rule of index i.
	 * 
	 * @param i
	 *            (int): Rule's index.
	 * @param j
	 *            (int): Category's index.
	 **/
	public void setCategoryAt(int i, int j, Category category) {
		this.rules.get(i).set(j, category);
	}
}
