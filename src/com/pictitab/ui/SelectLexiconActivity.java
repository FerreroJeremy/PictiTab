package com.pictitab.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.UITools;

public class SelectLexiconActivity extends Activity {

	public static final String DATA_LEXICON = "num_lexicon";

	// Les donnees de l'application
	private AppData data;
	
	// Informations generales de l'entree du lexique
	private LinearLayout layout;					// Le layout contenant la liste des profils et le bouton de creation
	private Button addLexicon;						// Bouton d'ajout de l'entree
	private GridLayout gridLexiconLayout;			// La grille contenant la liste des profils
	private ArrayList<GridLayout> lexiconButtons;	// La liste des profils en tant que boutons
	private ScrollView listLexicon;					// ScrollView du layout
	
	private int buttonWidth  = 90;					// Largeur du bouton
	private int padding      = 5;					// Marges entre les boutons
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees relatives aux profils enfant
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
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
		
		// On ajoute un parametre a l'intention
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		
		// Si le retour de l'intent (donc de l'activite fille) est OK on termine l'activite courante
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenetre de selection d'une entree du lexique. **/
	private void toDisplay() {
		setContentView(R.layout.activity_select_lexicon);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// On cree le bouton d'ajout d'entree
		createAddButton();
		
		// Affichage de la liste des entrees du lexique deja existants
		
		// Initialisation de l'affichage de la liste des entrees.
		listLexicon =new ScrollView(this);
		gridLexiconLayout =new GridLayout(this);
		
		// On definit le nombre de colonnes du GridLayout
		Display display = getWindowManager().getDefaultDisplay();
		int orientation = getResources().getConfiguration().orientation;
		int nbCols = UITools.getNbColumn(display, orientation, buttonWidth, padding);
		
		// On indique le nombre de colonnes a la grille des entrees
		gridLexiconLayout.setColumnCount(nbCols);
		
		// Construction de la liste des entrees
		createProfilsGridToDisplay();
		
		// Ajout des vues dans le layout
		listLexicon.addView(gridLexiconLayout);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(addLexicon);
		layout.addView(listLexicon);
		
		// Ajout du layout dans la vue
		setContentView(layout);
	}
	
	/** Adaptation de la fenetre a l'action de creation de la liste des entrees du lexique **/
	private void createProfilsGridToDisplay() {
		// On construit la liste des entrees du lexique
		List<Lexicon> lexicons =data.getLexicon();
		lexiconButtons =new ArrayList<GridLayout>();
		for(int i=0;i<lexicons.size();i++) {
			// On recupere le mot et le nom de l'image associee a l'entree.
			final String mot = lexicons.get(i).getWord();
			final String src = lexicons.get(i).getPictureSource();
			
			// On recupere l'image de l'entree et on remplit le bouton de l'entree avec.
			ImageButton tmpButton = loadPicture(src);
			
			// On associe une donnee textuelle au bouton de l'entree
			TextView tmpName =new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(mot);
			
			// Grille d'un seul element correspondant a l'entree
			GridLayout tmpLayout = createLexicon(tmpButton, tmpName);
			
			// Ajout de la vue contenant le bouton et le texte dans un tableau.
			lexiconButtons.add(tmpLayout);
			
			// Ajout du bouton dans le layout
			gridLexiconLayout.addView(tmpLayout);
			
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SelectLexiconActivity.this, LexiconAdministrationActivity.class);
					
					// On envoie les donnees
					Bundle b = new Bundle();
					b.putParcelable(MainActivity.DATA_KEY, data);
					
					// On ajoute un parametre a l'intention.
					intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
					intent.putExtra(SelectLexiconActivity.DATA_LEXICON, mot);
					
					// Lance l'intent avec un parametre de retour a 6
					startActivityForResult(intent, 6);
				}
			});
		}
	}
	
	/** Creaction dans la fenetre d'un bouton de creation d'entree **/
	private void createAddButton() {
		// Creation dynamique d'un bouton "ajout d'une entree"
		addLexicon = new Button(this);
		addLexicon.setHeight(100);
		addLexicon.setText("Ajouter une entree");
		
		// Action du bouton ajouter entree
		addLexicon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectLexiconActivity.this, LexiconAdministrationActivity.class);
				
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				
				// On lance l'intent i, une nouvelle activite, en attendant un resultat
				startActivityForResult(i, 5);
			}
		});
	}
	
	/**
	 * Methode qui charge la photo d'une entree du lexique.
	 * @param pictureName(String): Nom de l'entree
	 **/
	private ImageButton loadPicture(String pictureName) {
		ImageButton tmpButton =new ImageButton(this);
		try {
			InputStream inputStream = new FileInputStream(pictureName);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
			tmpButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return tmpButton;
	}
	
	/**
	 * Methode qui installe le bouton-image de l'entree dans la grille.
	 * @param button(ImageButton): Bouton associe a la categorie
	 **/
	private GridLayout createLexicon(ImageButton button, TextView name) {
		// Grille d'un seul element correspondant a l'entree
		GridLayout lexiconLayout =new GridLayout(this);
		lexiconLayout.setColumnCount(1);
		lexiconLayout.setPadding(padding, padding, padding, padding);
		
		// On ajoute le bouton a la GridLayout.
		lexiconLayout.addView(button);
		lexiconLayout.addView(name);
		return lexiconLayout;
	}
}
