package com.pictitab.data;

import java.util.ArrayList;

public class Entry {
	
	// Les informations relatives a l'entree dans l'historiques des logs des enfants
	private String date;					// La date de l'entree
	private ArrayList<Lexicon> sequence;	// La sequence des mots utilises
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Entry. **/
	public Entry() {
		this.setDate(new String(""));
		this.setSequence(new ArrayList<Lexicon>());
	}
	
	/**
	 * Constructeur de la classe Entry.
	 * @param date(String): Date d'entree.
	 * @param sequence(ArrayList<Lexicon>): Sequence des mots.
	 */
	public Entry(String date,  ArrayList<Lexicon> sequence) {
		this.setDate(date);
		this.setSequence(sequence);
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Renvoie la date de l'entree.
	 * @return La date de l'entree.
	 **/
	public String getDate() {
		return date;
	}
	/**
	 * Renseigne la date de lentree.
	 * @param date(String): La date de l'entree.
	 **/
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Renvoie la sequence realisee.
	 * @return La sequence realisee.
	 **/
	public ArrayList<Lexicon> getSequence() {
		return sequence;
	}
	
	/**
	 * Renseigne la sequence.
	 * @param sequence(ArrayList<Lexicon>): La sequence.
	 **/
	public void setSequence(ArrayList<Lexicon> sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Renvoie le mot d'indice i.
	 * @param i(int): L'indice de la regle.
	 * @return Le mot d'indice i de la sequence de l'entree.
	 **/
	public Lexicon getWordAt(int i) {
		return this.sequence.get(i);
	}
	
	/**
	 * Renseigne le mot d'indice i.
	 * @param i(int): L'indice de la regle.
	 * @param word(Lexicon): Le mot.
	 **/
	public void setWordAt(int i, Lexicon word) {
		this.sequence.set(i, word);
	}
}
