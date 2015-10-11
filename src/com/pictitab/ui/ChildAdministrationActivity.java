package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pictitab.data.AppData;
import com.pictitab.data.Child;
import com.pictitab.data.Grammar;
import com.pictitab.ui.R;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ChildAdministrationActivity extends Activity {
	
	// Les donnees de l'application
	private static AppData data;
	
	// Informations generales de l'enfant
	private EditText family_name;	// Nom
	private EditText first_name;	// Prenom
	private DatePicker birth_date;	// Date de naissance
	private Bitmap bitMapChild;		// Photographie
	private ImageButton imgButton;	// Bouton contenant la photographie de l'enfant
	private ImageButton logButton;	// Bouton de redirection vers la fenetre des logs de l'enfant
	
	// Suppression et validation d'un profil enfant
	private Button valideButton;	// Bouton de suppression de l'entree
	private Button deleteButton;	// Bouton d'ajout de l'entree
	
	// Liste deroulante de toutes les grammaires disponibles
	ExpandableTreeAdapter listAdapterAllGram;				// Adaptateur de la liste deroulante
	ExpandableListView lvExpAllGram;						// Header de la liste deroulante
	List<String> listDataHeaderAllGram;						// Liste de toutes les grammaires disponibles
	HashMap<String, List<String>> listDataChildAllGram;		// Map de la liste des grammaires disponibles
	
	// Liste deroulante des grammaires selectionnees
	ExpandableTreeAdapter listAdapterChildGram;				// Adaptateur de la liste deroulante
    ExpandableListView lvExpChildGram;						// Header de la liste deroulante
    List<String> listDataHeaderChildGram;					// Liste de toutes les grammaires selectionnees
    HashMap<String, List<String>> listDataChildChildGram;	// Map de la liste des grammaires selectionnees
    
    List<String> topChildGram;		// Les grammaires selectionnees
	
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Si le code de retour est bon, alors on change l'image.
		if(resultCode==RESULT_OK) {
			// On recupere l'image de l'appareil photo.
			this.bitMapChild = (Bitmap) data.getExtras().get("data");
			
			// On verifie que le retour a l'activite vienne de l'appareil photo.
			if(this.bitMapChild!=null) {
				imgButton.setImageBitmap(this.bitMapChild);
			}
			
			// On charge l'image dans la zone de la photographie
			this.imgButton.setImageBitmap(bitMapChild);
		}
	}
	
	@Override  
	public void onBackPressed() {
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenêtre d'administration d'un profil enfant. **/
	private void toDisplay() {
		setContentView(R.layout.activity_child_administration);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialisation des elements graphiques.
		family_name = (EditText) findViewById(R.id.category_name);
		first_name = (EditText) findViewById(R.id.first_name);
		birth_date = (DatePicker) findViewById(R.id.birth_date);
		imgButton = (ImageButton) findViewById(R.id.picImg);
		logButton = (ImageButton) findViewById(R.id.imageButtonClear);
		
		valideButton = (Button) findViewById(R.id.valider);
		deleteButton = (Button) findViewById(R.id.supprimer);
		
		// On rend le bouton de suppression invisible au depart
		deleteButton.setVisibility(View.INVISIBLE);
		
		// On recupere le nom et prenom de l'enfant
		final String nom = getIntent().getStringExtra("nom");
		final String prenom = getIntent().getStringExtra("prenom");
		
		// on récupère les listviews
		lvExpAllGram = (ExpandableListView) findViewById(R.id.expandableListView1);
		lvExpChildGram = (ExpandableListView) findViewById(R.id.expandableListView2);

        topChildGram = new ArrayList<String>();
        
        // On prépare les data
        prepareListData(nom, prenom);
        
        // on initialise les adaptateurs
        listAdapterAllGram = new ExpandableTreeAdapter(this, 5, listDataHeaderAllGram, listDataChildAllGram);
        listAdapterChildGram = new ExpandableTreeAdapter(this, 5, listDataHeaderChildGram, listDataChildChildGram);
        lvExpAllGram.setAdapter(listAdapterAllGram);
        lvExpChildGram.setAdapter(listAdapterChildGram);
		
		// Modification d'un profil enfant
		if(nom != null) {
	        // On initialise la vue
			setChildToDisplay(nom);
		}
		// Creation d'un profil enfant
		else {
			createChildToDisplay(nom, prenom);
			logButton.setVisibility(View.INVISIBLE);
		}
		
		// Action du bouton-image du profil
		imgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Crer un intent qui envoie vers la camera
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				// Attent le retour de cet intent
				startActivityForResult(intent, 3);
			}
		});
		
		// Action du bouton d'historique
		logButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ChildAdministrationActivity.this, ChildLogActivity.class);
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				
				i.putExtra("nom", nom);
				i.putExtra("prenom", prenom);
				// On lance l'intent i, une nouvelle activite, en attendant un resultat
				startActivity(i);
			}
		});
}
	
	 /**
	 * Adaptation de la fenetre a l'action de modification d'un profil enfant.
	 * @param nom(String): Le nom de l'enfant dans l'objet AppData.
	 **/
	private void setChildToDisplay(final String nom) {
		// On recupere les informations relatives a l'enfant
		final String prenom = getIntent().getStringExtra("prenom");
		final String photo = getIntent().getStringExtra("photo");
		int an = getIntent().getIntExtra("an",0);
		int mois = getIntent().getIntExtra("mois",0) - 1;
		int jour = getIntent().getIntExtra("jour",0);
		
		// On remplit directement les champs du profil avec les informations obtenues
		family_name.setText(nom);
		first_name.setText(prenom);
		birth_date.updateDate(an, mois, jour);
		
		// On extrait le chemin de la photo
		String fp   = File.separator;
		String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "photo" + fp + photo;
		final String picturePath = Environment.getExternalStorageDirectory() + path;
		
		// On charge la photo dans le bouton-image prevu a cet effet
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			imgButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// On rend le bouton de suppression visible et de couleur rouge
		deleteButton.setVisibility(View.VISIBLE);
		deleteButton.setTextColor(Color.RED);
		
		// On modifie le texte du bouton de validation de "Valider" en "Modifier"
		valideButton.setText("Modifier");
		
		// Action du bouton de suppression
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        // On supprime le profile
				        // Puis on actualise le fichier XML des profils
				        // Enfin on retourne en arriere
				        case DialogInterface.BUTTON_POSITIVE:
				        	deleteChild(picturePath, nom, prenom);
				        	XMLTools.printChildren(getApplicationContext(), data.getProfils());
				            onBackPressed();

				        // Aucune modification
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de suppression
				AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				ab.setTitle("Suppression").setMessage("Voulez-vous vraiment supprimer ce profil ?")
										  .setPositiveButton("Oui", dialogClickListener)
										  .setNegativeButton("Non", dialogClickListener)
										  .setIcon(android.R.drawable.ic_delete).show();
			}
		});
		
		// Action du bouton de modification
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        // On supprime le profil enfant
				        // Puis on le rajoute avec les nouvelles donnees
				        // Enfin on retourne en arriere
				        case DialogInterface.BUTTON_POSITIVE:
				        	// Si un des deux champs est vide, alors on affiche l'erreurs
				    		if (nom.equals("") || prenom.equals("")) {
				    			AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				    			ab.setTitle("Avertissement").setMessage("Veuillez remplir tous les champs correctement.")
				    										.setIcon(android.R.drawable.ic_notification_clear_all)
				    										.setNeutralButton("Ok", null).show();
				    		}
				    		else {
				    			deleteChild(picturePath, nom, prenom);
					        	addChild();
					        	createLog(nom, prenom);
				    		}
				        // Aucune modification
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de modification
				AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment modifier ce profil ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/**
	 * Supprime le profil d'un enfant ainsi que sa photo.
	 * @param path(String): Le chemin de la photo.
	 * @param nom(String): Le nom de l'enfant.
	 * @param prenom(String): Le prénom de l'enfant.
	 **/
	protected void deleteChild(String path, String nom, String prenom){
		data.deleteProfil(nom, prenom);
    	File photo = new File(path);
    	photo.delete();
	}
	
	/** 
	 * Cree le profil d'un enfant en remplissant les champs nom, prenom, date de naissance et photo si besoin.
	 **/
	protected void addChild(){
		// On recupere les informations relatives a l'enfant
		String familyName = family_name.getText().toString();
		String firstName = first_name.getText().toString();
		imgButton.buildDrawingCache();
		Bitmap bitMap = imgButton.getDrawingCache();
		
		// Si un des deux champs est vide, alors on affiche l'erreurs
		if (familyName.equals("") || firstName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir tous les champs correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		}
		// On remplit directement les champs du profil avec les informations obtenues
		else {
			// On recupere la date de naissance
			int birthDay = birth_date.getDayOfMonth();
			int birthMonth = birth_date.getMonth() + 1;
			int birthYear = birth_date.getYear();
			
			// On recupere la liste des grammaires de l'enfant
			ArrayList <Grammar> grammars = new ArrayList<Grammar>();
			for(int i=0; i < topChildGram.size(); i++) {
				grammars.add(data.getGrammarByName(topChildGram.get(i)));
			}

			String fp   = File.separator;
			String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "photo";
			String picturePath = Environment.getExternalStorageDirectory() + path;
			String pictureName = new String(familyName + "_" + firstName + ".png");
			
			FileOutputStream out;
			File fileName = new File(picturePath, pictureName);
			
			if(bitMapChild!=null) {
				try {
					out = new FileOutputStream(fileName);
					bitMapChild.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					out = new FileOutputStream(fileName);
					bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// Ajout du nouvel enfant.
			data.addProfil(new Child(familyName, firstName, birthDay, birthMonth, birthYear, pictureName, grammars));
			
			XMLTools.printChildren(getApplicationContext(), data.getProfils());
			onBackPressed();
		}
	}
	
	/** 
	 * Adaptation de la fenetre a l'action de creation d'un profil enfant. 
	 **/
	protected void createChildToDisplay(final String nom, final String prenom) {
		// Action du bouton de creation
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On ajoute le profil enfant
				// Puis on actualise le fichier XML des profils
		        // Enfin on retourne en arriere
				addChild();
				createLog(nom, prenom);
			}
		});
	}
	
	/**
	* Creer ou renomme, selon son existance, le fichier log de l'enfant.
	* @param nom(String): Le nom de l'enfant.
	* @param prenom(String): Le prenom de l'enfant.
	**/
	protected void createLog(String nom, String prenom) {
		// On recupere les nouveaux nom et prenom de l'enfant
		String familyName = family_name.getText().toString();
		String firstName = first_name.getText().toString();
		
		String fp   = File.separator;
		String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "log" + fp;
		String filePath = Environment.getExternalStorageDirectory() + path;
		File newFileLog = new File(filePath + familyName + "_" + firstName + ".xml");
		File oldFileLog = new File(filePath + nom + "_" + prenom + ".xml");
		// Si un fichier log existait deja pour l'enfant
		if (oldFileLog.exists()) {
			// On en cree un avec son nouveau nom
			oldFileLog.renameTo(newFileLog);
		}
		// Si un fichier log n'existait pas deja pour l'enfant
		else {
			// On renomme celui de son ancien nom avec le nouveau, p-ê inutile si aucun changement de nom
			XMLTools.createEmptyXML(filePath + familyName + "_" + firstName + ".xml", "logs");
		}
	}
	
	/**
	* Prepare la liste des grammaires dans les listes deroulantes.
	* @param nom(String): Le nom de l'enfant.
	**/
    private void prepareListData(String nom, String prenom) {
    	List<Grammar> grammars = data.getGrammars();
    	
    	// Liste de gauche, toutes les grammaires
        listDataHeaderAllGram = new ArrayList<String>();
        listDataChildAllGram = new HashMap<String, List<String>>();
        listDataHeaderAllGram.add("Toutes les grammaires...");
        List<String> topAllCat = new ArrayList<String>();

        for (int i =0; i < grammars.size(); i++) {
        	String name = grammars.get(i).getName();
        	topAllCat.add(name);
        	if((nom!=null) && (nom.equals(name))) {
        		topAllCat.remove(name);
        	}
        }
        listDataChildAllGram.put(listDataHeaderAllGram.get(0), topAllCat);
        
        // Liste de droite, seulement les grammaires de l'enfant
        listDataHeaderChildGram = new ArrayList<String>();
        listDataChildChildGram = new HashMap<String, List<String>>();
        listDataHeaderChildGram.add("Grammaires de l'enfant...");
        
        // Cas d'une modification
        if(nom != null) {
        	ArrayList<Grammar> grammarsOfChild = data.getChildByName(nom, prenom).getGrammars();
        	if(grammarsOfChild != null) {
                for (int i =0; i < grammarsOfChild.size(); i++) {
                	if(grammarsOfChild.get(i) != null) {
                		topChildGram.add(grammarsOfChild.get(i).getName());
                	}
                }
                listDataChildChildGram.put(listDataHeaderChildGram.get(0), topChildGram);
        	}
        }
        else { // Cas d'un ajout, aucune grammaire selectionnee, le champ nom est vide
            listDataChildChildGram.put(listDataHeaderChildGram.get(0), topChildGram);
        }
        
        lvExpAllGram.setOnChildClickListener(new OnChildClickListener() {
        	
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String grammarName = listDataChildAllGram.get(listDataHeaderAllGram.get(groupPosition)).get(childPosition);
                if(!topChildGram.contains(grammarName)) {
                	topChildGram.add(grammarName);
                }
                listAdapterChildGram.notifyDataSetChanged();

                return false;
            }
        });
        
        lvExpChildGram.setOnChildClickListener(new OnChildClickListener() {
        	
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String grammarName = listDataChildChildGram.get(listDataHeaderChildGram.get(groupPosition)).get(childPosition);
                topChildGram.remove(grammarName);
                listAdapterChildGram.notifyDataSetChanged();

                return false;
            }
        });
    }
}
