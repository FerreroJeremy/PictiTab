package com.pictitab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Child;
import com.pictitab.data.Entry;
import com.pictitab.data.Grammar;
import com.pictitab.data.Lexicon;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class XMLTools {
	
	// TODO Penser a valider les XML avec les DTD dans un soucis de rigueur
	// Bien qu'inutile etant donne que l'on gere nous meme leur edition et lecture
	
	/**
	 * Methode permettant de creer un fichier XML vide
	 * @param path(String): Le chemin ou creer le fichier XML.
	 * @param root(String): La racine de l'arbre du fichier XML.
	 **/
	public static void createEmptyXML(String path, String root) {
		File newxmlfile = new File(path);
		try{
			newxmlfile.createNewFile();
		} catch(IOException e) {
			Log.e("IOException", "Exception in create new File(");
		}
		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(newxmlfile);
        } catch(FileNotFoundException e) {
        	Log.e("FileNotFoundException",e.toString());
        }
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOS, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, root);
			serializer.endTag(null,root);
			serializer.endDocument();
			serializer.flush();
			fileOS.close();
		} catch(Exception e) {
			Log.e("Exception","Exception occured in wroting");
		}
	}
	
	/*========================================			CATEGORIES			==============================================*/
	
	/**
	 * Methode permettant l'extraction des informations contenues dans un arbre DOM
	 * en les presentant dans un tableau de categories.
	 * @param n(Node): Le Noeud racine de l'arbre DOM pour les categories.
	 * @return Un tableau de categories.
	 **/
	public static ArrayList<Category> DOMToCategories(Node n) {
		// Si on ne se trouve pas a la racine de l'arbre, on s'arrete
		if (!n.getNodeName().equals("categories")) {
			return new ArrayList<Category>();
		}

		ArrayList<Category> categories = new ArrayList<Category>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("categorie")) {
					String name = e.getAttributes().getNamedItem("nom").getTextContent();
					NodeList subNodes = e.getChildNodes();
					if(subNodes.getLength() == 0)
						categories.add(new Category(name));
					else
						categories.add(new Category(name, buildSubCategories(subNodes)));
				}
			} else if (nodes.item(i) instanceof Document) {
				NodeList fils = nodes.item(i).getChildNodes();
				for (int j = 0; j < fils.getLength(); j++) {
					categories.addAll(DOMToCategories(fils.item(j)));
				}
			}
		}
		return categories;
	}
	
	/**
	 * Methode parcourant recursivement les sous-categories pour construire la categorie mere.
	 * @param nodes(NodeList): La liste des noeuds de la categorie mere.
	 * @return Une liste de sous-categories.
	 **/
	public static List<Category> buildSubCategories(NodeList nodes) {
		int size = nodes.getLength();
		if (size == 0) {
			return null;
		}
		
		List<Category> subCategories = new ArrayList<Category>();
		
		for (int i = 0; i < size ; i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("categorie")) {
					String name = e.getAttributes().getNamedItem("nom").getTextContent();
					NodeList subNodes = e.getChildNodes();
					subCategories.add(new Category(name, buildSubCategories(subNodes)));
				}
			}
		}
		
		return subCategories;
	}
	
	/**
	 * Methode permettant de charger les categories.
	 * @param context(Context): Le contexte de l'application.
	 * @return Un tableau contenant les categories OU "null" si ces dernieres n'ont pu etre chargees.
	 **/
	public static ArrayList<Category> loadCategory(Context context) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() + File.separator + "data" + File.separator+"default"+File.separator+"categorie.xml";
		
		File categoryFile =new File(path);
		Document doc;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputStream profilsXML = null;
			try {
				profilsXML = new FileInputStream(categoryFile);
			} catch (IOException e) {
				Log.e("XMLTools", "Le fichier \""+categoryFile+"\" n'a pas pu etre lu.");
				e.printStackTrace();
			}

			// if(db.isValidating())
			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToCategories(doc.getDocumentElement());
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) 				   { e.printStackTrace(); }
		catch (IOException e) 				   { e.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Methode permettant d'afficher les categories.
	 * @param context(Context): Le contexte de l'application.
	 * @param categories(List<Category>): La liste des categories.
	 **/
	public static void printCategories(Context context, List<Category> categories) {
		String path =Environment.getExternalStorageDirectory() + File.separator+ "Android" + File.separator + "data" + File.separator+
				context.getApplicationContext().getPackageName()+ File.separator + "data" + File.separator + "default" + File.separator + "categorie.xml";
		
		File newxmlfile = new File(path);
		try { newxmlfile.createNewFile(); }
		catch(IOException e) { Log.e("IOException", "Exception in create new File("); }
		
		FileOutputStream fileOs = null;
		try { fileOs = new FileOutputStream(newxmlfile); }
		catch(FileNotFoundException e) { Log.e("FileNotFoundException",e.toString()); }
		
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "categories");
			for(int i=0;i<categories.size(); i++) {
				printSubCategories(serializer, categories.get(i));
			}
			serializer.endTag(null,"categories");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		}
		catch(Exception e) { Log.e("Exception","Exception occured in wroting"); }
	}
	
	/**
	 * Methode permettant d'afficher les sous-categories.
	 * @param serializer(XmlSerializer): L'interface de serialisation des donnees XML.
	 * @param category(Category): La categorie cible.
	 **/
	private static void printSubCategories(XmlSerializer serializer, Category category) {
		try {
			serializer.startTag(null, "categorie");
			serializer.attribute(null, "nom", category.getName());
			
			int size = category.getCategories().size();
			for(int i=0; i < size ; i++) {
				printSubCategories(serializer, category.getCategory(i));
			}
			serializer.endTag(null,"categorie");
			serializer.flush();
		}
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalStateException e)    { e.printStackTrace(); }
		catch (IOException e)              { e.printStackTrace(); }
	}
	
	/*========================================			 LEXICON			==============================================*/
	
	/**
	 * Methode permettant l'extraction des informations contenues dans un arbre DOM
	 * en les presentant dans un tableau des entrees du lexique.
	 * @param n(Node): Le Noeud racine de l'arbre DOM pour les entrees du lexique.
	 * @return Un tableau des entrees du lexique.
	 **/
	public static List<Lexicon> DOMToLexicon(Node n, List<Category> categories) {
		// Si on ne se trouve pas a la racine de l'arbre, on s'arrete
		if (!n.getNodeName().equals("lexiques")) {
			return new ArrayList<Lexicon>();
		}

		ArrayList<Lexicon> lexiques = new ArrayList<Lexicon>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("lexique")) {
					NodeList tmp = e.getChildNodes();
					String mot =tmp.item(0).getTextContent();
					String src =tmp.item(1).getAttributes().item(0).getTextContent();
					Category categorie = DataTools.searchCategory(categories, tmp.item(2).getTextContent());
					lexiques.add(new Lexicon(mot, src, categorie));
				}
			} else if (nodes.item(i) instanceof Document) {
				NodeList fils = nodes.item(i).getChildNodes();
				for (int j = 0; j < fils.getLength(); j++) {
					lexiques.addAll(DOMToLexicon(fils.item(j), categories));
				}
			}
		}
		return lexiques;
	}
	
	/**
	 * Methode permettant de charger le lexique.
	 * @param context(Context): Le contexte de l'application.
	 * @return Un tableau contenant le lexique OU "null" si ce dernier n'a pu etre charge.
	 **/
	public static List<Lexicon> loadLexicon(Context context, List<Category> categories) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() +File.separator+ "data" +File.separator+ "default" +File.separator+ "lexique.xml";
		
		File profilFile =new File(path);
		Document doc;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputStream profilsXML = null;
			try {
				profilsXML = new FileInputStream(profilFile);
			} catch (IOException e) {
				Log.e("XMLTools", "Le fichier \""+path+"\" n'a pas pu etre lu.");
				e.printStackTrace();
			}

			// if(db.isValidating())
			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToLexicon(doc.getDocumentElement(), categories);
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) 				   { e.printStackTrace(); }
		catch (IOException e) 				   { e.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Methode permettant d'ecrire les lexiques.
	 * @param context(Context): Le contexte de l'application.
	 * @param profils(List<Lexicon>): La liste des lexiques.
	 **/
	public static void printLexicon(Context context, List<Lexicon> lexicons) {
		String path =Environment.getExternalStorageDirectory() + File.separator+ "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName()+ File.separator + "data" + File.separator + "default" + File.separator + "lexique.xml";
		
		File newxmlfile = new File(path);
		try { newxmlfile.createNewFile(); }
		catch(IOException e) { Log.e("IOException", "Exception in create new File(\"lexique.xml\")"); }
		
		FileOutputStream fileOs = null;
		try { fileOs = new FileOutputStream(newxmlfile); }
		catch(FileNotFoundException e) { Log.e("FileNotFoundException",e.toString()); }
		
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "lexiques");
			for(int i=0;i<lexicons.size(); i++) {
				serializer.startTag(null, "lexique");
				serializer.startTag(null, "mot");
				serializer.text(lexicons.get(i).getWord());
				serializer.endTag(null,"mot");
				serializer.startTag(null, "img");
				serializer.attribute(null, "src", lexicons.get(i).getPictureSource());
				serializer.endTag(null,"img");
				serializer.startTag(null, "categorie");
				serializer.text(lexicons.get(i).getCategory().getName());
				serializer.endTag(null,"categorie");
				serializer.endTag(null,"lexique");
				serializer.flush();
			}
			serializer.endTag(null,"lexiques");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		}
		catch(Exception e) { Log.e("Exception","Exception occured in wroting \""+path+"\""); }
	}
	
	/*========================================			 GRAMMARS			==============================================*/
	
	/**
	 * Methode permettant l'extraction des informations contenues dans un arbre DOM
	 * en les presentant dans un tableau de grammaires.
	 * @param n(Node): Le Noeud racine de l'arbre DOM pour les grammaires.
	 * @return Un tableau de grammaires.
	 **/
	public static ArrayList<Grammar> DOMToGrammars(Node n, List<Category> categories) {
		// Si on ne se trouve pas a la racine de l'arbre, on s'arrete
		if (!n.getNodeName().equals("grammaires")) {
			return new ArrayList<Grammar>();
		}

		ArrayList<Grammar> grammaires = new ArrayList<Grammar>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("grammaire")) {
					String name = e.getAttributes().getNamedItem("nom").getTextContent();
					NodeList rulesNodes = e.getChildNodes();
					grammaires.add(new Grammar(name, buildRules(rulesNodes, categories)));
				}
			} else if (nodes.item(i) instanceof Document) {
				NodeList fils = nodes.item(i).getChildNodes();
				for (int j = 0; j < fils.getLength(); j++) {
					grammaires.addAll(DOMToGrammars(fils.item(j), categories));
				}
			}
		}
		return grammaires;
	}
	
	/**
	 * Methode construisant les regles d'une grammaire.
	 * @param nodes(NodeList): La liste des regles de la grammaire.
	 * @return Une liste de regles.
	 **/
	public static ArrayList<ArrayList<Category>> buildRules(NodeList nodes, List<Category> categories) {
		int size = nodes.getLength();
		if (size < 0) {
			return new ArrayList<ArrayList<Category>>();
		}
		
		ArrayList<ArrayList<Category>> grammar = new ArrayList<ArrayList<Category>>();
		
		for (int i = 0; i < size ; i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("regle")) {
					NodeList categoriesNodes = e.getChildNodes();
					ArrayList<Category> rule = new ArrayList<Category>();
					for (int j=0 ; j < categoriesNodes.getLength() ; j++){
						if (categoriesNodes.item(j) instanceof Element) {
							Element ee = ((Element) categoriesNodes.item(j));
							if (ee.getTagName().equals("categorie")) {
								String name = ee.getAttributes().getNamedItem("nom").getTextContent();
								Category c = DataTools.searchCategory(categories, name);
								rule.add(c);
							}
						}
					}
					grammar.add(rule);
				}
			}
		}
		return grammar;
	}
	
	/**
	 * Methode permettant de charger les grammaires.
	 * @param context(Context): Le contexte de l'application.
	 * @return Un tableau contenant les grammaires OU "null" si ces dernieres n'ont pu etre chargees.
	 **/
	public static ArrayList<Grammar> loadGrammar(Context context, List<Category> categories) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() + File.separator + "data" + File.separator + "default" + File.separator + "grammaire.xml";
		
		File grammarFile =new File(path);
		Document doc;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputStream profilsXML = null;
			try {
				profilsXML = new FileInputStream(grammarFile);
			} catch (IOException e) {
				Log.e("XMLTools", "Le fichier \""+grammarFile+"\" n'a pas pu etre lu.");
				e.printStackTrace();
			}

			// if(db.isValidating())
			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToGrammars(doc.getDocumentElement(), categories);
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) 				   { e.printStackTrace(); }
		catch (IOException e) 				   { e.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Methode permettant d'afficher les grammaires.
	 * @param context(Context): Le contexte de l'application.
	 * @param grammars(List<Grammar>): La liste des grammaires.
	 **/
	public static void printGrammars(Context context, List<Grammar> grammars) {
		String path =Environment.getExternalStorageDirectory() + File.separator+ "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName()+ File.separator + "data" + File.separator+"default"+File.separator+"grammaire.xml";
		
		File newxmlfile = new File(path);
		try { newxmlfile.createNewFile(); }
		catch(IOException e) { Log.e("IOException", "Exception in create new File("); }
		
		FileOutputStream fileOs = null;
		try { fileOs = new FileOutputStream(newxmlfile); }
		catch(FileNotFoundException e) { Log.e("FileNotFoundException",e.toString()); }
		
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "grammaires");
			for(int i=0;i<grammars.size(); i++) {
				printGrammar(serializer, grammars.get(i));
			}
			serializer.endTag(null,"grammaires");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		}
		catch(Exception e) { Log.e("Exception","Exception occured in wroting"); }
	}
	
	/**
	 * Methode permettant d'afficher la grammaire.
	 * @param serializer(XmlSerializer): L'interface de serialisation des donnees XML.
	 * @param grammar(Grammar): La grammaire cible.
	 **/
	private static void printGrammar(XmlSerializer serializer, Grammar grammar) {
		try {
			serializer.startTag(null, "grammaire");
			serializer.attribute(null, "nom", grammar.getName());
			
			int size = grammar.getRules().size();
			if (size > 0) {
				for (int i=0; i < size ; i++) {
					serializer.startTag(null, "regle");
					for (int j=0 ; j < grammar.getRuleAt(i).size() ; j++) {
						serializer.startTag(null, "categorie");
						serializer.attribute(null, "nom", grammar.getCategoryAt(i, j).getName());
						serializer.endTag(null,"categorie");
					}
					serializer.endTag(null,"regle");
				}
			}
			serializer.endTag(null,"grammaire");
			serializer.flush();
		}
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalStateException e)    { e.printStackTrace(); }
		catch (IOException e)              { e.printStackTrace(); }
	}
	
	/*========================================			 PROFILES			==============================================*/
	
	/**
	 * Methode permettant l'extraction des informations contenues dans un arbre DOM
	 * en les presentant dans un tableau de profils.
	 * @param n(Node): Le Noeud racine de l'arbre DOM pour les profils.
	 * @param data(AppData): Les donnees de l'application
	 * @return Un tableau de profils.
	 **/
	public static ArrayList<Child> DOMToProfils(Node n, AppData data) {
		// Si on ne se trouve pas a la racine de l'arbre, on s'arrete
		if (!n.getNodeName().equals("enfants")) {
			return new ArrayList<Child>();
		}

		ArrayList<Child> profils = new ArrayList<Child>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("enfant")) {
					String nom, prenom, photo;
					int jour =0, mois =0, annee =0;
					ArrayList<Grammar> grammars = new ArrayList<Grammar>();

					NodeList tmp = e.getChildNodes();
					int indice = 0;
					if (tmp.getLength() >= 4) {
						nom = tmp.item(indice++).getTextContent();
						prenom = tmp.item(indice++).getTextContent();
						
						String date = tmp.item(indice++).getTextContent();
						if (date.length() >= 10) {
							jour = Integer.parseInt(new String(""
									+ String.valueOf(date.charAt(0))
									+ String.valueOf(date.charAt(1))));
							mois = Integer.parseInt(new String(""
									+ String.valueOf(date.charAt(3))
									+ String.valueOf(date.charAt(4))));
							annee = Integer.parseInt(new String(""
									+ String.valueOf(date.charAt(6))
									+ String.valueOf(date.charAt(7))
									+ String.valueOf(date.charAt(8))
									+ String.valueOf(date.charAt(9))));
						}
						photo =tmp.item(indice++).getAttributes().getNamedItem("src").getTextContent();
						
						while(tmp.item(indice) != null) {
							if (tmp.item(indice) instanceof Element) {
								Element ee = ((Element) tmp.item(indice));
								if (ee.getTagName().equals("grammaire")) {
									Grammar g = data.getGrammarByName(tmp.item(indice++).getAttributes().getNamedItem("nom").getTextContent());
									grammars.add(g);
								}
							}
						}						
						profils.add(new Child(nom, prenom, jour, mois, annee, photo, grammars));
					}
				}
			} else if (nodes.item(i) instanceof Document) {
				NodeList fils = nodes.item(i).getChildNodes();
				for (int j = 0; j < fils.getLength(); j++) {
					profils.addAll(DOMToProfils(fils.item(j), data));
				}
			}
		}
		return profils;
	}

	/**
	 * Methode permettant de charger les profils.
	 * @param context(Context): Le contexte de l'application.
	 * @return Un tableau contenant les profils OU "null" si ces derniers n'ont pu etre charges.
	 **/
	public static ArrayList<Child> loadProfil(Context context, AppData data) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() + File.separator + "data" + File.separator+"enfant.xml";
		
		File profilFile =new File(path);
		Document doc;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputStream profilsXML = null;
			try {
				profilsXML = new FileInputStream(profilFile);
			} catch (IOException e) {
				Log.e("XMLTools", "Le fichier \""+profilFile+"\" n'a pas pu etre lu.");
				e.printStackTrace();
			}

			// if(db.isValidating())
			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToProfils(doc.getDocumentElement(), data);
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) 				   { e.printStackTrace(); }
		catch (IOException e) 				   { e.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Methode permettant d'afficher les profils.
	 * @param context(Context): Le contexte de l'application.
	 * @param profils(List<Child>): La liste des profils.
	 **/
	public static void printChildren(Context context, List<Child> profils) {
		String path =Environment.getExternalStorageDirectory() + File.separator+ "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName()+ File.separator + "data" + File.separator+"enfant.xml";
		
		File newxmlfile = new File(path);
		try { newxmlfile.createNewFile(); }
		catch(IOException e) { Log.e("IOException", "Exception in create new File("); }
		
		FileOutputStream fileOs = null;
		try { fileOs = new FileOutputStream(newxmlfile); }
		catch(FileNotFoundException e) { Log.e("FileNotFoundException",e.toString()); }
		
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "enfants");
			for(int i=0;i<profils.size(); i++) {
				serializer.startTag(null, "enfant");
				serializer.startTag(null, "nom");
				serializer.text(profils.get(i).getName());
				serializer.endTag(null,"nom");
				serializer.startTag(null, "prenom");
				serializer.text(profils.get(i).getFirstname());
				serializer.endTag(null,"prenom");
				serializer.startTag(null, "birthdate");
				serializer.text(	 String.format("%02d", profils.get(i).getBirthday())
								+"/"+String.format("%02d", profils.get(i).getBirthmonth())
								+"/"+String.format("%04d", profils.get(i).getBirthyear()));
				serializer.endTag(null,"birthdate");
				serializer.startTag(null, "photo");
				serializer.attribute(null, "src", profils.get(i).getPhoto());
				serializer.endTag(null,"photo");
				ArrayList<Grammar> grammars = profils.get(i).getGrammars();
				if(grammars != null) {
					for(int j=0 ; j < grammars.size() ; j++) {
						String name = grammars.get(j).getName();
						serializer.startTag(null, "grammaire");
						serializer.attribute(null, "nom", name);
						serializer.endTag(null, "grammaire");
					}
				}
				
				serializer.endTag(null,"enfant");
				serializer.flush();
			}
			serializer.endTag(null,"enfants");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		}
		catch(Exception e) { Log.e("Exception","Exception occured in wroting"); }
	}
	
	/*========================================			   LOGS				==============================================*/
	
	/**
	 * Methode permettant l'extraction des informations contenues dans un arbre DOM
	 * en les presentant dans un tableau de logs.
	 * @param n(Node): Le Noeud racine de l'arbre DOM pour les logs.
	 * @return Un tableau de logs.
	 **/
	public static ArrayList<Entry> DOMToLogs(Node n, AppData data) {
		// Si on ne se trouve pas a la racine de l'arbre, on s'arrete
		if (!n.getNodeName().equals("logs")) {
			return new ArrayList<Entry>();
		}

		ArrayList<Entry> logs = new ArrayList<Entry>();
		NodeList nodes = n.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("entree")) {
					String date = e.getAttributes().getNamedItem("date").getTextContent();
					
					Node sequenceNodes = e.getFirstChild();
					if (sequenceNodes instanceof Element) {
						Element ee = ((Element) sequenceNodes);
						if (ee.getTagName().equals("sequence")) {
							NodeList wordsNodes = ee.getChildNodes();
							ArrayList<Lexicon> sequence = new ArrayList<Lexicon>();
							for (int j=0 ; j < wordsNodes.getLength() ; j++){
								if (wordsNodes.item(j) instanceof Element) {
									Element eee = ((Element) wordsNodes.item(j));
									if (eee.getTagName().equals("mot")) {
										String name = eee.getAttributes().getNamedItem("nom").getTextContent();
										Lexicon l = data.getWordByName(name);
										sequence.add(l);
									}
								}
							}
							logs.add(new Entry(date, sequence));
						}
					}
				}
			} else if (nodes.item(i) instanceof Document) {
				NodeList fils = nodes.item(i).getChildNodes();
				for (int j = 0; j < fils.getLength(); j++) {
					logs.addAll(DOMToLogs(fils.item(j), data));
				}
			}
		}
		return logs;
	}
	
	/**
	 * Methode permettant de charger les logs.
	 * @param context(Context): Le contexte de l'application.
	 * @param name(String): Le nom de l'enfant.
	 * @param firstname(String): Le prenom de l'enfant.
	 * @return Un tableau contenant les logs d'un enfant precis.
	 **/
	public static ArrayList<Entry> loadLogs(Context context, String name, String firstname, AppData data) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() + File.separator + "log" + File.separator + name + "_" + firstname + ".xml";
		
		File filePath =new File(path);
		Document doc;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);

		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputStream profilsXML = null;
			try {
				profilsXML = new FileInputStream(filePath);
			} catch (IOException e) {
				Log.e("XMLTools", "Le fichier \""+filePath+"\" n'a pas pu etre lu.");
				e.printStackTrace();
			}

			// if(db.isValidating())
			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToLogs(doc.getDocumentElement(), data);
		}
		catch (ParserConfigurationException e) { e.printStackTrace(); }
		catch (SAXException e) 				   { e.printStackTrace(); }
		catch (IOException e) 				   { e.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Methode permettant d'afficher les logs.
	 * @param context(Context): Le contexte de l'application.
	 * @param name(String): Le nom de l'enfant.
	 * @param firstname(String): Le prenom de l'enfant.
	 * @param logs(List<Entry>): La liste des logs.
	 **/
	public static void printLogs(Context context, List<Entry> logs, String name, String firstname) {
		String path =Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator +
				context.getApplicationContext().getPackageName() + File.separator + "log" + File.separator + name + "_" + firstname + ".xml";
		
		File newxmlfile = new File(path);
		try { newxmlfile.createNewFile(); }
		catch(IOException e) { Log.e("IOException", "Exception in create new File("); }
		
		FileOutputStream fileOs = null;
		try { fileOs = new FileOutputStream(newxmlfile); }
		catch(FileNotFoundException e) { Log.e("FileNotFoundException",e.toString()); }
		
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "logs");
			for(int i=0;i<logs.size(); i++) {
				Entry entry = logs.get(i);
				try {
					serializer.startTag(null, "entree");
					serializer.attribute(null, "date", entry.getDate());

					serializer.startTag(null, "sequence");
					for (int j=0 ; j < entry.getSequence().size() ; j++) {
						serializer.startTag(null, "mot");
						serializer.attribute(null, "nom", entry.getWordAt(j).getWord());
						serializer.endTag(null,"mot");
					}
					serializer.endTag(null,"sequence");
						
					serializer.endTag(null,"entree");
					serializer.flush();
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				catch (IllegalStateException e)    { e.printStackTrace(); }
				catch (IOException e)              { e.printStackTrace(); }
			
			}
			serializer.endTag(null,"logs");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		}
		catch(Exception e) { Log.e("Exception","Exception occured in wroting"); }
	}
}
