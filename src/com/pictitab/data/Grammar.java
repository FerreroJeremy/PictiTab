package com.pictitab.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Grammar implements Parcelable {
	
	// Les informations relatives a la grammaire
	private String name;							// Le nom de la grammaire
	private ArrayList<ArrayList<Category>> rules;	// Les regles associees

	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Grammar. **/
	public Grammar() {
		this.name  = new String("");
		this.rules = new ArrayList<ArrayList<Category>>();
	}
	
	/**
	 * Constructeur de la classe Grammar.
	 * @param name(String): Nom de la grammaire.
	 */
	public Grammar(String name) {
		this.name = name;
		this.rules = new ArrayList<ArrayList<Category>>();
	}
	
	/**
	 * Constructeur de la classe Grammar.
	 * @param name(String): Nom de la grammaire.
	 * @param rules(ArrayList<ArrayList<Category>>): Liste des regles de la grammaire.
	 */
	public Grammar(String name, ArrayList<ArrayList<Category>> rules) {
		this.name = name;
		this.rules = rules;
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Constructeur de la classe Grammar utilisant Parcel
	 * @param in(Parcel): Le paquet dans lequel sont contenues les donnees
	 **/
	public Grammar(Parcel in) {
		this();
		readFromParcel(in);
	}
	
	/** Les classes qui implementent l'interface Parcelable doivent aussi avoir un champ statique appele CREATOR. **/
	public static final Creator<Grammar> CREATOR = new Creator<Grammar>() {

		public Grammar createFromParcel(Parcel source) {
			return new Grammar(source);
		}
		
		public Grammar[] newArray(int size) {
			return new Grammar[size];
		}
	};
	
	/**
	 * Ecriture des differents types de donnees de la classe Grammar
	 * @param dest(Parcel): Le paquet dans lequel sont ecrites les donnees
	 * @param flags(): Des indicateurs supplementaires sur la façon dont l'objet doit etre ecrit.
	 * Peut etre 0 ou PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(rules);
	}

	/**
	 * Lecture des differents types de donnees de la classe Grammar 
	 * @param in(Parcel): Le paquet depuis lequel sont lues les donnees
	 **/
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.name  = in.readString();
		this.rules = in.readArrayList(Grammar.class.getClassLoader());
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
	 * Renvoie le nom de la grammaire.
	 * @return Le nom de la grammaire.
	 **/
	public String getName() {
		return this.name;
	}
	
	/**
	 * Renomme la grammaire.
	 * @param newName(String): Nouveau nom de la grammaire.
	 **/
	public void rename(String newName) {
		this.name = newName;
	}
	
	/**
	 * Renvoie les regles de la grammaire.
	 * @return Les regles de la grammaire.
	 **/
	public ArrayList<ArrayList<Category>> getRules() {
		return this.rules;
	}
	
	/**
	 * Renvoie la regle d'indice i.
	 * @param i(int) : L'indice de la regle.
	 * @return La regle d'indice i.
	 **/
	public ArrayList<Category> getRuleAt(int i) {
		return rules.get(i);
	}
	
	/**
	 * Renseigne la regle d'indice i.
	 * @param i(int): L'indice de la regle.
	 * @param rule(ArrayList<Category>): La regle a definir. 
	 **/
	public void setRuleAt(int i, ArrayList<Category> rule) {
		this.rules.set(i, rule);
	}
	
	/**
	 * Ajoute une regle a la grammaire.
	 * @param rule(ArrayList<Category>): La regle a ajouter. 
	 **/
	public void addRule(ArrayList<Category> rule) {
		this.rules.add(rule);
	}
	
	/**
	 * Supprime la regle d'indice i.
	 * @param i(int) : L'indice de la regle.
	 **/
	public void deleteRule(int i) {
		rules.remove(i);
	}

	/**
	 * Renvoie la categorie d'indice j de la regle i.
	 * @param i(int): L'indice de la regle.
	 * @param j(int): L'indice de la categorie.
	 * @return La categorie d'indice j de la regle i.
	 **/
	public Category getCategoryAt(int i, int j) {
		return this.rules.get(i).get(j);
	}

	/**
	 * Renseigne la categorie d'indice j de la regle i.
	 * @param i(int): L'indice de la regle.
	 * @param j(int): L'indice de la categorie.
	 **/
	public void setCategoryAt(int i, int j, Category category) {
		this.rules.get(i).set(j, category);
	}
}
