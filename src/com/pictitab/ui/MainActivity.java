package com.pictitab.ui;

import java.io.File;

import com.pictitab.data.AppData;
import com.pictitab.ui.R;
import com.pictitab.utils.XMLTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String TYPE_SOURCE = "source";
	public static final String DATA_KEY = "DATA";
	public static final String DATAEXTRA_KEY = "DATAEXTRA";
	
	// Les donnees de l'application
	private AppData data;
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees de l'application
		data =new AppData();
		
		// On controle la memoire externe de la tablette et on affiche l'activite
		this.controlDir();
		this.toDisplay();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.data =(AppData)data.getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Controle de la memoire externe de l'appareil physique. **/
	private void controlDir() {
		// Si il n'y a pas de memoire externe ou qu'elle est seulement en
		// lecture seule, ecriture impossible
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
			&& Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
				Toast.makeText(getApplicationContext(), "Probleme de droits ou memoire introuvable !", Toast.LENGTH_LONG).show();
		}
		// Creation des repertoires si il n'existe pas
		String pathApp = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator + getApplicationContext().getPackageName();
		File dossierApp = new File(pathApp);
		if (!dossierApp.exists()) {
			dossierApp.mkdir();
		}
		File dossierPhoto = new File(pathApp + File.separator + "photo");
		if (!dossierPhoto.exists()) {
			dossierPhoto.mkdir();
		}
		File dossierImage = new File(pathApp + File.separator + "image");
		if (!dossierImage.exists()) {
			dossierImage.mkdir();
		}
		File dossierLog = new File(pathApp + File.separator + "log");
		if (!dossierLog.exists()) {
			dossierLog.mkdir();
		}
		File dossierData = new File(pathApp + File.separator + "data");
		if (!dossierData.exists()) {
			dossierData.mkdir();
			XMLTools.createEmptyXML(pathApp + File.separator + "data" + File.separator + "enfant.xml", "enfants");
		}
		File dossierDefault = new File(pathApp + File.separator + "data" + File.separator + "default");
		if (!dossierDefault.exists()) {
			dossierDefault.mkdir();
			XMLTools.createEmptyXML(pathApp + File.separator + "data" + File.separator + "default" + File.separator + "categorie.xml", "categories");
			XMLTools.createEmptyXML(pathApp + File.separator + "data" + File.separator + "default" + File.separator + "lexique.xml", "lexiques");
			XMLTools.createEmptyXML(pathApp + File.separator + "data" + File.separator + "default" + File.separator + "grammaire.xml", "grammaires");
		}
	}
	
	/** Mise en place de la fenetre d'acceuil de l'application. **/
	private void toDisplay() {
		// On charge les donnees.
		data.setCategories(XMLTools.loadCategory(this));
		data.setLexicon(XMLTools.loadLexicon(this, data.getCategories()));
		data.setGrammars(XMLTools.loadGrammar(this, data.getCategories()));
		data.setProfils(XMLTools.loadProfil(this, data));
		
		// On affiche la vue ayant le xml "activity_main"
		setContentView(R.layout.activity_main);
		
		// On prend connaissance des boutons
		final Button ChildButton = (Button) findViewById(R.id.selectChildActivity);
		final Button EducatorButton = (Button) findViewById(R.id.selectEducatorActivity);
		
		// Action du bouton "ChildButton"
		ChildButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Nouvelle intention : activity source -> classe de destination
				Intent intent = new Intent(MainActivity.this, SelectChildActivity.class);
				// On ajoute un parametre a l'intention
				intent.putExtra(TYPE_SOURCE, "main_activity");
				// On passe les donnees de l'application a l'activite suivante...
				Bundle b = new Bundle();
				b.putParcelable(DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				intent.putExtra(DATAEXTRA_KEY, b);
				// On demarre la nouvelle intention
				startActivity(intent);
			}
		});
		
		// Action du bouton "EducateurButton"
		EducatorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MainEducatorActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				intent.putExtra(DATAEXTRA_KEY, b);
				startActivityForResult(intent, 0);
			}
		});
	}
}
