package com.pictitab.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pictitab.utils.UITools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SelectPictureActivity extends Activity {
	
	public static final String DATA_Picture = "Picture_path";
	
	private LinearLayout layout;					// Layout principal
	private ScrollView listPictureLayout;			// La ScrollView du layout
	private GridLayout gridPictureLayout;			// La grille des images
	private ArrayList<GridLayout> pictureButtons;	// La liste des boutons-image
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.toDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.toDisplay();
	}
	
	@Override
	public void onBackPressed() {
		// On recupere toutes les donnees relatives aux images
		// On ajoute un parametre a l'intention.
		getIntent().putExtra(SelectPictureActivity.DATA_Picture, "");
		setResult(RESULT_CANCELED, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenetre de selection d'une entree du lexique. **/
	private void toDisplay() {
		// Affichage de la liste des images existantes
		// Initialisation de l'affichage de la liste des images.
		listPictureLayout =new ScrollView(this);
		gridPictureLayout =new GridLayout(this);
		
		// Ici on sait que les images font 80x80p, du coup on fait taille_ecran/80
		// pour avoir le nombre de colones disponibles!
		int nbCol =UITools.getNbColumn(getWindowManager().getDefaultDisplay(), getResources().getConfiguration().orientation, 90, 5);
		gridPictureLayout.setColumnCount(nbCol);
		pictureButtons =new ArrayList<GridLayout>();
		
		// On recupere les images ainsi que leurs noms.
		String folderPath =Environment.getExternalStorageDirectory().getPath() + File.separator + "Pictures"; // <-- CHANGER ICI LE PATH DU REPERTOIRE D'IMAGE SI BESOIN
		List<Bitmap> pictures =this.getPictures(folderPath);
		List<String> picturesPath =this.getPicturesName(folderPath);
		
		for(int i=0;i<picturesPath.size();i++) {
			
			ImageButton tmpButton =new ImageButton(this);
			GridLayout tmpLayout =new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);
			TextView tmpName =new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			
			// On recupere l'image.
			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(pictures.get(i), 80, 80, false));
			// On donne la bonne valeur au TextView
			tmpName.setText(picturesPath.get(i));
			// On ajoute le bouton-image a la GridView.
			tmpLayout.addView(tmpButton);
			// On ajoute ce dernier a la GridLayout
			tmpLayout.addView(tmpName);
			// Ajout de la vue contenant le bouton et le texte dans un tableau.
			pictureButtons.add(tmpLayout);
			
			// Ajout du bouton dans le layout
			gridPictureLayout.addView(tmpLayout);
			
			final String selectedPicture =new String(folderPath+File.separator+picturesPath.get(i));
			
			// Action du bouton-image
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// On met a jour les donnees
					// On ajoute un parametre a l'intention.
					getIntent().putExtra(SelectPictureActivity.DATA_Picture, selectedPicture);
					setResult(RESULT_OK, getIntent());
					finish();
				}
			});
		}
		
		// Ajout des vues dans le layout
		layout = new LinearLayout(this);
		listPictureLayout.addView(gridPictureLayout);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(listPictureLayout);
		
		// Ajout du layout dans la vue
		setContentView(layout);
	}
	
	/**
	 * Methode permettant d'extraire les noms des images d'un dossier.
	 * /!\ Attention, cette methode ne verifie pas que le dossier ait ete cree prealablement.
	 * @param folderPath(String): Chemin du dossier contenant les images.
	 * @return Un tableau contenant les noms des images du dossier (ce tableau peut-etre vide).
	 */
	private List<String> getPicturesName(String folderPath) {
		List<String> paths =new ArrayList<String>();
		File dir = new File(folderPath);//File dir = new File(myFile, "Pictures");
		if (dir.listFiles() == null){
			return paths;
		} 
		else{
			File childfile[] = dir.listFiles();
			for(int i=0;i<childfile.length;i++) {
				if( (childfile[i].getPath().endsWith(".png")) || (childfile[i].getPath().endsWith(".jpg")) )
					paths.add(childfile[i].getName());
			}
		}
		return paths;
	}

	/**
	 * Methode permettant d'extraire les images d'un dossier.
	 * /!\ Attention, cette methode ne verifie pas que le dossier ait ete cree prealablement.
	 * @param folderPath(String): Chemin du dossier contenant les images.
	 * @return Un tableau contenant les images du dossier (ce tableau peut-etre vide).
	 */
	private List<Bitmap> getPictures(String folderPath) {
		List<Bitmap> pics =new ArrayList<Bitmap>();
		File dir = new File(folderPath);
		
		if (dir.listFiles() == null){
			return pics;
		} 
		else{
			File childfile[] = dir.listFiles();
			for(int i=0;i<childfile.length;i++) {
				if( (childfile[i].getPath().endsWith(".png")) || (childfile[i].getPath().endsWith(".jpg")) ) {
					Bitmap bit =BitmapFactory.decodeFile(childfile[i].getPath());
					pics.add(bit);
				}
			}
		}
		return pics;
	}
}
