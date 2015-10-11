package com.pictitab.ui;

import com.pictitab.ui.R;
import com.pictitab.data.AppData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainEducatorActivity extends Activity {
	
	// Les donnees de l'application
	private AppData data;
	
	// Les boutons pour se rendre aux differentes fenetres d'adminisatrion
	private Button categorydButton;		// Categories
	private Button lexiconButton;		// Lexique
	private Button grammarButton;		// Grammaires
	private Button childButton;			// Profils enfant
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees a afficher
		this.data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
		// On affiche l'activite
		this.toDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.toDisplay();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.data =(AppData)data.getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
	}
	
	@Override  
	public void onBackPressed() {
		// On actualise nos donnees
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		// On ajoute un parametre a l'intention.
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenetre de selection du mode d'administration du mode educateur. **/
	private void toDisplay() {
		setContentView(R.layout.activity_main_educator);
		
		// Initialisation des elements graphiques.
		categorydButton = (Button) findViewById(R.id.selectCategoryAdministration);
		lexiconButton 	= (Button) findViewById(R.id.selectLexiconAdministration);
		grammarButton   = (Button) findViewById(R.id.selectGrammarAdministration);
		childButton     = (Button) findViewById(R.id.selectChildAdministration);
		
		// Action du bouton "categorydButton"
		categorydButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this, SelectCategoryActivity.class);
				
				/*---------On envois nos donnees!---------*/
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				/*----------------------------------------*/
				startActivityForResult(i, 1);
			}
		});
		
		// Action du bouton "lexiconButton"
		lexiconButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this, SelectLexiconActivity.class);
				
				/*---------On envois nos donnees!---------*/
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				/*----------------------------------------*/
				startActivityForResult(i, 4);
			}
		});
		
		// Action du bouton "grammarButton"
		grammarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this, SelectGrammarActivity.class);
				
				/*---------On envois nos donnees!---------*/
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				/*----------------------------------------*/
				startActivityForResult(i, 1);
			}
		});
		
		// Action du bouton "childButton"
		childButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this, SelectChildActivity.class);
				
				/*---------On envois nos donnees!---------*/
				i.putExtra(MainActivity.TYPE_SOURCE, "educator_admin");
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				/*----------------------------------------*/
				startActivityForResult(i, 1);
			}
		});
	}
}
