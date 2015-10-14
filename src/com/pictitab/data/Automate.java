package com.pictitab.data;

import java.util.ArrayList;
import java.util.List;

public class Automate {
	
	// The Automate is the predictive system.

	private String gramName;					// Grammar name
	private ArrayList<Node> rules;				// Associated rules
	private ArrayList<Integer> eligibleRules;	// Associated rules id
	private ArrayList<Node> currentNodes;		// current position in the eligible rules
	private int position;						// current position

	AppData data;
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTORS													==*/
	/*====================================================================================================================*/
	
	/** 
     * Default constructor of the Automate class. 
     **/
	public Automate() {
		this.gramName =new String("");
		this.rules =new ArrayList<Node>();
		this.eligibleRules =new ArrayList<Integer>();
		this.currentNodes =new ArrayList<Node>();
		this.position =-1;
	}
	
	/**
	 * Constructor of the Automate class.
	 * @param newRules(ArrayList<Node>): New rules.
	 * @param newGramName(String): Grammar name.
	 **/
	public Automate(ArrayList<Node> newRules, String newGramName){
		this.rules = newRules;
		this.gramName = newGramName;
		this.eligibleRules =new ArrayList<Integer>();
		this.currentNodes =new ArrayList<Node>();
		this.position =-1;
		
		// At the beginning of the rules are eligible
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
	}
	
	/**
	 * Constructor of the Automate class.
	 * @param gram(Grammar): Grammar.
	 * @param words(ArrayList<Lexicon>): Lexicon entries.
	 * @param data(AppData): Data
	 **/
	public Automate(Grammar gram, List<Lexicon> words, AppData data) {
		this.gramName =gram.getName();
		this.eligibleRules =new ArrayList<Integer>();
		this.currentNodes =new ArrayList<Node>();
		this.rules =new ArrayList<Node>();
		this.position =-1;
		this.data =data;
		for(int i=0;i<gram.getRules().size();i++) {
			Node tmp =this.createNodesFromRule(gram.getRules().get(i), 0, words);
			this.rules.add(tmp);
		}
		// At the beginning of the rules are eligible
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
	}
	
	/*====================================================================================================================*/
	/*==											 PROCESS															==*/
	/*====================================================================================================================*/
	
	/**
	 * Build the next nodes representing the rule of the automate.
	 * @param rule(ArrayList<Category>): categories sequence representing the rule of the grammar.
	 * @param numState(int): Index of the depth of the current node in the rule.
	 * @param words(List<Lexicon>): Lexicon entries.
	 * @return Current node.
	 **/
	private Node createNodesFromRule(ArrayList<Category> rule, int numState, List<Lexicon> words) {
		Node node =new Node();
		// rules of the grammar (the first element are always the category of the current node)
		ArrayList<Category> endRule =new ArrayList<Category>(rule);
		ArrayList<Lexicon> remainingWords =new ArrayList<Lexicon>(words);
		
		node.setCategory(endRule.remove(0));
		node.setWords(this.getWordsFromCategory(node.getCategory(), remainingWords));
		node.setState(numState);
		if(endRule.size()>0) {
			Node son =this.createNodesFromRule(endRule, numState+1, remainingWords);
			son.setFather(node);
			ArrayList<Node> transitions =new ArrayList<Node>();
			transitions.add(son);
			node.setTransitions(transitions);
		}
		return node;
	}
	
	/**
	 * Return words of a category.
	 * @param c(Category): Category.
	 * @param words(ArrayList<Lexicon>): Lexicon entries.
	 * @return Words of the category.
	 **/
	public ArrayList<Lexicon> getWordsFromCategory(Category c, ArrayList<Lexicon> words)
	{
		Category c2 = data.getCategories().get(data.getCategoryByName(c.getName()));
		ArrayList<Lexicon> tmpWords =new ArrayList<Lexicon>();
		List<Category> subcategories = c2.getCategories();
		for(int i =0; i < subcategories.size(); i++) {
			ArrayList<Lexicon> tmpLex =getWordsFromCategory(subcategories.get(i), words);
			tmpWords.addAll(tmpLex);
		}
		for(int i =0; i < words.size(); i++) {
			if(words.get(i).getCategory().getName().equals(c.getName())) {
				tmpWords.add(words.get(i));
			}
		}
		return tmpWords;
	}
	
	/**
	 * Go to the next state of the automate (this operation can be aborted).
	 * Update the rules and the current node.
	 * @param CatName(String): Next category name.
	 * @return "true" if ok else "false".
	 **/
	public boolean moveForwardToNextCat (String catName) {
		Node tmp;
		int index;
		ArrayList<Integer> rulesToDelete =new ArrayList<Integer>();
		boolean testForward =false;
		
		for(int i=0;i<this.eligibleRules.size();i++) {
			if(this.position==-1) {
				tmp =this.rules.get(this.eligibleRules.get(i));
				if(tmp.haveNextCategoryInFirstRules(catName, this.data)) {
					this.currentNodes.add(tmp);
					testForward =true;
				} else {
					rulesToDelete.add(this.eligibleRules.get(i));
				}
			} else {
				tmp =this.currentNodes.get(i);
				if(!tmp.isFinal()) {
					index =tmp.haveNextCategory(catName, this.data);
					if(index != -1) {
						this.currentNodes.set(i, tmp.getNextTransition(index));
						testForward =true;
					} else {
						rulesToDelete.add(this.eligibleRules.get(i));
					}
				} else {
					rulesToDelete.add(this.eligibleRules.get(i));
				}
			}
		}
		this.eligibleRules.removeAll(rulesToDelete);
		if(testForward)
			this.position++;

		return testForward;
	}
	
	/**
	 * Go to the precedent state in the automate (flashback).
	 * Update the rules and the current node.
	 * @param sentence(ArrayList<Lexicon>): The remaining words of the sentence.
	 **/
	public void moveBackward(ArrayList<Lexicon> sentence) {
		this.eligibleRules.clear();
		this.currentNodes.clear();
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
		this.position =-1;
		
		for(int i=0;i<sentence.size();i++) {
			this.moveForwardToNextCat(sentence.get(i).getCategory().getName());
		}
	}
	
	/**
	 * Return all the words to draw/display to go to the next state of the automate.
	 * @return list of lexicon entries.
	 **/
	public ArrayList<Lexicon> getWordsToDisplay() {
		ArrayList<Lexicon> words =new ArrayList<Lexicon>();
		ArrayList<Lexicon> tmpwords =new ArrayList<Lexicon>();
		Node tmp;
		if(this.position!=-1) {
			for(int i=0;i<this.eligibleRules.size();i++) {
				tmp =this.currentNodes.get(i);
				for(int j=0;j<tmp.getTransitions().size();j++) {
					tmpwords =tmp.getTransitions().get(j).getWords();
					words.removeAll(tmpwords);
					words.addAll(tmpwords);
				}
			}
		} else {
			for(int i=0;i<this.rules.size();i++) {
				tmp =this.rules.get(i);
				tmpwords =tmp.getWords();
				words.removeAll(tmpwords);
				words.addAll(tmpwords);
			}
		}
		return words;
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Return automate's rules.
	 * @return rules.
	 **/
	public ArrayList<Node> getRules() {
		return rules;
	}
	
	/**
	 * Set rules to the automate.
	 * @param rules(ArrayList<Node>): rules.
	 **/
	public void setRules(ArrayList<Node> rules) {
		this.rules = rules;
	}
	
	/**
	 * Return automate's grammar.
	 * @return grammar.
	 **/
	public String getGramName() {
		return gramName;
	}
	
	/**
	 * Set grammar to the automate.
	 * @param gramName(String): Grammar.
	 **/
	public void setGramName(String gramName) {
		this.gramName = gramName;
	}
}
