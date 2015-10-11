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
	
	// Les donnees de l'application
	private static AppData data;
	
	// Informations generales de la categorie
	private TextView titre;				// Titre de la fenetre
	private EditText categoryName;		// Edition du nom de la categorie
	private Button valideButton;		// Bouton de creation de la categorie

	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees relatives aux categories
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
		// On affiche l'activite
		this.toDisplay();
	}

	@Override  
	public void onBackPressed() {
		setResult(RESULT_OK, this.getIntent());
		finish();
	}

	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenêtre d'administration d'une categorie. **/
	private void toDisplay() {
		setContentView(R.layout.activity_category_administration);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialisation des elements graphiques.
		categoryName = (EditText) findViewById(R.id.catName);
		titre = (TextView) findViewById(R.id.EditTextCategory);
		valideButton = (Button) findViewById(R.id.AjCat);
		
		// On prepare les donnees de la liste
		final String nom = getIntent().getStringExtra("nom");
		final int mode = getIntent().getIntExtra("mode", 0);
        
		// (mode = 0) : Creation d'une categorie en tant que fille de la categorie "nom"
		if(mode == 0) {
			createChildCategoryToDisplay(nom);
		}
		// (mode = 1) : Modification d'une categorie
		else {
			modifyCategoryToDisplay(nom);
		}
	}
    
    /**
	 * Adaptation de la fenetre a l'action de modification d'une categorie.
	 * @param nom(String): Le nom de la categorie dans l'objet AppData.
	 **/
	private void modifyCategoryToDisplay(final String nom) {
		// On affiche le nom de la categorie dans le champ prevu a cet effet
		categoryName.setText(nom);
		
		// On modifie le texte du bouton de validation de "Valider" en "Modifier"
		valideButton.setText("Renommer");
		titre.setText("Nouveau nom de la categorie");
		
		// Action du bouton de modification
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        // On modifie la categorie
				        case DialogInterface.BUTTON_POSITIVE:
				        	modifyCategory(nom);

				        // Aucune modification
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de modification
				AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment renommer cette categorie ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/** Adaptation de la fenetre a l'action de creation d'une categorie. **/
	protected void createNewCategoryToDisplay() {
		titre.setText("Nom de la nouvelle categorie");
		// Action du bouton de creation
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On ajoute la categorie
				addCategory();
			}
		});
	}
	
	/** Adaptation de la fenetre a l'action de creation d'une categorie en tant que fille d'une autre. **/
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
	
	/** Cree la categorie. **/
	protected void addSubCategory(String name){
		// On recupere le nom de la categorie
		String catName = categoryName.getText().toString();
		
		// Si le champ du nom de la categorie est vide, alors on affiche l'erreurs
		if (catName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// Si la categorie existe deja
		else if(categoryIsExist(catName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Ajout de la nouvelle categorie
		else {
			// On construit la liste des sous-categories
			ArrayList<Category> subCategories = new ArrayList<Category>();
			// On ajoute cette nouvelle categorie aux donnees
			Category child = new Category(catName, subCategories);
			data.addCategory(child);
			
			// On recupere la categorie correspondant au nom de la categorie mere
			Category mother = data.getCategories().get(data.getCategoryByName(name));
			// On ajoute la fille a la mere
			mother.addCategory(child);
	        // Puis on actualise le fichier XML des categories
	        // Enfin on retourne en arriere
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			onBackPressed();
		}
	}
	
	/** Cree la categorie. **/
	protected void addCategory(){
		// On recupere le nom de la categorie
		String catName = categoryName.getText().toString();
		
		// Si le champ du nom de la categorie est vide, alors on affiche l'erreurs
		if (catName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// Si la categorie existe deja
		else if(categoryIsExist(catName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Ajout de la nouvelle categorie
		else {
			// On construit la liste des sous-categories
			ArrayList<Category> subCategories = new ArrayList<Category>();
			// On ajoute cette nouvelle categorie aux donnees
			Category child = new Category(catName, subCategories);
			data.addCategory(child);
			
			// On recupere la categorie correspondant au nom de la categorie mere
			Category mother = data.getCategories().get(data.getCategoryByName("TOUT"));
			// On ajoute la fille a la mere
			mother.addCategory(child);
	        // Puis on actualise le fichier XML des categories
	        // Enfin on retourne en arriere
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			onBackPressed();
		}
	}
	
	/**
	* Modifie la categorie courante.
	* @param oldName(String): Le nom de la categorie a modifier.
	**/
	protected void modifyCategory(String oldName) {
		// On recupere le nouveau nom de la categorie
		String newName = categoryName.getText().toString();
		// Si le champ du nom de la categorie est vide, alors on affiche l'erreurs
		if (newName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir le champ correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// Si la categorie existe deja
		else if((!newName.equals(oldName)) && (categoryIsExist(newName))) {
			AlertDialog.Builder ab = new AlertDialog.Builder(CategoryAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette categorie existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Modification
		else {
			// On renomme la categorie dans l'ensemble des categories, dans les mots du lexique et dans les regles des grammaires
			data.renameCategory(oldName, newName);
	        // Puis on actualise les fichiers XML des categories, mots et grammaires
	        // Enfin on retourne en arriere
			XMLTools.printCategories(getApplicationContext(), data.getCategories());
			XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** 
	* Retourne si la categorie existe deja ou non.
	* @param category(String): La categorie entree
	* @return (boolean) : true si la categorie existe deja, false sinon
	**/
	public static boolean categoryIsExist(String category) {
		// On recupere la categorie correspondante
		int id = data.getCategoryByName(category);
		if(id != -1) {
	        Category c = data.getCategories().get(id);
	        // Si elle existe
	        if(c != null) {
	        	return true;
	        }
		}
        return false;
	}
}
