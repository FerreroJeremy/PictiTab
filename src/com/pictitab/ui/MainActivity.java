package com.pictitab.ui;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pictitab.data.AppData;
import com.pictitab.utils.XMLTools;

public class MainActivity extends Activity {

	public static final String TYPE_SOURCE = "source";
	public static final String DATA_KEY = "DATA";
	public static final String DATAEXTRA_KEY = "DATAEXTRA";

	private AppData data;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */
	// Overrided functions to manage the AppData !
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get all the data
		data = new AppData();

		// Control of the external storage of the device and display the window
		this.controlDir();
		this.toDisplay();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.data = (AppData) data.getBundleExtra(MainActivity.DATAEXTRA_KEY)
				.getParcelable(MainActivity.DATA_KEY);
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == TRAITEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Control the external storage.
	 **/
	private void controlDir() {
		// If there isn't external storage or if it is in read only then the
		// write of data and save directory is impossible
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED_READ_ONLY)) {
			Toast.makeText(getApplicationContext(),
					"Probleme de droits ou memoire introuvable !",
					Toast.LENGTH_LONG).show();
		}
		// Creation of directory if they don't exist
		String pathApp = Environment.getExternalStorageDirectory()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator + getApplicationContext().getPackageName();
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
			XMLTools.createEmptyXML(pathApp + File.separator + "data"
					+ File.separator + "enfant.xml", "enfants");
		}
		File dossierDefault = new File(pathApp + File.separator + "data"
				+ File.separator + "default");
		if (!dossierDefault.exists()) {
			dossierDefault.mkdir();
			XMLTools.createEmptyXML(pathApp + File.separator + "data"
					+ File.separator + "default" + File.separator
					+ "categorie.xml", "categories");
			XMLTools.createEmptyXML(pathApp + File.separator + "data"
					+ File.separator + "default" + File.separator
					+ "lexique.xml", "lexiques");
			XMLTools.createEmptyXML(pathApp + File.separator + "data"
					+ File.separator + "default" + File.separator
					+ "grammaire.xml", "grammaires");
		}
	}

	/**
	 * Display the window.
	 **/
	private void toDisplay() {
		// Load the data.
		data.setCategories(XMLTools.loadCategory(this));
		data.setLexicon(XMLTools.loadLexicon(this, data.getCategories()));
		data.setGrammars(XMLTools.loadGrammar(this, data.getCategories()));
		data.setProfils(XMLTools.loadProfil(this, data));

		setContentView(R.layout.activity_main);

		final Button ChildButton = (Button) findViewById(R.id.selectChildActivity);
		final Button EducatorButton = (Button) findViewById(R.id.selectEducatorActivity);

		// Child mode
		ChildButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SelectChildActivity.class);
				intent.putExtra(TYPE_SOURCE, "main_activity");
				Bundle b = new Bundle();
				b.putParcelable(DATA_KEY, data);
				intent.putExtra(DATAEXTRA_KEY, b);
				startActivity(intent);
			}
		});

		// Educator mode
		EducatorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MainEducatorActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(DATA_KEY, data);
				intent.putExtra(DATAEXTRA_KEY, b);
				startActivityForResult(intent, 0);
			}
		});
	}
}
