package com.pictitab.data;

import java.util.ArrayList;

public class Entry {
	
	private String date;					// The entry date
	private ArrayList<Lexicon> sequence;	// The used word sequence
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTORS                                                 ==*/
	/*====================================================================================================================*/
	
	/** 
     * Default constructor of the class.
     **/
	public Entry() {
		this.setDate(new String(""));
		this.setSequence(new ArrayList<Lexicon>());
	}
	
	/**
	 * Copy constructor of the class.
	 * @param date(String): Entry date.
	 * @param sequence(ArrayList<Lexicon>): Word sequence.
	 */
	public Entry(String date,  ArrayList<Lexicon> sequence) {
		this.setDate(date);
		this.setSequence(sequence);
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Get the entry date.
	 * @return Date.
	 **/
	public String getDate() {
		return date;
	}
	/**
	 * Set a entry date.
	 * @param date(String): Date.
	 **/
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Get the word sequence.
	 * @return List of words.
	 **/
	public ArrayList<Lexicon> getSequence() {
		return sequence;
	}
	
	/**
	 * Set a word sequence.
	 * @param sequence(ArrayList<Lexicon>): List of words.
	 **/
	public void setSequence(ArrayList<Lexicon> sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Return the word of position i.
	 * @param i(int): position.
	 * @return Word.
	 **/
	public Lexicon getWordAt(int i) {
		return this.sequence.get(i);
	}
	
	/**
	 * Set a word in position i.
	 * @param i(int): Position.
	 * @param word(Lexicon): Word.
	 **/
	public void setWordAt(int i, Lexicon word) {
		this.sequence.set(i, word);
	}
}
