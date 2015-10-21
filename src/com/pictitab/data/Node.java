package com.pictitab.data;

import java.util.ArrayList;

public class Node {

	private Category category; // The node category
	private int state; // The depth of the category in the current rule
	private ArrayList<Node> transitions; // The transition nodes for the states
											// of the automate
	private Node father; // The parent node of the current node
	private ArrayList<Lexicon> words; // The available words accessible from the
										// current node

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
	 * Default constructor of the Node class.
	 **/
	public Node() {
		this.state = -1;
		this.transitions = new ArrayList<Node>();
		this.father = null;
		this.words = new ArrayList<Lexicon>();
		this.category = null;
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == NODE PROCESS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Return if the current node is a final node (no child).
	 * 
	 * @return true if it is the case, else false.
	 **/
	public boolean isFinal() {
		if (this.transitions.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Return the transition id going to the category with the parameter name.
	 * 
	 * @param catName
	 *            (String): Category parameter name.
	 * @param data
	 *            (AppData): Data.
	 * @return Transition id or -1 if it doesn't exist.
	 **/
	public int haveNextCategory(String catName, AppData data) {
		int res = -1;
		Node tmpNode;

		for (int i = 0; i < this.transitions.size(); i++) {
			tmpNode = this.transitions.get(i);
			if (tmpNode.getCategory().getName().equals(catName)) {
				res = i;
				return res;
			} else {
				if (this.testSubCategory(tmpNode.getCategory(), catName, data)) {
					res = i;
					return res;
				}
			}
		}
		return res;
	}

	/**
	 * Return if the node have available transitions toward other children
	 * categories.
	 * 
	 * @param catName
	 *            (String): Category name.
	 * @param data
	 *            (AppData): Data.
	 * @return true if it is possible, else false.
	 **/
	public boolean haveNextCategoryInFirstRules(String catName, AppData data) {
		if (this.category.getName().equals(catName)) {
			return true;
		} else {
			if (this.testSubCategory(this.category, catName, data)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return if a category is a child of an another category.
	 * 
	 * @param cat
	 *            (Category): Category.
	 * @param catName
	 *            (String): Category name.
	 * @param data
	 *            (AppData): Data.
	 * @return true if the second category is a sub-category of the first
	 *         category else false.
	 **/
	private boolean testSubCategory(Category cat, String catName, AppData data) {
		Category trueCat = data.getCategories().get(
				data.getCategoryByName(cat.getName()));
		for (int i = 0; i < trueCat.getCategories().size(); i++) {
			Category tmpCat = trueCat.getCategories().get(i);
			if (tmpCat.getName().equals(catName)) {
				return true;
			} else {
				if (this.testSubCategory(tmpCat, catName, data)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Return the nex node of a transition.
	 * 
	 * @param i
	 *            (int): Transition.
	 * @return Node.
	 **/
	public Node getNextTransition(int i) {
		return this.transitions.get(i);
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
	 * Return the node category.
	 * 
	 * @return Node category.
	 **/
	public Category getCategory() {
		return this.category;
	}

	/**
	 * Set a node category.
	 * 
	 * @param newCategory
	 *            (Category): Category.
	 **/
	public void setCategory(Category newCategory) {
		this.category = newCategory;
	}

	/**
	 * Return the depth of the category in the current rule.
	 * 
	 * @return Depth.
	 **/
	public int getState() {
		return state;
	}

	/**
	 * Set the depth of the category in the current rule.
	 * 
	 * @param state
	 *            (int): Depth.
	 **/
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Return the transition nodes.
	 * 
	 * @return Transition nodes.
	 **/
	public ArrayList<Node> getTransitions() {
		return transitions;
	}

	/**
	 * Set the transition nodes.
	 * 
	 * @param transitions
	 *            (ArrayList<Node>): Transition nodes.
	 **/
	public void setTransitions(ArrayList<Node> transitions) {
		this.transitions = transitions;
	}

	/**
	 * Return the parent node of the current node.
	 * 
	 * @return Parent node.
	 **/
	public Node getFather() {
		return father;
	}

	/**
	 * Set the parent node of the current node.
	 * 
	 * @param newFather
	 *            (Node): Parent node.
	 **/
	public void setFather(Node newFather) {
		this.father = newFather;
	}

	/**
	 * Return the accessible words from the current node.
	 * 
	 * @return Accessible words.
	 **/
	public ArrayList<Lexicon> getWords() {
		return words;
	}

	/**
	 * Set the accessible words from the current node.
	 * 
	 * @param words
	 *            (ArrayList<Lexicon>): Accessible words.
	 **/
	public void setWords(ArrayList<Lexicon> words) {
		this.words = words;
	}
}