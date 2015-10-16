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

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Child;
import com.pictitab.data.Entry;
import com.pictitab.data.Grammar;
import com.pictitab.data.Lexicon;

public class XMLTools {

	/**
	 * Create an empty XML file
	 * 
	 * @param path
	 *            (String): path where create the file.
	 * @param root
	 *            (String): XML tree root.
	 **/
	public static void createEmptyXML(String path, String root) {
		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {

		}
		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {

		}
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOS, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, root);
			serializer.endTag(null, root);
			serializer.endDocument();
			serializer.flush();
			fileOS.close();
		} catch (Exception e) {

		}
	}

	/*
	 * ======================================== CATEGORIES
	 * ==============================================
	 */

	/**
	 * Extract the information from DOM tree and constitute a list of extracted
	 * elements.
	 * 
	 * @param n
	 *            (Node): DOM tree root node.
	 * @return List of elements.
	 **/
	public static ArrayList<Category> DOMToCategories(Node n) {
		// If it is not the root of the tree, stop
		if (!n.getNodeName().equals("categories")) {
			return new ArrayList<Category>();
		}

		ArrayList<Category> categories = new ArrayList<Category>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("categorie")) {
					String name = e.getAttributes().getNamedItem("nom")
							.getTextContent();
					NodeList subNodes = e.getChildNodes();
					if (subNodes.getLength() == 0)
						categories.add(new Category(name));
					else
						categories.add(new Category(name,
								buildSubCategories(subNodes)));
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
	 * Build sub-categories to build the parent category.
	 * 
	 * @param nodes
	 *            (NodeList): Nodes list of the parent category.
	 * @return Sub-categories.
	 **/
	public static List<Category> buildSubCategories(NodeList nodes) {
		int size = nodes.getLength();
		if (size == 0) {
			return null;
		}

		List<Category> subCategories = new ArrayList<Category>();

		for (int i = 0; i < size; i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("categorie")) {
					String name = e.getAttributes().getNamedItem("nom")
							.getTextContent();
					NodeList subNodes = e.getChildNodes();
					subCategories.add(new Category(name,
							buildSubCategories(subNodes)));
				}
			}
		}

		return subCategories;
	}

	/**
	 * Load the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @return List of elements or null.
	 **/
	public static ArrayList<Category> loadCategory(Context context) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "categorie.xml";

		File categoryFile = new File(path);
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

			}

			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToCategories(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Write and save the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param categories
	 *            (List<Category>): List of elements.
	 **/
	public static void printCategories(Context context,
			List<Category> categories) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "categorie.xml";

		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
		}

		FileOutputStream fileOs = null;
		try {
			fileOs = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "categories");
			for (int i = 0; i < categories.size(); i++) {
				printSubCategories(serializer, categories.get(i));
			}
			serializer.endTag(null, "categories");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Write and save one element.
	 * 
	 * @param serializer
	 *            (XmlSerializer): Xml serializer.
	 * @param category
	 *            (Category): Element.
	 **/
	private static void printSubCategories(XmlSerializer serializer,
			Category category) {
		try {
			serializer.startTag(null, "categorie");
			serializer.attribute(null, "nom", category.getName());

			int size = category.getCategories().size();
			for (int i = 0; i < size; i++) {
				printSubCategories(serializer, category.getCategory(i));
			}
			serializer.endTag(null, "categorie");
			serializer.flush();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================== LEXICON
	 * ==============================================
	 */

	/**
	 * Extract the information from DOM tree and constitute a list of extracted
	 * elements.
	 * 
	 * @param n
	 *            (Node): DOM tree root node.
	 * @param categories
	 *            (List<Category>): Categories
	 * @return List of elements.
	 **/
	public static List<Lexicon> DOMToLexicon(Node n, List<Category> categories) {

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
					String mot = tmp.item(0).getTextContent();
					String src = tmp.item(1).getAttributes().item(0)
							.getTextContent();
					Category categorie = DataTools.searchCategory(categories,
							tmp.item(2).getTextContent());
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
	 * Load the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param categories
	 *            (List<Category>): Categories
	 * @return List of elements or null.
	 **/
	public static List<Lexicon> loadLexicon(Context context,
			List<Category> categories) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "lexique.xml";

		File profilFile = new File(path);
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
			}

			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToLexicon(doc.getDocumentElement(), categories);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Write and save the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param profils
	 *            (List<Lexicon>): List of elements.
	 **/
	public static void printLexicon(Context context, List<Lexicon> lexicons) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "lexique.xml";

		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
		}

		FileOutputStream fileOs = null;
		try {
			fileOs = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "lexiques");
			for (int i = 0; i < lexicons.size(); i++) {
				serializer.startTag(null, "lexique");
				serializer.startTag(null, "mot");
				serializer.text(lexicons.get(i).getWord());
				serializer.endTag(null, "mot");
				serializer.startTag(null, "img");
				serializer.attribute(null, "src", lexicons.get(i)
						.getPictureSource());
				serializer.endTag(null, "img");
				serializer.startTag(null, "categorie");
				serializer.text(lexicons.get(i).getCategory().getName());
				serializer.endTag(null, "categorie");
				serializer.endTag(null, "lexique");
				serializer.flush();
			}
			serializer.endTag(null, "lexiques");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		} catch (Exception e) {
		}
	}

	/*
	 * ======================================== GRAMMARS
	 * ==============================================
	 */

	/**
	 * Extract the information from DOM tree and constitute a list of extracted
	 * elements.
	 * 
	 * @param n
	 *            (Node): DOM tree root node.
	 * @param categories
	 *            (List<Category>): Categories
	 * @return List of elements.
	 **/
	public static ArrayList<Grammar> DOMToGrammars(Node n,
			List<Category> categories) {

		if (!n.getNodeName().equals("grammaires")) {
			return new ArrayList<Grammar>();
		}

		ArrayList<Grammar> grammaires = new ArrayList<Grammar>();
		NodeList nodes = n.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("grammaire")) {
					String name = e.getAttributes().getNamedItem("nom")
							.getTextContent();
					NodeList rulesNodes = e.getChildNodes();
					grammaires.add(new Grammar(name, buildRules(rulesNodes,
							categories)));
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
	 * Build the grammar rules.
	 * 
	 * @param nodes
	 *            (NodeList): Rules list of the grammar.
	 * @return List of rules.
	 **/
	public static ArrayList<ArrayList<Category>> buildRules(NodeList nodes,
			List<Category> categories) {
		int size = nodes.getLength();
		if (size < 0) {
			return new ArrayList<ArrayList<Category>>();
		}

		ArrayList<ArrayList<Category>> grammar = new ArrayList<ArrayList<Category>>();

		for (int i = 0; i < size; i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("regle")) {
					NodeList categoriesNodes = e.getChildNodes();
					ArrayList<Category> rule = new ArrayList<Category>();
					for (int j = 0; j < categoriesNodes.getLength(); j++) {
						if (categoriesNodes.item(j) instanceof Element) {
							Element ee = ((Element) categoriesNodes.item(j));
							if (ee.getTagName().equals("categorie")) {
								String name = ee.getAttributes()
										.getNamedItem("nom").getTextContent();
								Category c = DataTools.searchCategory(
										categories, name);
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
	 * Load the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param categories
	 *            (List<Category>): Categories
	 * @return List of elements or null.
	 **/
	public static ArrayList<Grammar> loadGrammar(Context context,
			List<Category> categories) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "grammaire.xml";

		File grammarFile = new File(path);
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
			}

			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToGrammars(doc.getDocumentElement(), categories);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Write and save the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param grammars
	 *            (List<Grammar>): List of elements.
	 **/
	public static void printGrammars(Context context, List<Grammar> grammars) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "default"
				+ File.separator + "grammaire.xml";

		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
		}

		FileOutputStream fileOs = null;
		try {
			fileOs = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "grammaires");
			for (int i = 0; i < grammars.size(); i++) {
				printGrammar(serializer, grammars.get(i));
			}
			serializer.endTag(null, "grammaires");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Write and save one element.
	 * 
	 * @param serializer
	 *            (XmlSerializer): Xml serializer.
	 * @param grammar
	 *            (Grammar): Element.
	 **/
	private static void printGrammar(XmlSerializer serializer, Grammar grammar) {
		try {
			serializer.startTag(null, "grammaire");
			serializer.attribute(null, "nom", grammar.getName());

			int size = grammar.getRules().size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					serializer.startTag(null, "regle");
					for (int j = 0; j < grammar.getRuleAt(i).size(); j++) {
						serializer.startTag(null, "categorie");
						serializer.attribute(null, "nom", grammar
								.getCategoryAt(i, j).getName());
						serializer.endTag(null, "categorie");
					}
					serializer.endTag(null, "regle");
				}
			}
			serializer.endTag(null, "grammaire");
			serializer.flush();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ======================================== PROFILES
	 * ==============================================
	 */

	/**
	 * Extract the information from DOM tree and constitute a list of extracted
	 * elements.
	 * 
	 * @param n
	 *            (Node): DOM tree root node.
	 * @param data
	 *            (AppData): Data
	 * @return List of elements.
	 **/
	public static ArrayList<Child> DOMToProfils(Node n, AppData data) {
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
					int jour = 0, mois = 0, annee = 0;
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
						photo = tmp.item(indice++).getAttributes()
								.getNamedItem("src").getTextContent();

						while (tmp.item(indice) != null) {
							if (tmp.item(indice) instanceof Element) {
								Element ee = ((Element) tmp.item(indice));
								if (ee.getTagName().equals("grammaire")) {
									Grammar g = data.getGrammarByName(tmp
											.item(indice++).getAttributes()
											.getNamedItem("nom")
											.getTextContent());
									grammars.add(g);
								}
							}
						}
						profils.add(new Child(nom, prenom, jour, mois, annee,
								photo, grammars));
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
	 * Load the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param data
	 *            (AppData): Data
	 * @return List of elements or null.
	 **/
	public static ArrayList<Child> loadProfil(Context context, AppData data) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "enfant.xml";

		File profilFile = new File(path);
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
			}

			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToProfils(doc.getDocumentElement(), data);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Write and save the elements.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param profils
	 *            (List<Child>): List of elements.
	 **/
	public static void printChildren(Context context, List<Child> profils) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "data" + File.separator + "enfant.xml";

		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
		}

		FileOutputStream fileOs = null;
		try {
			fileOs = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "enfants");
			for (int i = 0; i < profils.size(); i++) {
				serializer.startTag(null, "enfant");
				serializer.startTag(null, "nom");
				serializer.text(profils.get(i).getName());
				serializer.endTag(null, "nom");
				serializer.startTag(null, "prenom");
				serializer.text(profils.get(i).getFirstname());
				serializer.endTag(null, "prenom");
				serializer.startTag(null, "birthdate");
				serializer.text(String.format("%02d", profils.get(i)
						.getBirthday())
						+ "/"
						+ String.format("%02d", profils.get(i).getBirthmonth())
						+ "/"
						+ String.format("%04d", profils.get(i).getBirthyear()));
				serializer.endTag(null, "birthdate");
				serializer.startTag(null, "photo");
				serializer.attribute(null, "src", profils.get(i).getPhoto());
				serializer.endTag(null, "photo");
				ArrayList<Grammar> grammars = profils.get(i).getGrammars();
				if (grammars != null) {
					for (int j = 0; j < grammars.size(); j++) {
						String name = grammars.get(j).getName();
						serializer.startTag(null, "grammaire");
						serializer.attribute(null, "nom", name);
						serializer.endTag(null, "grammaire");
					}
				}

				serializer.endTag(null, "enfant");
				serializer.flush();
			}
			serializer.endTag(null, "enfants");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		} catch (Exception e) {
		}
	}

	/*
	 * ======================================== LOGS
	 * ==============================================
	 */

	/**
	 * Extract the information from DOM tree and constitute a list of logs.
	 * 
	 * @param n
	 *            (Node): DOM tree root node.
	 * @param data
	 *            (AppData): Data.
	 * @return Logs.
	 */
	public static ArrayList<Entry> DOMToLogs(Node n, AppData data) {
		if (!n.getNodeName().equals("logs")) {
			return new ArrayList<Entry>();
		}

		ArrayList<Entry> logs = new ArrayList<Entry>();
		NodeList nodes = n.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element e = ((Element) nodes.item(i));
				if (e.getTagName().equals("entree")) {
					String date = e.getAttributes().getNamedItem("date")
							.getTextContent();

					Node sequenceNodes = e.getFirstChild();
					if (sequenceNodes instanceof Element) {
						Element ee = ((Element) sequenceNodes);
						if (ee.getTagName().equals("sequence")) {
							NodeList wordsNodes = ee.getChildNodes();
							ArrayList<Lexicon> sequence = new ArrayList<Lexicon>();
							for (int j = 0; j < wordsNodes.getLength(); j++) {
								if (wordsNodes.item(j) instanceof Element) {
									Element eee = ((Element) wordsNodes.item(j));
									if (eee.getTagName().equals("mot")) {
										String name = eee.getAttributes()
												.getNamedItem("nom")
												.getTextContent();
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
	 * Load the logs.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param name
	 *            (String): last name of the child.
	 * @param firstname
	 *            (String): first name of the child.
	 * @return Logs.
	 **/
	public static ArrayList<Entry> loadLogs(Context context, String name,
			String firstname, AppData data) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "log" + File.separator + name + "_"
				+ firstname + ".xml";

		File filePath = new File(path);
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
			}

			doc = db.parse(profilsXML);
			doc.getDocumentElement().normalize();
			return DOMToLogs(doc.getDocumentElement(), data);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Write and save the logs.
	 * 
	 * @param context
	 *            (Context): Context.
	 * @param name
	 *            (String): last name of the child.
	 * @param firstname
	 *            (String): first name of the child.
	 * @param logs
	 *            (List<Entry>): Logs.
	 **/
	public static void printLogs(Context context, List<Entry> logs,
			String name, String firstname) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator
				+ context.getApplicationContext().getPackageName()
				+ File.separator + "log" + File.separator + name + "_"
				+ firstname + ".xml";

		File newxmlfile = new File(path);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
		}

		FileOutputStream fileOs = null;
		try {
			fileOs = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileOs, "UTF-8");
			serializer.startDocument("UTF-8", Boolean.valueOf(true));
			serializer.startTag(null, "logs");
			for (int i = 0; i < logs.size(); i++) {
				Entry entry = logs.get(i);
				try {
					serializer.startTag(null, "entree");
					serializer.attribute(null, "date", entry.getDate());

					serializer.startTag(null, "sequence");
					for (int j = 0; j < entry.getSequence().size(); j++) {
						serializer.startTag(null, "mot");
						serializer.attribute(null, "nom", entry.getWordAt(j)
								.getWord());
						serializer.endTag(null, "mot");
					}
					serializer.endTag(null, "sequence");

					serializer.endTag(null, "entree");
					serializer.flush();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			serializer.endTag(null, "logs");
			serializer.endDocument();
			serializer.flush();
			fileOs.close();
		} catch (Exception e) {
		}
	}
}
