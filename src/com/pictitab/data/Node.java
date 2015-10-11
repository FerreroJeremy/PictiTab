package com.pictitab.data;

import java.util.ArrayList;

public class Node {
	
	// Les informations relatives a un noeud de l'automate
	private Category category;				// La categorie du noeud
	private int state;						// La profondeur de la categorie dans la règle courante
	private ArrayList<Node> transitions;	// Les noeuds de transition d'un etat a un autre dans l'automate
	private Node father;					// Le noeud pere du noeud courant
	private ArrayList<Lexicon> words;		// Les mots accessibles depuis le noeud
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	public Node() {
		this.state =-1;
		this.transitions =new ArrayList<Node>();
		this.father =null;
		this.words =new ArrayList<Lexicon>();
		this.category =null;
	}
	
	/*====================================================================================================================*/
	/*==												 TRAITEMENTS NOEUD												==*/
	/*====================================================================================================================*/
	
	/**
	 * Methode indiquant si le noeud courant n'a pas de fils (Noeud final).
	 * @return "true" si ce dernier n'a aucun fils OU "false" si ce n'est pas le cas.
	 */
	public boolean isFinal() {
		if(this.transitions.size()==0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Methode renvoyant le numero de la transition allant vers la categorie dont le nom est passe en argument.
	 * @param catName(String): Le nom de la categorie dans laquelle on souhaite aller.
	 * @param data(AppData): Les donnees de l'application.
	 * @return le numero de la transition allant vers la categorie passe en argument OU -1 si aucune n'y va.
	 */
	public int haveNextCategory(String catName, AppData data) {
		int res =-1;
		Node tmpNode;

		for(int i=0; i<this.transitions.size(); i++) {
			tmpNode =this.transitions.get(i);
			if(tmpNode.getCategory().getName().equals(catName)) {
				res =i;
				return res;
			} else {
				if(this.testSubCategory(tmpNode.getCategory(), catName, data)) {
					res =i;
					return res;
				}
			}
		}
		return res;
	}
	
	/**
	 * Indique si le noeud a des transitions possibles vers d'autres categories filles.
	 * @param catName(String): Le nom de la categorie dans laquelle on souhaite aller.
	 * @param data(AppData): Les donnees de l'application.
	 * @return true si le noeud possede des trasitions vers d'autres categories filles.
	 */
	public boolean haveNextCategoryInFirstRules(String catName, AppData data) {
		if(this.category.getName().equals(catName)) {
			return true;
		} else {
			if(this.testSubCategory(this.category, catName, data)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Methode permettant d'explorer les sous-categories de la categorie passee en argument.
	 * @param cat(Category): La categorie a tester.
	 * @param catName(String): Le nom de la categorie a reconnaitre.
	 * @param data(AppData): Les donnees de l'application.
	 * @return "true" si la categorie appartient aux sous-categories de celle passee en argument OU "false" sinon.
	 */
	private boolean testSubCategory(Category cat, String catName, AppData data) {
		Category trueCat =data.getCategories().get(data.getCategoryByName(cat.getName()));
		for(int i=0;i<trueCat.getCategories().size();i++) {
			Category tmpCat =trueCat.getCategories().get(i);
			if(tmpCat.getName().equals(catName)) {
				return true;
			} else {
				if(this.testSubCategory(tmpCat, catName, data)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Methode permettant de retourner le prochain noeud issu de la transition "i".
	 * @param i La transition que l'on souhaite emprunter.
	 * @return Le Noeud issu de la transition "i".
	 */
	public Node getNextTransition(int i) {
		return this.transitions.get(i);
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Renvoie la categorie du noeud.
	 * @return La categorie du noeud.
	 **/
	public Category getCategory() {
		return this.category;
	}
	
	/**
	 * Renseigne la categorie du noeud.
	 * @param newCategory(Category): La categorie du noeud.
	 **/
	public void setCategory(Category newCategory) {
		this.category= newCategory;
	}
	
	/**
	 * Renvoie la profondeur de la categorie dans la règle courante.
	 * @return La profondeur de la categorie dans la règle courante.
	 **/
	public int getState() {
		return state;
	}
	
	/**
	 * Renseigne la profondeur de la categorie dans la règle courante.
	 * @param state(int): La profondeur de la categorie dans la règle courante.
	 **/
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Renvoie les noeuds de transition.
	 * @return Les noeuds de transition.
	 **/
	public ArrayList<Node> getTransitions() {
		return transitions;
	}
	
	/**
	 * Renseigne les noeuds de transition.
	 * @param transitions(ArrayList<Node>): Les noeuds de transition.
	 **/
	public void setTransitions(ArrayList<Node> transitions) {
		this.transitions = transitions;
	}
	
	/**
	 * Renvoie le noeud pere du noeud courant.
	 * @return Le noeud pere du noeud courant.
	 **/
	public Node getFather() {
		return father;
	}
	
	/**
	 * Renseigne le noeud pere.
	 * @param newFather(Node): Le noeud pere.
	 **/
	public void setFather(Node newFather) {
		this.father = newFather;
	}
	
	/**
	 * Renvoie les mots accessibles depuis le noeud.
	 * @return Les mots accessibles depuis le noeud.
	 **/
	public ArrayList<Lexicon> getWords() {
		return words;
	}
	
	/**
	 * Renseigne les mots accessibles depuis le noeud.
	 * @param words(ArrayList<Lexicon>): Les mots accessibles depuis le noeud.
	 **/
	public void setWords(ArrayList<Lexicon> words) {
		this.words = words;
	}
}