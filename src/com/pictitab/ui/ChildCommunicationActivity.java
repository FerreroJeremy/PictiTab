package com.pictitab.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pictitab.data.AppData;
import com.pictitab.data.Automate;
import com.pictitab.data.Child;
import com.pictitab.data.Entry;
import com.pictitab.data.Grammar;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.UITools;
import com.pictitab.utils.XMLTools;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ChildCommunicationActivity extends Activity {
	
	public static final String SELECTED_CHILD ="selected_child";
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  -  H:mm:ss", Locale.FRANCE);
	
	// Les donnees de l'application
	private AppData data;
	
	// Principales zones de la fenetre
	private LinearLayout mainLayout;					// Le layout princpal de la fenetre
	private LinearLayout topLayout;						// Le layout contenant les boutons d'arret et d'effacement
	private LinearLayout sentenceLayout;				// Le layout des mots selectionnes dans la phrase
	private ScrollView botLayout;						// ScrollView des mots disponibles
	
	// Les deux boutons
	private ImageButton stopSentenceButton;				// Bouton d'arret de la communication
	private ImageButton clearSentenceButton;			// Bouton d'effacement de la sequence
	
	// La phrase en cours
	private HorizontalScrollView sentenceScrollView;	// ScrollView horizontale des mots selectionnes
	private List<GridLayout> wordsSentenceButtons;		// La grille des mots selectionnes
	
	// Le clavier propose a l'enfant
	private GridLayout listWordLayout;					// Les mots disponibles
	private ArrayList<GridLayout> wordsToSelectButtons;	// La grille des mots disponibles
	
	private Child profil;								// Le profil de l'enfant
	private ArrayList<Automate> automates;				// Les automates parcourant les regles des grammaires administrees a l'enfant
	private ArrayList<Lexicon> wordsToSelect;			// Les mots que l'enfant peut selectionner
	private ArrayList<Lexicon> wordsFromSentence;		// Les mots rentres par l'enfant dans la sequence
	private List<Entry> logs;							// Les entrees de l'enfant
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere les donnees a afficher
		this.data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		int num = (int)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getInt(SELECTED_CHILD, -1);
		if(num == -1) {
			// Affiche un message d'erreur dans le cas ou l'enfant n'est pas indique
			AlertDialog.Builder ab = new AlertDialog.Builder(ChildCommunicationActivity.this);
			ab.setTitle("Avertissement").setMessage("Aucun enfant selectionne.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
			finish();
		}
		// On recupere le profil de l'enfant selectionne ainsi que son historique
		this.profil =data.getProfils().get(num);
		logs = XMLTools.loadLogs(this, this.profil.getName(), this.profil.getFirstname(), data);

		this.initialize();
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
		super.onBackPressed();
		
		// On definit une date au format "aaaa-mm-jj"
		Date actuelle = new Date();
		String date = dateFormat.format(actuelle);
		
		// On ajoute l'entree au log
		ArrayList <Lexicon> copy = new ArrayList<Lexicon>(wordsFromSentence);
		logs.add(new Entry(date, copy));
		
		// On met a jour le log XML que lorsqu'on ferme la fenetre
		XMLTools.printLogs(getApplicationContext(), logs, this.profil.getName(), this.profil.getFirstname());
		// On ferme cette activite
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	private void initialize() {
		// Initialisation des variables.
		this.wordsFromSentence = new ArrayList<Lexicon>();
		this.wordsToSelect = new ArrayList<Lexicon>();
		this.automates =new ArrayList<Automate>();
		
		Grammar tmpGram;
		for(int i=0;i<this.profil.getGrammars().size();i++) {
			tmpGram =this.profil.getGrammars().get(i);
			if(tmpGram != null) {
				this.automates.add(new Automate(this.data.getGrammarByName(tmpGram.getName()), this.data.getLexicon(), this.data));
			}
		}
		// On rempli le tableau des mots pouvant etre ajouter a une phrase.
		this.setListWords();
	}
	
	/** Methode creant le contenu de la fenetre de communication. **/
	private void toDisplay() {
		// Ajout du mainLayout dans la vue.
		setContentView(R.layout.activity_child_communication);
		
		// Initialisation des elements graphiques.
		this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		this.topLayout          = new LinearLayout(this);
		this.sentenceScrollView = new HorizontalScrollView(this);
		this.sentenceLayout     = new LinearLayout(this);
		this.stopSentenceButton  = (ImageButton) findViewById(R.id.imageButtonReturn);
		this.clearSentenceButton = (ImageButton) findViewById(R.id.imageButtonClear);
		this.botLayout      = new ScrollView(this);
		int nbCol = UITools.getNbColumn(getWindowManager().getDefaultDisplay(), getResources().getConfiguration().orientation, 90, 5);
		this.listWordLayout = new GridLayout(this);
		this.listWordLayout.setColumnCount(nbCol);
		this.wordsSentenceButtons = new ArrayList<GridLayout>();
		this.wordsToSelectButtons = new ArrayList<GridLayout>();
		/*--------------------------------------*/
		this.mainLayout.addView(this.topLayout);
		this.mainLayout.addView(this.botLayout);
		this.topLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.topLayout.addView(this.sentenceScrollView);
		this.sentenceScrollView.addView(this.sentenceLayout);

		// Implementation de l'action du bouton "STOP"
		this.stopSentenceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		// Implementation de l'action du bouton "STOP"
		this.clearSentenceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On definit une date
				Date actuelle = new Date();
				// Donne la date au format "aaaa-mm-jj"
				String date = dateFormat.format(actuelle);
				
				// On ajoute l'entree au log
				ArrayList <Lexicon> copy = new ArrayList<Lexicon>(wordsFromSentence);
				logs.add(new Entry(date, copy));
				
				// On vide le bandeau superieur representant la phrase constituee
				deleteWordFromSentenceAction(0);
			}
		});
		
		this.sentenceLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.sentenceLayout.setBackgroundColor(Color.LTGRAY);

		this.sentenceLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,110));
		this.botLayout.addView(this.listWordLayout);
		
		this.drawWordsSentence();
		this.drawWordsToSelect();
	}
	
	/** Methode permettant de dessiner dans la vue les boutons-images represensant les mots selectionnes. **/
	private void drawWordsSentence() {
		for(int i=0;i<this.wordsFromSentence.size();i++) {
			// Pour chaque mot selectionne, on cree un bouton
			ImageButton tmpButton =new ImageButton(this);
			GridLayout tmpLayout =new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);
			
			// On recupere l'image.
			Bitmap bit =BitmapFactory.decodeFile(this.wordsFromSentence.get(i).getPictureSource());
			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(bit, 80, 80, false));
			
			// On donne la bonne valeur au TextView
			TextView tmpName =new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(this.wordsFromSentence.get(i).getWord());
			
			// On ajoute le bouton-image a la GridView.
			tmpLayout.addView(tmpButton);
			tmpLayout.addView(tmpName);
			
			// Ajout de la vue contenant le bouton et le texte dans un tableau.
			this.wordsSentenceButtons.add(tmpLayout);
			
			// Ajout du bouton dans le layout
			this.sentenceLayout.addView(tmpLayout);
			
			final int wordIndex =i;
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteWordFromSentenceAction(wordIndex);
				}
			});
		}
	}
	
	/** Methode permettant de dessiner dans la vue les boutons-images represensant les mots disponibles. **/
	private void drawWordsToSelect() {
		for(int i=0;i<this.wordsToSelect.size();i++) {
			// Pour chaque mot que l'on peut selectionner, on cree un bouton
			ImageButton tmpButton =new ImageButton(this);
			GridLayout tmpLayout =new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);
			
			// On recupere l'image.
			Bitmap bit =BitmapFactory.decodeFile(this.wordsToSelect.get(i).getPictureSource());
			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(bit, 80, 80, false));
			
			// On donne la bonne valeur au TextView
			TextView tmpName =new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(this.wordsToSelect.get(i).getWord());
			
			// On ajoute le bouton-image a la GridView.
			tmpLayout.addView(tmpButton);
			tmpLayout.addView(tmpName);
			
			// Ajout de la vue contenant le bouton et le texte dans un tableau.
			this.wordsToSelectButtons.add(tmpLayout);
			
			// Ajout du bouton dans le layout
			this.listWordLayout.addView(tmpLayout);
			
			final int wordIndex =i;
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectWordAction(wordIndex);
				}
			});
		}
	}

	/**
	 * Methode permettant de faire revenir en arriere l'automate de chaque grammaire de l'enfant.
	 * @param wordIndex(int): l'indice de l'image mot que l'on souhaite supprimer, ainsi que les suivantes.
	 */
	private void deleteWordFromSentenceAction(int wordIndex) {
		// On supprime les images-mots que l'on peut actuellement selectionner...
		this.sentenceLayout.removeAllViews();
    	this.listWordLayout.removeAllViews();
		this.wordsToSelect.clear();
		
		// On actualise les images-mots de la phrase a afficher...
		for(int i=this.wordsFromSentence.size()-1;i>=0;i--) {
			this.wordsFromSentence.remove(i);
			if(i==wordIndex)
				break;
		}
		// Pour chaque automate, on recule...
		for(int i=0;i<this.automates.size();i++) {
			this.automates.get(i).moveBackward(this.wordsFromSentence);
			this.getEligibleWord(this.automates.get(i));
		}
		this.drawWordsSentence();
		this.drawWordsToSelect();
	}
	

	/**
	 * Methode permettant d'ajouter un mot a la phrase et d'actualiser les mots que l'on peut selectionner.
	 * @param wordIndex(int): Index du mot a ajouter a la phrase courante.
	 */
	private void selectWordAction(int wordIndex) {
		Lexicon word =this.wordsToSelect.remove(wordIndex);
		String catName =word.getCategory().getName();
		
		// Efface les contenus...
		this.wordsToSelect.clear();
		this.sentenceLayout.removeAllViews();
    	this.listWordLayout.removeAllViews();
    	
		// Ajout du mot a la phrase.
		this.wordsFromSentence.add(word);
		
		// Mise a jour des automates:
		for(int i=0;i<this.automates.size();i++) {
			this.automates.get(i).moveForwardToNextCat(catName);
			this.getEligibleWord(this.automates.get(i));
		}
		this.drawWordsSentence();
		this.drawWordsToSelect();
	}
	
	/** Methode permettant d'afficher les premiers mots possibles en fonction des grammaires de l'enfant. **/
	private void setListWords() {
		// Pour chaque automate de grammaire de l'enfant:
		for(int i=0; i<this.automates.size(); i++) {
			this.getEligibleWord(this.automates.get(i));
		}
	}
	
	/**
	 * Methode permettant de selectionner les mots eligibles pour constituer la phrase.
	 * @param automate(Automate): L'automate d'une grammaire utilisable par l'enfant.
	 */
	private void getEligibleWord(Automate automate) {
		ArrayList<Lexicon> tmpWords;
		// On recupere la liste des mots eligibles pour cette grammaire sans creer de doublons
		tmpWords =automate.getWordsToDisplay();
		this.wordsToSelect.removeAll(tmpWords);
		this.wordsToSelect.addAll(tmpWords);
	}
}