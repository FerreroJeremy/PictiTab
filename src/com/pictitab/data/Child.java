package com.pictitab.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
	
	// Les informations relatives au profil enfant
	private String name;					// Nom
	private String firstname;				// Prenom
	private int birthday;					// Jour de naissance
	private int birthmonth;					// Mois de naissance
	private int birthyear;					// Annee de naissance
	private ArrayList<Grammar> grammars;	// Grammaires autorisees
	private String photo;					// Nom de la photo
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
	
	/** Constructeur par defaut de la classe Child. **/
	public Child() {
		this.name    = new String("");
		this.firstname = new String("");
		this.birthday  = 0;
		this.birthmonth  = 0;
		this.birthyear = 0;
		this.photo = new String("");
		this.grammars = new ArrayList<Grammar>();
	}
	
	/**
	 * Constructeur de la classe Profil.
	 * @param name(String): Nom de l'enfant.
	 * @param firstname(String): Prenom de l'enfant.
	 * @param day(int): Jour de naissance de l'enfant.
	 * @param month(int): Mois de naissance de l'enfant.
	 * @param year(int): Annee de naissance de l'enfant.
	 * @param photo(String): Nom de la photo.
	 * @param grammars(ArrayList<Grammar>): Liste des grammaires
	 */
	public Child(String name, String firstname, int day, int month, int year, String photo, ArrayList<Grammar> grammars) {
		this.name = name;
		this.firstname = firstname;
		this.birthday = day;
		this.birthmonth = month;
		this.birthyear = year;
		this.photo =photo;
		this.grammars = grammars;
	}
	
	/*====================================================================================================================*/
	/*==												     PARCELABLE													==*/
	/*====================================================================================================================*/
	
	/**
	 * Constructeur de la classe Child utilisant Parcel
	 * @param in(Parcel): Le paquet dans lequel sont contenues les donnees
	 **/
	public Child(Parcel in) {
		this();
		readFromParcel(in);
	}
	
	/** Les classes qui implementent l'interface Parcelable doivent aussi avoir un champ statique appele CREATOR. **/
	public static final Creator<Child> CREATOR = new Creator<Child>() {

		public Child createFromParcel(Parcel source) {
			return new Child(source);
		}
		
		public Child[] newArray(int size) {
			return new Child[size];
		}
	};
	
	/**
	 * Ecriture des differents types de donnees de la classe Child
	 * @param dest(Parcel): Le paquet dans lequel sont ecrites les donnees
	 * @param flags(): Des indicateurs supplementaires sur la façon dont l'objet doit etre ecrit.
	 * Peut etre 0 ou PARCELABLE_WRITE_RETURN_VALUE.
	 **/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(firstname);
		dest.writeInt(birthday);
		dest.writeInt(birthmonth);
		dest.writeInt(birthyear);
		dest.writeString(photo);
		dest.writeList(grammars);
	}
	
	/**
	 * Lecture des differents types de donnees de la classe Child 
	 * @param in(Parcel): Le paquet depuis lequel sont lues les donnees
	 **/
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.firstname = in.readString();
		this.birthday = in.readInt();
		this.birthmonth = in.readInt();
		this.birthyear = in.readInt();
		this.photo = in.readString();
		this.grammars = in.readArrayList(Grammar.class.getClassLoader());
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
	 * Renvoie le nom de l'enfant.
	 * @return Le nom de l'enfant.
	 **/
	public String getName() {
		return name;
	}

	/**
	 * Renseigne le nom de l'enfant.
	 * @param name(String): Le nom de l'enfant.
	 **/
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Renvoie le prenom de l'enfant.
	 * @return Le prenom de l'enfant.
	 **/
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Renseigne le prenom de l'enfant.
	 * @param firstname(String): Le prenom de l'enfant.
	 **/
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Renvoie le jour de la naissance de l'enfant.
	 * @return Le jour de la naissance de l'enfant.
	 **/
	public int getBirthday() {
		return birthday;
	}

	/**
	 * Renseigne le jour de la naissance de l'enfant.
	 * @param birthday(int): Le jour de naissance de l'enfant.
	 **/
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	/**
	 * Renvoie le mois de la naissance de l'enfant.
	 * @return Le mois de la naissance de l'enfant.
	 **/
	public int getBirthmonth() {
		return birthmonth;
	}

	/**
	 * Renseigne le mois de la naissance de l'enfant.
	 * @param birthmonth(int): Le mois de naissance de l'enfant.
	 **/
	public void setBirthmonth(int birthmonth) {
		this.birthmonth = birthmonth;
	}

	/**
	 * Renseigne l'annee de la naissance de l'enfant.
	 * @return L'annee de la naissance de l'enfant.
	 **/
	public int getBirthyear() {
		return birthyear;
	}

	/**
	 * Renseigne l'annee de la naissance de l'enfant.
	 * @param birthyear(int): L'annee de naissance de l'enfant.
	 **/
	public void setBirthyear(int birthyear) {
		this.birthyear = birthyear;
	}
	
	/**
	 * Renvoie le nom de la photo de l'enfant.
	 * @return Le nom de la photo de l'enfant.
	 **/
	public String getPhoto() {
		return photo;
	}

	/**
	 * Renseigne le nom de la photo de l'enfant.
	 * @param nom(String): Le nom la photo de l'enfant.
	 **/
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	/**
	 * Renvoie la liste des grammaires.
	 * @return La liste des grammaires.
	 **/
	public ArrayList<Grammar> getGrammars() {
		return grammars;
	}
	
	/**
	 * Renseigne la liste des grammaires.
	 * @param grammars(ArrayList<Grammar>): La liste des grammaires.
	 **/
	public void setGrammars(ArrayList<Grammar> grammars) {
		this.grammars = grammars;
	}
}
