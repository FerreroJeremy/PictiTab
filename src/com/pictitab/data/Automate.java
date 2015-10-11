package com.pictitab.data;

import java.util.ArrayList;
import java.util.List;

public class Automate {

	// Les informations relatives a l'automate qui met a jour les mots disponibles en fonction :
	//    - des grammaires autorisees a l'enfant
	//    - de l'etat de la phrase
	private String gramName;					// Le nom de la grammaire
	private ArrayList<Node> rules;				// Les regles associees
	private ArrayList<Integer> eligibleRules;	// Les numeros des regles associees
	private ArrayList<Node> currentNodes;		// Les noeuds courants dans l'etat present de automate correspondants aux règles éligible
	private int position;						// Position du noeud courant
	
	// Les donnees de l'application
	AppData data;
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Automate. */
	public Automate() {
		this.gramName =new String("");
		this.rules =new ArrayList<Node>();
		this.eligibleRules =new ArrayList<Integer>();
		this.currentNodes =new ArrayList<Node>();
		this.position =-1;
	}
	
	/**
	 * Constructeur de la classe Automate.
	 * @param newRules(ArrayList<Node>): Les nouvelles regles de cet automate.
	 * @param newGramName(String): Le nom de la grammaire duquel est issu cet automate.
	 */
	public Automate(ArrayList<Node> newRules, String newGramName){
		this.rules = newRules;
		this.gramName = newGramName;
		this.eligibleRules =new ArrayList<Integer>();
		this.currentNodes =new ArrayList<Node>();
		this.position =-1;
		
		// Au debut toutes les regles sont eligibles.
		// Donc on initialise les regles eligibles.
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
	}
	
	/**
	 * Constructeur d'un automate a partir d'une grammaire et la liste de mots.
	 * @param gram(Grammar): La grammaire a partir de laquelle on doit construire l'automate.
	 * @param words(ArrayList<Lexicon>): La liste de tous les mots de l'application.
	 * @param data(AppData): Les donnees de l'application
	 */
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
		// Au debut toutes les regles sont eligibles.
		// Donc on initialise les regles eligibles.
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
	}
	
	/**
	 * Methode construisant la suite des noeuds representant une regle pour l'automate.
	 * @param rule(ArrayList<Category>): La liste des categories representant les regles de la grammaire.
	 * @param numState(int): L'indice de la profondeur du noeud courant dans la regle de la grammaire.
	 * @param words(List<Lexicon>): Les mots de l'application.
	 * @return Le noeud courant.
	 */
	private Node createNodesFromRule(ArrayList<Category> rule, int numState, List<Lexicon> words) {
		Node node =new Node();
		// Ce tableau nous sert a stocker les regles de la grammaire (le premier element du tableau sera toujours la categorie du noeud courant)
		ArrayList<Category> endRule =new ArrayList<Category>(rule);
		// Ce tableau sert a stocker les images-mots n'ayant pas encore ete attribues a un noeud.
		ArrayList<Lexicon> remainingWords =new ArrayList<Lexicon>(words);
		
		// On donne a notre noeud sa categorie, tout en la retirant des categories restantes.
		node.setCategory(endRule.remove(0));
		// On recupere les mots auxquels cette categorie nous donne acces:
		node.setWords(this.getWordsFromCategory(node.getCategory(), remainingWords));
		node.setState(numState);
		// Si il reste encore des regles a construire pour l'automate:
		if(endRule.size()>0) {
			Node son =this.createNodesFromRule(endRule, numState+1, remainingWords);
			son.setFather(node);
			ArrayList<Node> transitions =new ArrayList<Node>();
			transitions.add(son);
			node.setTransitions(transitions);
		}
		return node;
	}
	
	/*====================================================================================================================*/
	/*==											 TRAITEMENTS AUTOMATE												==*/
	/*====================================================================================================================*/
	
	/**
	 * Methode permettant de recuperer les mots pour une categorie donnee.
	 * @param c(Category): La categorie dont on doit recuperer les mots.
	 * @param words(ArrayList<Lexicon>): La liste de tout les mots.
	 * @return La liste des mots associes a cette categorie/regle.
	 */
	public ArrayList<Lexicon> getWordsFromCategory(Category c, ArrayList<Lexicon> words)
	{
		Category c2 = data.getCategories().get(data.getCategoryByName(c.getName()));
		// Tableau servant a contenir les mots de cette categorie.
		ArrayList<Lexicon> tmpWords =new ArrayList<Lexicon>();
		// On rappelle la methode sur toutes ses filles
		List<Category> subcategories = c2.getCategories();
		for(int i =0; i < subcategories.size(); i++) {
			ArrayList<Lexicon> tmpLex =getWordsFromCategory(subcategories.get(i), words);
			tmpWords.addAll(tmpLex);
		}
		// On verifie les mots appartenant a cette categorie
		for(int i =0; i < words.size(); i++) {
			if(words.get(i).getCategory().getName().equals(c.getName())) {
				// Si le mot appartient bien a la categorie, on l'ajoute
				tmpWords.add(words.get(i));
			}
		}
		return tmpWords;
	}
	
	/**
	 * Methode permettant d'avancer dans l'automate (Cette operation peut echouer).
	 * Cette methode met a jour les regles eligibles ainsi que les noeuds courants.
	 * @param CatName(String): Le nom de la categorie dans laquelle on souhaite aller.
	 * @return "true" si il y a bien une transition vers un etat appartenant a la categorie dont le nom est passe en argument OU "false" sinon.
	 */
	public boolean moveForwardToNextCat (String catName) {
		Node tmp;
		int index;
		ArrayList<Integer> rulesToDelete =new ArrayList<Integer>();
		boolean testForward =false;
		
		for(int i=0;i<this.eligibleRules.size();i++) {

			// Si on n'a jamais ete dans un etat.
			if(this.position==-1) {
				// On prend la premiere regle eligible.
				tmp =this.rules.get(this.eligibleRules.get(i));
				if(tmp.haveNextCategoryInFirstRules(catName, this.data)) {
					this.currentNodes.add(tmp);
					testForward =true;
				} else {
					rulesToDelete.add(this.eligibleRules.get(i));
				}
			} else {
				// On prend le noeud courant.
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
		// On test que l'avancement ai bien ete effectue.
		if(testForward)
			this.position++;

		return testForward;
	}
	
	/**
	 * Methode permettant de reculer dans l'automate.
	 * Cette methode met a jour les regles eligibles ainsi que les noeuds courants et la position de l'automate.
	 * @param sentence(ArrayList<Lexicon>): Les mots restants de la phrase.
	 */
	public void moveBackward(ArrayList<Lexicon> sentence) {
		
		// On fait table rase...
		this.eligibleRules.clear();
		this.currentNodes.clear();
		for(int i=0;i<this.rules.size();i++) {
			this.eligibleRules.add(i);
		}
		this.position =-1;
		
		// Et on simule l'automate pour les mots restant de la phrase.
		for(int i=0;i<sentence.size();i++) {
			this.moveForwardToNextCat(sentence.get(i).getCategory().getName());
		}
	}
	
	/**
	 * Methode permettant de recuperer toutes les images-mots a afficher pour passer dans le prochain etat de l'automate.
	 * @return Un tableau contenant toutes les images-mots possibles pour passer dans un etat suivant.
	 */
	public ArrayList<Lexicon> getWordsToDisplay() {
		ArrayList<Lexicon> words =new ArrayList<Lexicon>();
		ArrayList<Lexicon> tmpwords =new ArrayList<Lexicon>();
		Node tmp;
		if(this.position!=-1) {
			// Pour chaque regle eligible de l'automate, on recupere les images-mots.
			for(int i=0;i<this.eligibleRules.size();i++) {
				tmp =this.currentNodes.get(i);
				// On parcourt tous les prochains etats des regles eligibles.
				for(int j=0;j<tmp.getTransitions().size();j++) {
					// Et on recupere les mots permettant d'acceder a ces dernieres.
					tmpwords =tmp.getTransitions().get(j).getWords();
					// On supprime les doublons,
					words.removeAll(tmpwords);
					// puis on ajoute tout.
					words.addAll(tmpwords);
				}
			}
		} else {
			for(int i=0;i<this.rules.size();i++) {
				tmp =this.rules.get(i);
				tmpwords =tmp.getWords();
				// On supprime les doublons,
				words.removeAll(tmpwords);
				// puis on ajoute tout.
				words.addAll(tmpwords);
			}
		}
		return words;
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
	
	/**
	 * Renvoie les regles de l'automate.
	 * @return Les regles de l'automate.
	 **/
	public ArrayList<Node> getRules() {
		return rules;
	}
	
	/**
	 * Renseigne les regles de l'automate.
	 * @param rules(ArrayList<Node>): Les regles de l'automate.
	 **/
	public void setRules(ArrayList<Node> rules) {
		this.rules = rules;
	}
	
	/**
	 * Renvoie le nom de la grammaire de l'automate.
	 * @return Le nom de la grammaire de l'automate.
	 **/
	public String getGramName() {
		return gramName;
	}
	
	/**
	 * Renseigne le nom de la grammaire de l'automate.
	 * @param gramName(String): Le nom de la grammaire de l'automate.
	 **/
	public void setGramName(String gramName) {
		this.gramName = gramName;
	}
}
