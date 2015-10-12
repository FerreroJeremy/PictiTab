package com.pictitab.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.utils.XMLTools;

public class CategoryAdministrationActivity extends Activity {
	
	private static AppData data;
	
	private TextView titre;				// Window's title
	private EditText categoryName;		// Edition of the category name
	private Button valideButton;		// Button of the creation of the category

	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load a categories data
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
		this.toDisplay();
	}

	@Override  
	public void onBackPressed() {
		setResult(RESULT_OK, this.getIntent());
		finish();
	}

	/*====================================================================================================================*/
	/*==                                                        PROCESS													==*/
	/*====================================================================================================================*/
	
	/** 
     * Display of the category administration window.
     **/
	private void toDisplay() {
		setContentView(R.layout.activity_category_administration);
		
		// Set orientation in portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		categoryName = (EditText) findViewById(R.id.catName);
		titre = (TextView) findViewById(R.id.EditTextCategory);
		valideButton = (Button) findViewById(R.id.AjCat);
		
		final String nom = getIntent().getStringExtra("nom");
		final int mode = getIntent().getIntExtra("mode", 0);
        
		// (mode = 0) : Creation of a category in a sub-category of the category "nom"
		if(mode == 0) {
			createChildCategoryToDisplay(nom);
		}
		// (mode = 1) : Modify of category
		else {
			modifyCategoryToDisplay(nom);
		}
	}
    
    /**
     * Display window for modification of a category.
	 * @param nom(String): The name of the category in the Data.
	 **/
	private void modifyCategoryToDisplay(final String nom) {
		categoryName.setText(nom);
		
		valideButton.setText("Renommer");
		titre.setText("Nouveau nom de la categorie");
		
		// Modification button
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				        	modifyCategory(nom);
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Alert dialog box to rename the category
				AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment renommer cette categorie ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/**
     * Display the window for the creation of a category.
     **/
	protected void createNewCategoryToDisplay() {
		titre.setText("Nom de la nouvelle categorie");
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addCategory();
			}
		});
	}
	
	/**
     * Display the window for the creation of a category in sub-category of the other.
     **/
	protected void createChildCategoryToDisplay(final String nom) {
		titre.setText("Nom de la nouvelle sous-categorie");
		// Action du bouton de creation
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On ajoute la categorie
				addSubCategory(nom);
			}
		});
	}
	
	/** 
     * Create the sub-category.
     **/
	protected void addSubCategory(String name){
		// Get the category name
		String catName = categoryName.getText().toString();
		
        // If the category name is empty else error
		if (catName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// If the category already exists
		else if(categoryIsExist(catName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Add a new category
		else {
			// Build a sub-category list
			ArrayList<Category> subCategories = new ArrayList<Category>();
			// Add new category to the data
			Category child = new Category(catName, subCategories);
			data.addCategory(child);
			// Get the category according to the name of the parent category
			Category mother = data.getCategories().get(data.getCategoryByName(name));
			// Add the child to the parent
			mother.addCategory(child);
	        // Update the xml file
	        // Go back
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			onBackPressed();
		}
	}
	
	/** 
     * Create the category.
     **/
	protected void addCategory(){
		// Get category name
		String catName = categoryName.getText().toString();
		
		// Condition on name
		if (catName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// If the category already exists
		else if(categoryIsExist(catName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Add a new category
		else {
			ArrayList<Category> subCategories = new ArrayList<Category>();
			Category child = new Category(catName, subCategories);
			data.addCategory(child);
			Category mother = data.getCategories().get(data.getCategoryByName("TOUT"));
			mother.addCategory(child);
	        // Update xml file and go back
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			onBackPressed();
		}
	}
	
	/**
	* Modify the current category.
	* @param oldName(String): The name of the category to modify.
	**/
	protected void modifyCategory(String oldName) {
		// Get new name
		String newName = categoryName.getText().toString();
		// Condition test
		if (newName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		else if((!newName.equals(oldName)) && (categoryIsExist(newName))) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		} else {
			// Rename
			data.renameCategory(oldName, newName);
	        // Update xml file
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** 
	* Return if the category already exists or not
	* @param category(String): Category name
	* @return (boolean) : true if it is the case else false
	**/
	public static boolean categoryIsExist(String category) {
		int id = data.getCategoryByName(category);
		if(id != -1) {
	        Category c = data.getCategories().get(id);
	        if(c != null) {
	        	return true;
	        }
		}
        return false;
	}
}
