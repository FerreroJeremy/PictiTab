package com.pictitab.data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
	
	private String name;					// Last name
	private String firstname;				// First name
	private int birthday;
	private int birthmonth;
	private int birthyear;
	private ArrayList<Grammar> grammars;	// Avalaible grammar
	private String photo;					// Picture's name
	
	/*====================================================================================================================*/
	/*==												   CONSTRUCTORS												==*/
	/*====================================================================================================================*/
	
	/** 
     * Default constructor. 
     **/
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
	 * Copy constructor.
	 * @param name(String): Last name.
	 * @param firstname(String): Fisrt name.
	 * @param day(int): Birth day.
	 * @param month(int): Month day.
	 * @param year(int): Year day.
	 * @param photo(String): Picture's name.
	 * @param grammars(ArrayList<Grammar>): Avalaible grammars.
	 **/
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
	 * Parcel constructor.
	 * @param in(Parcel): parcel
     **/
	public Child(Parcel in) {
		this();
		readFromParcel(in);
	}
	
    /**
     * Necessary creator.
     **/
	public static final Creator<Child> CREATOR = new Creator<Child>() {

		public Child createFromParcel(Parcel source) {
			return new Child(source);
		}
		
		public Child[] newArray(int size) {
			return new Child[size];
		}
	};
	
	/**
	 * Write child profile's data into parcel.
	 * @param dest(Parcel): Parcel
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
	 * Read child profile's data from parcel.
	 * @param in(Parcel): Parcel
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
	 * Necessary stuff.
	 **/
	@Override
	public int describeContents() {
		return 0;
	}
	
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/

	/**
	 * Return the last name of the child.
	 * @return Last name.
	 **/
	public String getName() {
		return name;
	}

	/**
	 * Set a last name of the child.
	 * @param name(String): Last name.
	 **/
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the first name of the child.
	 * @return First name.
	 **/
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Set a first name of the child.
     * @param firstname(String): First name.
	 **/
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Return the day of birth.
	 * @return Day.
	 **/
	public int getBirthday() {
		return birthday;
	}

	/**
	 * Set a day of birth.
	 * @param birthday(int): Day.
	 **/
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	/**
	 * Return the month of birth.
	 * @return Month.
	 **/
	public int getBirthmonth() {
		return birthmonth;
	}

	/**
	 * Set a month of birth.
	 * @param birthmonth(int): Month.
	 **/
	public void setBirthmonth(int birthmonth) {
		this.birthmonth = birthmonth;
	}

	/**
	 * Return the year of birth.
	 * @return Year.
	 **/
	public int getBirthyear() {
		return birthyear;
	}

	/**
	 * Set a year of birth.
	 * @param birthyear(int): Year.
	 **/
	public void setBirthyear(int birthyear) {
		this.birthyear = birthyear;
	}
	
	/**
	 * Return the picture's name.
	 * @return Picture's name.
	 **/
	public String getPhoto() {
		return photo;
	}

	/**
	 * Set a picture's name.
	 * @param nom(String): Picture's name.
	 **/
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	/**
	 * Return the grammar list.
	 * @return Grammars.
	 **/
	public ArrayList<Grammar> getGrammars() {
		return grammars;
	}
	
	/**
	 * Set a grammar list.
	 * @param grammars(ArrayList<Grammar>): Grammars.
	 **/
	public void setGrammars(ArrayList<Grammar> grammars) {
		this.grammars = grammars;
	}
}
