package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Child;
import com.pictitab.utils.UITools;

public class SelectChildActivity extends Activity {
	
	// Les donnees de l'application
	private AppData data;
	
	private LinearLayout layout;					// Le layout de la fenetre
	private Button addChild;						// Bouton d'ajout du profil en mode educateur exclusivement
	private GridLayout gridChildrenLayout;			// La grille contenant la liste des profils
	private ArrayList<GridLayout> childrenButtons;	// La liste des profils en tant que boutons
	private ScrollView listChildren;				// ScrollVew du layout
	
	private int pictureWidth  = 80;		// Largeur du bouton
	private int pictureHeight = 130;	// Hauteur du bouton
	private int padding       = 5;		// Marges entre les boutons

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
	
	/** Mise en place de la fenetre de selection d'un profil enfant. **/
	private void toDisplay() {
		// On recupere l'intent parent
		Intent intent = this.getIntent();
		// Si l'intention recuperee n'est pas nulle, on remplit la zone de selection suivant le mode d'utilisation
		if (intent != null) {
			String mode = intent.getStringExtra(MainActivity.TYPE_SOURCE);
			
			// On cree la liste des enfants
			createChildActivity(mode);
		}
	}
	
	/**
	 * Adaptation a la fenetre du traitement des profils enfant en fonction du mode d'utilisation.
	 * @param mode(String): Mode d'utilisation enfant ou educateur
	 **/
	private void createChildActivity(String mode) {
		// Si mode utilisateur, cree bouton d'ajout de profil enfant
		if(mode.equals("educator_admin")) {
			createAddButton();
		}

		// Initialisation de l'affichage de la liste des enfants.
		listChildren =new ScrollView(this);
		gridChildrenLayout = new GridLayout(this);
		
		// On definit le nombre de colonnes du GridLayout
		Display display = getWindowManager().getDefaultDisplay();
		int orientation = getResources().getConfiguration().orientation;
		int nbCols = UITools.getNbColumn(display, orientation, pictureWidth, padding);
		Point size = new Point();
		display.getSize(size);
		
		// On indique le nombre de colonnes a la grille des entrees
		gridChildrenLayout.setColumnCount(nbCols);
		
		// Construction de la liste des profils enfant
		createChildrenGridToDisplay(mode);
		
		// Ajout des vues dans le layout
		listChildren.addView(gridChildrenLayout);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		if(mode.equals("educator_admin")) {
			layout.addView(addChild);
		}
		layout.addView(listChildren);
		
		// Ajout du layout dans la vue
		setContentView(layout);
	}
	
	/** Creation dans la fenetre d'un bouton de creation de profil enfant **/
	private void createAddButton() {
		// Creation dynamique d'un bouton "ajout d'un profil"
		addChild = new Button(this);
		addChild.setHeight(100);
		addChild.setText("Ajouter un enfant");
		
		// Action du bouton ajouter profil enfant
		addChild.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectChildActivity.this, ChildAdministrationActivity.class);
				
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				
				// On lance l'intent i, une nouvelle activite, en attendant un resultat
				startActivityForResult(i, 2);
			}
		});
	}
	
	/**
	 * Adaptation a la fenetre de la creation de la grille des profils enfants
	 * @param mode(String): Mode d'utilisation enfant ou educateur
	 **/
	private void createChildrenGridToDisplay(final String mode) {
		// On construit la liste des profils
		final List<Child> profils =data.getProfils();
		childrenButtons =new ArrayList<GridLayout>();
		for(int i=0;i<profils.size();i++) {
			// On recupere les infomations relatives au profil de l'enfant.
			final String nom = profils.get(i).getName();
			final String prenom = profils.get(i).getFirstname();
			final String photo = profils.get(i).getPhoto();
			final int an = profils.get(i).getBirthyear();
			final int mois = profils.get(i).getBirthmonth();
			final int jour = profils.get(i).getBirthday();
			final int num =i;
			
			// On recupere l'image du profil.
			ImageButton tmpButton = loadPicture(photo);
			
			// On associe une donnee textuelle au bouton de l'entree
			TextView tmpName =new TextView(this);
			tmpName.setText(prenom + " " + nom);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER;
			tmpName.setLayoutParams(params);
			
			// Grille d'un seul element correspondant a l'entree
			GridLayout tmpLayout = createChild(tmpButton, tmpName);
			
			// Ajout du bouton dans le layout
			childrenButtons.add(tmpLayout);
			
			// Ajout du bouton dans le layout
			gridChildrenLayout.addView(tmpLayout);
			
			// Action du bouton-image correspondant au profil enfant
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = null;
					if(mode.equals("educator_admin")) {
						intent = new Intent(SelectChildActivity.this, ChildAdministrationActivity.class);
					}
					else if(mode.equals("main_activity")) {
						intent = new Intent(SelectChildActivity.this, ChildCommunicationActivity.class);
					}
					
					// On envoie les donnees
					Bundle b = new Bundle();
					b.putParcelable(MainActivity.DATA_KEY, data);
					
					// On ajoute un parametre a l'intention.
					
					if(mode.equals("main_activity")) {
						// On envois seulement l'index de l'enfant dans le tableau "profils" des données de l'appli.
						b.putInt(ChildCommunicationActivity.SELECTED_CHILD, num);
					}
					
					intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
					intent.putExtra("nom", nom);
					intent.putExtra("prenom", prenom);
					intent.putExtra("photo", photo);
					
					if(mode.equals("educator_admin")) {
						intent.putExtra("an", an);
						intent.putExtra("mois", mois);
						intent.putExtra("jour", jour);
						// On lance l'intent i, une nouvelle activite, en attendant un resultat
						startActivityForResult(intent, 2);
					}
					else {
						startActivity(intent);
					}
					
				}
			});
		}
	}
	
	/**
	 * Methode qui charge la photo d'un profil et la redimensionne.
	 * @param pictureName(String): Nom de l'image
	 **/
	private ImageButton loadPicture(String pictureName) {
		// On recupere le chemin de l'image
		ImageButton tmpButton = new ImageButton(this);
		String fp   = File.separator;
		String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "photo" + fp + pictureName;
		
		// On charge l'image
		String picturePath = Environment.getExternalStorageDirectory() + path;
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			// On la redimensionne
			bitmap = Bitmap.createScaledBitmap(bitmap, pictureWidth, pictureHeight, true);
			tmpButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return tmpButton;
	}
	
	/**
	 * Methode qui installe le bouton-image du profil dans la grille.
	 * @param button(ImageButton): Bouton associe au profil
	 * @param name(TextView): Zone de texte associee au profil, prenom suivi de nom
	 **/
	private GridLayout createChild(ImageButton button, TextView name) {
		// Grille d'un seul element correspondant a l'entree
		GridLayout childLayout =new GridLayout(this);
		childLayout.setColumnCount(1);
		childLayout.setPadding(padding, padding, padding, padding);
		
		// On ajoute le bouton-image et la TextView a la GridLayout.
		childLayout.addView(button);
		childLayout.addView(name);
		return childLayout;
	}
}
