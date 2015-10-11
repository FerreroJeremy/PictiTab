package com.pictitab.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AppData implements Parcelable {

	// Les differents objets de l'application
	private List<Category> categories;	// Les categories
	private List<Lexicon> lexique;		// Les entrees du lexique
	private List<Grammar> grammars;		// Les grammaires
	private List<Child> profils;		// Les profils enfant
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe AppData. **/
	public AppData() {
		categories = new ArrayList<Category>();
		lexique    = new ArrayList<Lexicon>();
		grammars   = new ArrayList<Grammar>();
		profils    = new ArrayList<Child>();
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Constructeur de la classe AppData utilisant Parcel
	 * @param in(Parcel): Le paquet dans lequel sont contenues les donnees
	 **/
	public AppData (Parcel in) {
		this();
		readFromParcel(in);
	}
	
	/** Les classes qui implementent l'interface Parcelable doivent aussi avoir un champ statique appele CREATOR. **/
	public static final Parcelable.Creator<AppData> CREATOR = new Parcelable.Creator<AppData>() {

		public AppData createFromParcel(Parcel source) {
			return new AppData(source);
		}

		public AppData[] newArray(int size) {
			return new AppData[size];
		}

	};
	
	/**
	 * Ecriture des differents types de donnees de la classe AppData
	 * @param dest(Parcel): Le paquet dans lequel sont ecrites les donnees
	 * @param flags(): Des indicateurs supplementaires sur la façon dont l'objet doit etre ecrit.
	 * Peut etre 0 ou PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(categories);
		dest.writeTypedList(lexique);
		dest.writeTypedList(grammars);
		dest.writeTypedList(profils);
	}
	
	/**
	 * Lecture des differents types de donnees de la classe AppData 
	 * @param in(Parcel): Le paquet depuis lequel sont lues les donnees
	 **/
	private void readFromParcel(Parcel in) {
		in.readTypedList(categories, Category.CREATOR);
		in.readTypedList(lexique, Lexicon.CREATOR);
		in.readTypedList(grammars, Grammar.CREATOR);
		in.readTypedList(profils, Child.CREATOR);
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

	/*========================================			CATEGORIES			==============================================*/
	
	/**
	 * Methode retournant les categories chargees.
	 * @return Les sous-categories.
	 * **/
	public List<Category> getCategories() {
		return this.categories;
	}
	
	/**
	 * Methode indiquant si oui ou non les categories ont ete chargees.
	 * @param newCategories(ArrayList<Category>): Les nouvelles categories a charger.
	 * @return "true" si les categories ont pu etre mises a jour (c-a-d: newProfils != "null") OU "false" dans le cas contraire.
	 **/
	public boolean setCategories(ArrayList<Category> newCategories) {
		if (newCategories != null) {
			this.categories = newCategories;
			return true;
		}
		return false;
	}

	/**
	 * Methode ajoutant une nouvelle categorie
	 * @param newCategory(Category): La nouvelle categorie.
	 * **/
	public void addCategory(Category newCategory) {
		this.categories.add(newCategory);
	}
	
	/**
	 * Methode retournant l'indice d'une categorie dans la liste complete a partir de son nom.
	 * @param name(String): Le nom de la sous-categorie.
	 * @return L'indice de la categorie ayant ce nom dans la liste de toutes les categories.
	 * **/
	public int getCategoryByName(String name){
		int size = this.categories.size();
		for(int i=0 ; i < size ; i++){
			String categoryName = this.categories.get(i).getName();
			if(name.equals(categoryName)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Renseigne la liste des categories qui ne sont filles d'aucune autre categorie que la racine.
	 * @return La liste des categories qui ne sont filles d'aucune autre categorie que la racine.
	 **/
	public ArrayList<Category> getNotChildCategories() {
		ArrayList<Category> list = new ArrayList<Category>();
		int compteur;
		int size = categories.size();
		for(int i=0 ; i < size ; i++) {
			
			compteur = 0;
			String nameC1 = categories.get(i).getName();
			for(int j=0 ; j < size ; j++) {
				
				int nbChildren = categories.get(j).getCategories().size();
				for(int k=0 ; k < nbChildren ; k++) {
					if(nameC1.equals(categories.get(j).getCategories().get(k).getName()))
						compteur++;
				}
			}
			if(compteur == 0) {
				list.add(categories.get(i));				
			}
		}
		
		return list;
	}
	
	/**
	 * Methode renommant une categorie.
	 * @param oldName(String): Le nom de la categorie a renommer.
	 * @param newName(String): Le nouveau nom de la categorie.
	 * **/
	public void renameCategory(String oldName, String newName){
		
		// On renomme la categorie dans les mots du lexique et dans les regles des grammaires
		renameCategoryInLexicon(oldName, newName);
		renameCategoryInGrammars(oldName, newName);
		
		Category cat = this.categories.get(this.getCategoryByName(oldName));
		
		// On cherche a la renommer dans les sous-categories de ses meres
		// On parcourt toutes les categories de data
		for (int j=0; j < this.categories.size(); j++) {
			List<Category> subCats = this.categories.get(j).getCategories();
			// On parcourt leurs sous-categories
			for (int k=0; k < subCats.size(); k++) {
				if(subCats.get(k) != null) {
					// Si l'une de leurs sous-categories est celle que l'on va supprimer
					if(subCats.get(k).getName().equals(oldName))
						// On l'enleve de ses filles
						subCats.get(k).rename(newName);
				}
			}
		}
		// Et enfin on renomme ladite categorie
		cat.rename(newName);
	}

	/**
	 * Methode supprimant les grammaires et les mots dependants d'une categorie.
	 * @param name(String): Le nom de la categorie.
	 **/
	public void deleteDependencyCategory(String name){
		
		// On supprime les mots du lexique appartenant a cette categorie
		ArrayList<Lexicon> listOfWords= this.getWordsByCategory(this.getCategories().get(this.getCategoryByName(name)));
		for(int i=0; i < listOfWords.size(); i++) {
			this.deleteWord(listOfWords.get(i).getWord());
		}
		
		// On supprime les grammaires ayant des regles contenant cette categorie
		ArrayList<Grammar> listOfGrammars= this.getGrammarsByCategory(this.getCategories().get(this.getCategoryByName(name)));
		for(int i=0; i < listOfGrammars.size(); i++) {
			this.deleteGrammar(listOfGrammars.get(i).getName());
		}
	}
	
	/**
	 * Methode retournant une liste de categories a supprimer.
	 * @param deletedList(ArrayList<Category>): La liste des categories a supprimer.
	 * @param name(String): Le nom de la sous-categorie.
	 * **/
	public ArrayList<Category> deleteCategoryStep(ArrayList<Category> deletedList, String name){
		
		Category cat = this.categories.get(this.getCategoryByName(name));
		
		// On supprime les sous-categories de la categorie
		for(int i=0 ; i < cat.getCategories().size() ; i++) {
			List<Category> subCategories = cat.getCategories();
			if(subCategories != null) {
				// On recupere son nom
				String subCatName = subCategories.get(i).getName();
				Category subCat = this.getCategories().get(this.getCategoryByName(subCatName));
				// On recupere ses sous-categories
				List<Category> subSubCategories = subCat.getCategories();

				if(subSubCategories != null) {
					// On fait un appel recursif sur celles-ci
					deletedList = this.deleteCategoryStep(deletedList, subCatName);
				}
				
			}
		}
		// On ajoute la categorie a la liste
		deletedList.add(cat);
		return deletedList;
	}
	
	/**
	 * Methode supprimant une liste de categories.
	 * @param deletedList(ArrayList<Category>): La liste des categories a supprimer.
	 * **/
	public void deleteCategories(ArrayList<Category> deletedList){
		
		// On boucle sur la liste des categories a supprimer
		for(int i=0; i < deletedList.size(); i++) {
			// On recupere son nom et on supprime ses grammaires et mots dependants
			String name = deletedList.get(i).getName();
			deleteDependencyCategory(name);
			
			// On vide sa liste de sous-categories
			Category cat = this.categories.get(this.getCategoryByName(name));
			cat.clearCategories();
			
			// On cherche a la supprimer dans les sous-categories de ses meres
			// On parcourt toutes les categories de data
			for (int j=0; j < this.categories.size(); j++) {
				List<Category> subCats = this.categories.get(j).getCategories();
				// On parcourt leurs sous-categories
				for (int k=0; k < subCats.size(); k++) {
					// Si l'une de leurs sous-categories est celle que l'on va supprimer
					if(subCats.get(k) != null) {
						if(subCats.get(k).getName().equals(name))
							// On l'enleve de ses filles
							subCats.remove(subCats.get(k));
					}
				}
			}
			// Et enfin on supprime ladite categorie
			this.categories.remove(cat);
		}
	}
	
	/**
	 * Methode supprimant une liste de categories et ses sous-categories.
	 * @param name(String): Le nom de la categorie a supprimer.
	 * **/
	public void deleteCategoryAndItsChildren(String name){
		
		ArrayList<Category> deletedList = new ArrayList<Category>();
		// Retourne la liste de toutes les categories a supprimer
		deletedList = deleteCategoryStep(deletedList, name);
		
		// On supprime toutes les categories de cette liste
		deleteCategories(deletedList);
	}
	
	/*========================================			 LEXICON			==============================================*/
	
	/**
	 * Methode retournant les entrees du lexique.
	 * @return Les entrees du lexique.
	 **/
	public List<Lexicon> getLexicon() {
		return this.lexique;
	}
	
	/**
	 * Methode indiquant si oui ou non les mots ont ete charges.
	 * @param newLexicon(List<Lexicon>): Le nouveau lexique a charger.
	 * @return "true" si les entrees du lexique ont pu etre mises a jour (c-a-d: newLexicon != "null") OU "false" dans le cas contraire.
	 **/
	public boolean setLexicon(List<Lexicon> newLexicon) {
		if (newLexicon != null) {
			this.lexique = newLexicon;
			return true;
		}
		return false;
	}
	
	/**
	 * Methode ajoutant une nouvelle entree du lexique.
	 * @param newLexicon(Lexicon): La nouvelle entree du lexique.
	 **/
	public void addLexicon(Lexicon newLexicon) {
		this.lexique.add(newLexicon);
	}

	/**
	 * Methode supprimant une entree du lexique a partir de son numero.
	 * @param num(int): L'identifiant de l'entree du lexique.
	 **/
	public void deleteLexicon(int num){
		int size = this.categories.size();
		if(num<size) {
			this.lexique.remove(num);
		}
	}
	
	/**
	 * Methode supprimant une entree du lexique
	 * @param name(String): Le mot.
	 **/
	public void deleteWord(String name){
		int size = this.lexique.size();
		for(int i=0 ; i < size ; i++){
			String lexiconName = this.lexique.get(i).getWord();
			if(name.equals(lexiconName)){
				lexique.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Methode retournant le mot ayant un certain nom.
	 * @param name(String): Le nom de l'entree a retourner
	 * @return L'entree ayant pour mot name
	 **/
	public Lexicon getWordByName(String name) {
		Lexicon lexicon = null;
		for(int i=0 ; i < lexique.size() ; i++) {
			if(name.equals(lexique.get(i).getWord()))
				lexicon = lexique.get(i);
		}
		return lexicon;
	}
	
	/**
	 * Methode retournant les mots ayant une certaine categorie.
	 * @param category(Category): La categorie a rechercher
	 * @return (ArrayList<Lexicon>) : La liste de mot appartenant a la categorie
	 **/
	public ArrayList<Lexicon> getWordsByCategory(Category category) {
		ArrayList<Lexicon> list= new ArrayList<Lexicon>();
		for(int i=0 ; i < lexique.size() ; i++) {
			if(lexique.get(i) != null) {
				if(lexique.get(i).getCategory() != null) {
					if(category.getName().equals(lexique.get(i).getCategory().getName()))
						list.add(lexique.get(i));
				}
			}
		}
		return list;
	}
	
	/**
	 * Methode renommant les categories dans le lexique.
	 * @param oldName(String): Le nom de la categorie a renommer.
	 * @param newName(String): Le nouveau nom de la categorie.
	 **/
	public void renameCategoryInLexicon(String oldName, String newName) {
		
		Category cat = this.categories.get(this.getCategoryByName(oldName));
		
		for(int i=0 ; i < lexique.size() ; i++) {
			if(lexique.get(i) != null) {
				if(lexique.get(i).getCategory() != null) {
					if(cat.getName().equals(lexique.get(i).getCategory().getName()))
						lexique.get(i).getCategory().rename(newName);
				}
			}
		}
	}
	
	/*========================================			 GRAMMARS			==============================================*/
	
	/**
	 * Methode retournant les grammaires chargees.
	 * @return Les grammaires.
	 **/
	public List<Grammar> getGrammars() {
		return this.grammars;
	}
	
	/**
	 * Methode indiquant si oui ou non les grammaires ont ete chargees.
	 * @param newGrammars(ArrayList<Grammar>): Les nouvelles grammaires a charger.
	 * @return "true" si les grammaires ont pu etre mises a jour (c-a-d: newProfils != "null") OU "false" dans le cas contraire.
	 **/
	public boolean setGrammars(ArrayList<Grammar> newGrammars) {
		if (newGrammars != null) {
			this.grammars = newGrammars;
			return true;
		}
		return false;
	}
	
	/**
	 * Methode retournant la grammaire ayant un certain nom.
	 * @param name(String): Le nom de la grammaire a retourner.
	 * @return Le grammaire de nom name.
	 **/
	public Grammar getGrammarByName(String name) {
		Grammar grammar = null;
		for(int i=0 ; i < grammars.size() ; i++) {
			if(name.equals(grammars.get(i).getName()))
				grammar = grammars.get(i);
		}
		return grammar;
	}
	
	/**
	 * Methode ajoutant une nouvelle grammaire
	 * @param newGrammar(Grammar): La nouvelle grammaire.
	 **/
	public void addGrammar(Grammar newGrammar) {
		this.grammars.add(newGrammar);
	}
	
	/**
	 * Methode retournant les grammaires ayant des regles contenant une certaine categorie.
	 * @param category(Category): La categorie a rechercher
	 * @return La liste des grammaires ayant une regle avec la categorie
	 **/
	public ArrayList<Grammar> getGrammarsByCategory(Category category) {
		ArrayList<Grammar> list= new ArrayList<Grammar>();
		// On parcourt toutes les grammaires
		for(int i=0 ; i < grammars.size() ; i++) {
			// On parcourt toutes les regles de la grammaire i
			for(int j=0; j < grammars.get(i).getRules().size(); j++) {
				// On parcourt toutes les categories de la regle j
				for(int k=0; k < grammars.get(i).getRules().get(j).size(); k++) {
					// Si la categorie k est la meme que celle passee en parametre
					if(grammars.get(i) != null) {
						if(grammars.get(i).getCategoryAt(j, k) != null) {
							if(category.getName().equals(grammars.get(i).getCategoryAt(j, k).getName())) {
								if(!list.contains(grammars.get(i))) {
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
	 * Methode renommant les categories dans les regles des grammaires.
	 * @param oldName(String): Le nom de la categorie a renommer.
	 * @param newName(String): Le nouveau nom de la categorie.
	 **/
	public void renameCategoryInGrammars(String oldName, String newName) {
		
		Category cat = this.categories.get(this.getCategoryByName(oldName));
		
		// On parcourt toutes les grammaires
		for(int i=0 ; i < grammars.size() ; i++) {
			// On parcourt toutes les regles de la grammaire i
			for(int j=0; j < grammars.get(i).getRules().size(); j++) {
				// On parcourt toutes les categories de la regle j
				for(int k=0; k < grammars.get(i).getRules().get(j).size(); k++) {
					// Si la categorie k est la meme que celle passee en parametre
					if(grammars.get(i) != null) {
						if(grammars.get(i).getCategoryAt(j, k) != null) {
							if(cat.getName().equals(grammars.get(i).getCategoryAt(j, k).getName())) {
								// Renomme la categorie
								grammars.get(i).getCategoryAt(j, k).rename(newName);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Methode supprimant une grammaire.
	 * @param name(String): Le nom de la grammaire.
	 **/
	public void deleteGrammar(String name){
		int size = this.grammars.size();
		for(int i=0 ; i < size ; i++){
			String grammarName = this.grammars.get(i).getName();
			if(name.equals(grammarName)){
				grammars.remove(i);
				break;
			}
		}
	}
	
	/*========================================			 PROFILES			==============================================*/
	
	/**
	 * Methode retournant les profils charges.
	 * @return Les profils.
	 **/
	public List<Child> getProfils() {
		return this.profils;
	}
	
	/**
	 * Methode indiquant si oui ou non les profils ont ete charges.
	 * @param newProfils(ArrayList<Child>): Les nouveaux profils a charger.
	 * @return "true" si les profils ont pu etre mis a jour (c-a-d: newProfils != "null") OU "false" dans le cas contraire.
	 **/
	public boolean setProfils(ArrayList<Child> newProfils) {
		if (newProfils != null) {
			this.profils = newProfils;
			return true;
		}
		return false;
	}
	
	/**
	 * Methode ajoutant un nouveau profil
	 * @param newProfil(Child): Le nouveau profil.
	 * **/
	public void addProfil(Child newProfil) {
		this.profils.add(newProfil);
	}

	/**
	 * Methode supprimant un profil.
	 * @param nom(String): Le nom du profil.
	 * @param prenom(String): Le prenom du profil.
	 * **/
	public void deleteProfil(String nom, String prenom){
		int size = this.profils.size();
		for(int i=0 ; i < size ; i++){
			String nomProfil = this.profils.get(i).getName();
			String prenomProfil = this.profils.get(i).getFirstname();
			if(nom.equals(nomProfil) && prenom.equals(prenomProfil)){
				profils.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Methode retournant l'enfant ayant un certain nom.
	 * @param nom(String): Le nom du profil.
	 * @param prenom(String): Le prenom du profil.
	 * **/
	public Child getChildByName(String nom, String prenom){
		Child c = null;
		int size = this.profils.size();
		for(int i=0 ; i < size ; i++){
			String nomProfil = this.profils.get(i).getName();
			String prenomProfil = this.profils.get(i).getFirstname();
			if(nom.equals(nomProfil) && prenom.equals(prenomProfil)){
				c = profils.get(i);
			}
		}
		return c;
	}
}
