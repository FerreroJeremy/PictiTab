package com.pictitab.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AppData implements Parcelable {

	// The different objects of the application
	private List<Category> categories; // Categories
	private List<Lexicon> lexique; // Lexicon entries
	private List<Grammar> grammars; // Grammars
	private List<Child> profils; // Children profiles

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == CONSTRUCTOR == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/** Default constructor of the AppData class. **/
	public AppData() {
		categories = new ArrayList<Category>();
		lexique = new ArrayList<Lexicon>();
		grammars = new ArrayList<Grammar>();
		profils = new ArrayList<Child>();
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
	 * Constructor of the AppData class using Parcel
	 * 
	 * @param in
	 *            (Parcel): Data in package
	 **/
	public AppData(Parcel in) {
		this();
		readFromParcel(in);
	}

	/**
	 * Necessary creator.
	 **/
	public static final Parcelable.Creator<AppData> CREATOR = new Parcelable.Creator<AppData>() {

		public AppData createFromParcel(Parcel source) {
			return new AppData(source);
		}

		public AppData[] newArray(int size) {
			return new AppData[size];
		}

	};

	/**
	 * Write the different data of the class AppData into Parcel.
	 * 
	 * @param dest
	 *            (Parcel): Data in package
	 * @param flags
	 *            (): supplementary information about the writing. May be 0 or
	 *            PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(categories);
		dest.writeTypedList(lexique);
		dest.writeTypedList(grammars);
		dest.writeTypedList(profils);
	}

	/**
	 * Read the different data of the class AppData from Parcel.
	 * 
	 * @param in
	 *            (Parcel): Data in package
	 **/
	private void readFromParcel(Parcel in) {
		in.readTypedList(categories, Category.CREATOR);
		in.readTypedList(lexique, Lexicon.CREATOR);
		in.readTypedList(grammars, Grammar.CREATOR);
		in.readTypedList(profils, Child.CREATOR);
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

	/*
	 * ======================================== CATEGORIES
	 * ==============================================
	 */

	/**
	 * Return the loaded categories.
	 * 
	 * @return Sub-categories.
	 **/
	public List<Category> getCategories() {
		return this.categories;
	}

	/**
	 * Return if the categories are loaded or not.
	 * 
	 * @param newCategories
	 *            (ArrayList<Category>): New categories.
	 * @return true if categories are loaded (i.e. newProfils != null) else
	 *         false.
	 **/
	public boolean setCategories(ArrayList<Category> newCategories) {
		if (newCategories != null) {
			this.categories = newCategories;
			return true;
		}
		return false;
	}

	/**
	 * Add a new category
	 * 
	 * @param newCategory
	 *            (Category): New category.
	 **/
	public void addCategory(Category newCategory) {
		this.categories.add(newCategory);
	}

	/**
	 * Return a index of a category from its name.
	 * 
	 * @param name
	 *            (String): Sub-category name.
	 * @return Index.
	 **/
	public int getCategoryByName(String name) {
		int size = this.categories.size();
		for (int i = 0; i < size; i++) {
			String categoryName = this.categories.get(i).getName();
			if (name.equals(categoryName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Return categories that aren't child of an another category.
	 * 
	 * @return Categories list.
	 **/
	public ArrayList<Category> getNotChildCategories() {
		ArrayList<Category> list = new ArrayList<Category>();
		int compteur;
		int size = categories.size();
		for (int i = 0; i < size; i++) {

			compteur = 0;
			String nameC1 = categories.get(i).getName();
			for (int j = 0; j < size; j++) {

				int nbChildren = categories.get(j).getCategories().size();
				for (int k = 0; k < nbChildren; k++) {
					if (nameC1.equals(categories.get(j).getCategories().get(k)
							.getName()))
						compteur++;
				}
			}
			if (compteur == 0) {
				list.add(categories.get(i));
			}
		}
		return list;
	}

	/**
	 * Rename a category.
	 * 
	 * @param oldName
	 *            (String): Old name.
	 * @param newName
	 *            (String): New name.
	 **/
	public void renameCategory(String oldName, String newName) {

		// Rename category in lexicon and grammar
		renameCategoryInLexicon(oldName, newName);
		renameCategoryInGrammars(oldName, newName);

		Category cat = this.categories.get(this.getCategoryByName(oldName));

		// Rename in the sub-category of its mother category
		for (int j = 0; j < this.categories.size(); j++) {
			List<Category> subCats = this.categories.get(j).getCategories();
			for (int k = 0; k < subCats.size(); k++) {
				if (subCats.get(k) != null) {
					if (subCats.get(k).getName().equals(oldName))
						subCats.get(k).rename(newName);
				}
			}
		}
		// Rename the category
		cat.rename(newName);
	}

	/**
	 * Delete lexicon entries and grammars depending a category.
	 * 
	 * @param name
	 *            (String): Category name.
	 **/
	public void deleteDependencyCategory(String name) {
		ArrayList<Lexicon> listOfWords = this.getWordsByCategory(this
				.getCategories().get(this.getCategoryByName(name)));
		for (int i = 0; i < listOfWords.size(); i++) {
			this.deleteWord(listOfWords.get(i).getWord());
		}
		ArrayList<Grammar> listOfGrammars = this.getGrammarsByCategory(this
				.getCategories().get(this.getCategoryByName(name)));
		for (int i = 0; i < listOfGrammars.size(); i++) {
			this.deleteGrammar(listOfGrammars.get(i).getName());
		}
	}

	/**
	 * Return a list of categories to delete.
	 * 
	 * @param deletedList
	 *            (ArrayList<Category>): Categories to delete.
	 * @param name
	 *            (String): Sub-category name.
	 **/
	public ArrayList<Category> deleteCategoryStep(
			ArrayList<Category> deletedList, String name) {
		Category cat = this.categories.get(this.getCategoryByName(name));

		for (int i = 0; i < cat.getCategories().size(); i++) {
			List<Category> subCategories = cat.getCategories();
			if (subCategories != null) {
				String subCatName = subCategories.get(i).getName();
				Category subCat = this.getCategories().get(
						this.getCategoryByName(subCatName));
				List<Category> subSubCategories = subCat.getCategories();

				if (subSubCategories != null) {
					deletedList = this.deleteCategoryStep(deletedList,
							subCatName);
				}

			}
		}
		deletedList.add(cat);
		return deletedList;
	}

	/**
	 * Delete categories.
	 * 
	 * @param deletedList
	 *            (ArrayList<Category>): list of categories.
	 **/
	public void deleteCategories(ArrayList<Category> deletedList) {

		for (int i = 0; i < deletedList.size(); i++) {

			String name = deletedList.get(i).getName();
			deleteDependencyCategory(name);

			// Drop the sub-categories list
			Category cat = this.categories.get(this.getCategoryByName(name));
			cat.clearCategories();

			// Delete the category selected in the sub-categories of its mother
			for (int j = 0; j < this.categories.size(); j++) {
				List<Category> subCats = this.categories.get(j).getCategories();
				for (int k = 0; k < subCats.size(); k++) {
					if (subCats.get(k) != null) {
						if (subCats.get(k).getName().equals(name))
							subCats.remove(subCats.get(k));
					}
				}
			}
			// Delete
			this.categories.remove(cat);
		}
	}

	/**
	 * Delete category and its sub-categories.
	 * 
	 * @param name
	 *            (String): category name.
	 **/
	public void deleteCategoryAndItsChildren(String name) {
		ArrayList<Category> deletedList = new ArrayList<Category>();
		deletedList = deleteCategoryStep(deletedList, name);
		deleteCategories(deletedList);
	}

	/*
	 * ======================================== LEXICON
	 * ==============================================
	 */

	/**
	 * Return lexicon entries.
	 * 
	 * @return Lexicon entries.
	 **/
	public List<Lexicon> getLexicon() {
		return this.lexique;
	}

	/**
	 * Return if the words are loaded or not.
	 * 
	 * @param newLexicon
	 *            (List<Lexicon>): new lexicon.
	 * @return true if the lexicon entries are updated (i.e. newLexicon != null)
	 *         else false.
	 **/
	public boolean setLexicon(List<Lexicon> newLexicon) {
		if (newLexicon != null) {
			this.lexique = newLexicon;
			return true;
		}
		return false;
	}

	/**
	 * Add a lexicon entry.
	 * 
	 * @param newLexicon
	 *            (Lexicon): New lexicon entry (a word).
	 **/
	public void addLexicon(Lexicon newLexicon) {
		this.lexique.add(newLexicon);
	}

	/**
	 * Delete a lexicon entry from its id.
	 * 
	 * @param num
	 *            (int): Index.
	 **/
	public void deleteLexicon(int num) {
		int size = this.categories.size();
		if (num < size) {
			this.lexique.remove(num);
		}
	}

	/**
	 * Delete a lexicon entry.
	 * 
	 * @param name
	 *            (String): Word.
	 **/
	public void deleteWord(String name) {
		int size = this.lexique.size();
		for (int i = 0; i < size; i++) {
			String lexiconName = this.lexique.get(i).getWord();
			if (name.equals(lexiconName)) {
				lexique.remove(i);
				break;
			}
		}
	}

	/**
	 * Return a lexicon entry according to its word.
	 * 
	 * @param name
	 *            (String): Word
	 * @return Lexicon entry
	 **/
	public Lexicon getWordByName(String name) {
		Lexicon lexicon = null;
		for (int i = 0; i < lexique.size(); i++) {
			if (name.equals(lexique.get(i).getWord()))
				lexicon = lexique.get(i);
		}
		return lexicon;
	}

	/**
	 * Return word according to a category.
	 * 
	 * @param category
	 *            (Category): Category
	 * @return (ArrayList<Lexicon>) : list of lexicon entries
	 **/
	public ArrayList<Lexicon> getWordsByCategory(Category category) {
		ArrayList<Lexicon> list = new ArrayList<Lexicon>();
		for (int i = 0; i < lexique.size(); i++) {
			if (lexique.get(i) != null) {
				if (lexique.get(i).getCategory() != null) {
					if (category.getName().equals(
							lexique.get(i).getCategory().getName()))
						list.add(lexique.get(i));
				}
			}
		}
		return list;
	}

	/**
	 * Rename categories in lexicon entries.
	 * 
	 * @param oldName
	 *            (String): old category name.
	 * @param newName
	 *            (String): new category name.
	 **/
	public void renameCategoryInLexicon(String oldName, String newName) {

		Category cat = this.categories.get(this.getCategoryByName(oldName));

		for (int i = 0; i < lexique.size(); i++) {
			if (lexique.get(i) != null) {
				if (lexique.get(i).getCategory() != null) {
					if (cat.getName().equals(
							lexique.get(i).getCategory().getName()))
						lexique.get(i).getCategory().rename(newName);
				}
			}
		}
	}

	/*
	 * ======================================== GRAMMARS
	 * ==============================================
	 */

	/**
	 * Return loaded grammars.
	 * 
	 * @return Grammars.
	 **/
	public List<Grammar> getGrammars() {
		return this.grammars;
	}

	/**
	 * Return if the grammars are loaded or not.
	 * 
	 * @param newGrammars
	 *            (ArrayList<Grammar>): grammars to loaded.
	 * @return true if the grammars are updated (i.e. newProfils != null) else
	 *         false.
	 **/
	public boolean setGrammars(ArrayList<Grammar> newGrammars) {
		if (newGrammars != null) {
			this.grammars = newGrammars;
			return true;
		}
		return false;
	}

	/**
	 * Return grammar form its name.
	 * 
	 * @param name
	 *            (String): grammar name.
	 * @return Grammar.
	 **/
	public Grammar getGrammarByName(String name) {
		Grammar grammar = null;
		for (int i = 0; i < grammars.size(); i++) {
			if (name.equals(grammars.get(i).getName()))
				grammar = grammars.get(i);
		}
		return grammar;
	}

	/**
	 * Add a grammar
	 * 
	 * @param newGrammar
	 *            (Grammar): New grammar.
	 **/
	public void addGrammar(Grammar newGrammar) {
		this.grammars.add(newGrammar);
	}

	/**
	 * Return grammar with rules including a certain category
	 * 
	 * @param category
	 *            (Category): Searched category
	 * @return list of grammars
	 **/
	public ArrayList<Grammar> getGrammarsByCategory(Category category) {
		ArrayList<Grammar> list = new ArrayList<Grammar>();
		for (int i = 0; i < grammars.size(); i++) {
			for (int j = 0; j < grammars.get(i).getRules().size(); j++) {
				for (int k = 0; k < grammars.get(i).getRules().get(j).size(); k++) {
					if (grammars.get(i) != null) {
						if (grammars.get(i).getCategoryAt(j, k) != null) {
							if (category.getName().equals(
									grammars.get(i).getCategoryAt(j, k)
											.getName())) {
								if (!list.contains(grammars.get(i))) {
									list.add(grammars.get(i));
									break;
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Rename category in grammars rules.
	 * 
	 * @param oldName
	 *            (String): old category name.
	 * @param newName
	 *            (String): new category name.
	 **/
	public void renameCategoryInGrammars(String oldName, String newName) {
		Category cat = this.categories.get(this.getCategoryByName(oldName));
		for (int i = 0; i < grammars.size(); i++) {
			for (int j = 0; j < grammars.get(i).getRules().size(); j++) {
				for (int k = 0; k < grammars.get(i).getRules().get(j).size(); k++) {
					if (grammars.get(i) != null) {
						if (grammars.get(i).getCategoryAt(j, k) != null) {
							if (cat.getName().equals(
									grammars.get(i).getCategoryAt(j, k)
											.getName())) {
								grammars.get(i).getCategoryAt(j, k)
										.rename(newName);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Delete a grammar.
	 * 
	 * @param name
	 *            (String): Grammar name.
	 **/
	public void deleteGrammar(String name) {
		int size = this.grammars.size();
		for (int i = 0; i < size; i++) {
			String grammarName = this.grammars.get(i).getName();
			if (name.equals(grammarName)) {
				grammars.remove(i);
				break;
			}
		}
	}

	/*
	 * ======================================== PROFILES
	 * ==============================================
	 */

	/**
	 * Return loaded profiles.
	 * 
	 * @return Profiles.
	 **/
	public List<Child> getProfils() {
		return this.profils;
	}

	/**
	 * Return if the profiles are loaded or not.
	 * 
	 * @param newProfils
	 *            (ArrayList<Child>): profiles to load.
	 * @return true if the profiles are loaded (i.e. newProfils != null) else
	 *         false.
	 **/
	public boolean setProfils(ArrayList<Child> newProfils) {
		if (newProfils != null) {
			this.profils = newProfils;
			return true;
		}
		return false;
	}

	/**
	 * Add a child profile
	 * 
	 * @param newProfil
	 *            (Child): New child profile.
	 * **/
	public void addProfil(Child newProfil) {
		this.profils.add(newProfil);
	}

	/**
	 * Delete a child profile.
	 * 
	 * @param nom
	 *            (String): Child profile last name.
	 * @param prenom
	 *            (String): Child profile first name.
	 **/
	public void deleteProfil(String nom, String prenom) {
		int size = this.profils.size();
		for (int i = 0; i < size; i++) {
			String nomProfil = this.profils.get(i).getName();
			String prenomProfil = this.profils.get(i).getFirstname();
			if (nom.equals(nomProfil) && prenom.equals(prenomProfil)) {
				profils.remove(i);
				break;
			}
		}
	}

	/**
	 * Return a child according to its name.
	 * 
	 * @param nom
	 *            (String): Child profile last name.
	 * @param prenom
	 *            (String): Child profile first name.
	 **/
	public Child getChildByName(String nom, String prenom) {
		Child c = null;
		int size = this.profils.size();
		for (int i = 0; i < size; i++) {
			String nomProfil = this.profils.get(i).getName();
			String prenomProfil = this.profils.get(i).getFirstname();
			if (nom.equals(nomProfil) && prenom.equals(prenomProfil)) {
				c = profils.get(i);
			}
		}
		return c;
	}
}
