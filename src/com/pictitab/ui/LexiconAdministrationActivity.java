package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

public class LexiconAdministrationActivity extends Activity {
	
	// Les donnees de l'application
	private static AppData data;
	
	// Informations generales de l'entree du lexique
	private EditText lexiconName;			// Edition du nom de l'entree du lexique
	private TextView selectedCategory;		// Vue indiquant la categorie de l'entree du lexique
	
	// Selection de l'image representant le mot
	private ImageView lexiconImageView;		// Vue de l'image chargee
	private Bitmap lexiconBit;				// Image associee de l'entree du lexique
	private Button lexiconFileButton;		// Bouton de selection de l'image a partir d'un fichier
	private Button lexiconCameraButton;		// Bouton de capture d'image a partir de la camera
	
	private String pictureOrigin;			// Provenance de l'image : camera ou selection de fichier
	private String picturePathOnDevice;		// Localisation physique de l'image dans la tablette
	
	// Liste dynamique des categories et de leurs sous-categories
	private ImageButton previousButton;							// Boutton pour retourner a la categorie mere
    ExpandableListView lvExpAllCat;								// Vue de la liste deroulante de toutes les categories
    static ExpandableTreeAdapter listAdapter;					// Adaptateur de la liste deroulante
    
    static List<String> listDataHeaderAllCat;					// Header de la liste deroulante
    static List<String> topAllCat;								// Liste de toutes les categories
    static HashMap<String, List<String>> listDataChildAllCat;	// Liste des sous-categories rangees par categories meres
    static int compteur;					 					// Indice de la profondeur de l'arborescence
    static SparseArray<List<Category>> tree; 					// Indique la profondeur d'une categorie dans l'arborescence
    private String categorieName;								// Le nom de la categorie selectionnee par l'utilisateur
    
    // Suppression et validation d'une entree
	private Button valideButton;			// Bouton de suppression de l'entree
	private Button deleteButton;			// Bouton d'ajout de l'entree
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees relatives au lexique
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
			this.lexiconBit = (Bitmap) data.getExtras().get("data");
			
			// On recupere le chemin de l'image selectionnee.
			this.picturePathOnDevice = data.getExtras().getString(SelectPictureActivity.DATA_Picture);
			this.testExtras();
			this.lexiconImageView.setImageBitmap(lexiconBit);
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
	
	/** Mise en place de la fenetre d'administration d'une entree du lexique. **/
	private void toDisplay() {
		setContentView(R.layout.activity_lexicon_administration);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialisation des elements graphiques.
		this.lexiconName = (EditText) findViewById(R.id.lexicon_word);
		this.selectedCategory = (TextView) findViewById(R.id.textCatSelect);
		
		this.lexiconImageView =(ImageView) findViewById(R.id.lexiconImageView);
		this.lexiconFileButton =(Button) findViewById(R.id.lexiconFileButton);
		this.lexiconCameraButton =(Button) findViewById(R.id.lexiconCameraButton);
		
		this.previousButton = (ImageButton) findViewById(R.id.previousButton1);
		this.lvExpAllCat = (ExpandableListView) findViewById(R.id.expListCat);
		
		this.valideButton = (Button) findViewById(R.id.lexiconValidateButton);
		this.deleteButton = (Button) findViewById(R.id.lexiconDeleteButton);
		
		// On rend le bouton de suppression invisible au depart
		this.deleteButton.setVisibility(View.INVISIBLE);
		
		// On prepare les donnees de la liste
		prepareListData();
		
		// On definit un adaptateur en precisant l'entete et les enfants de la liste
	    LexiconAdministrationActivity.listAdapter = new ExpandableTreeAdapter(this, 1, listDataHeaderAllCat, listDataChildAllCat);
	    
	    // On affecte l'adaptateur a la liste
	    this.lvExpAllCat.setAdapter(listAdapter);
		
		// Initialisation des variables.
		this.pictureOrigin =new String("");
		this.categorieName =new String("");
		
		// On recupere le numero de l'entree dans l'objet AppData.
		final String mot = getIntent().getStringExtra(SelectLexiconActivity.DATA_LEXICON);
		
		// Modification d'une entree du lexique
		if(mot != null)
			setLexiconToDisplay(mot);
		// Creation d'une entree du lexique
		else
			createLexiconToDisplay();
		
		// Action du bouton de chargement de l'image par selection de fichier
		this.lexiconFileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LexiconAdministrationActivity.this, SelectPictureActivity.class);
				startActivityForResult(intent, 60);
			}
		});
		
		// Action du bouton de capture de l'image par la camera
		this.lexiconCameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 7);
			}
		});
	}
	
	/** Prepare la liste des categories dans la liste deroulante. **/
    private void prepareListData() {
    	// Initialisation de la liste deroulante de toutes les categories
        listDataHeaderAllCat = new ArrayList<String>();
        listDataHeaderAllCat.add("Selectionner une categorie...");
        listDataChildAllCat = new HashMap<String, List<String>>();
        
        // Initialisation de l'arborescence des categories
        compteur = 0;
        tree = new SparseArray<List<Category>>();
        
        // On recupere les categories qui ne sont pas categories filles puis on les insere dans l'arbre de profondeur 0
    	List<Category> categories = data.getNotChildCategories();
    	tree.put(compteur, categories);
        
        // On remplit la liste des noms des categories
        topAllCat = new ArrayList<String>();
        for (int i =0; i < categories.size(); i++) {
        	topAllCat.add(categories.get(i).getName());
        }
        
        // On remplit la liste deroulante avec le header et la liste des categories non filles
        listDataChildAllCat.put(listDataHeaderAllCat.get(0), topAllCat);
        
        // Action du bouton de retour a la categorie mere dans l'arborescence des categories
        previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On ne peut acceder aux meres que si l'on n'est pas deja au niveau de la racine avec compteur = 0
				if(compteur >=1) {
					// On recupere la liste des categories de profondeur compteur - 1
					List<Category> categories = tree.get(--compteur);
					
					// On efface puis on reconstruit la liste des noms des categories
					topAllCat.clear();
	                for (int i =0; i < categories.size(); i++) {
	                	topAllCat.add(categories.get(i).getName());
	                }
	                
	                // On met a jour la liste deroulante
	                listAdapter.notifyDataSetChanged();
				}
			}
        });
        
        // Action d'un clic sur un element de la liste
        lvExpAllCat.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	// On recupere le nom de la categorie selectionnee
                categorieName =listDataChildAllCat.get(listDataHeaderAllCat.get(groupPosition)).get(childPosition);
                
                // On actualise la TextView correspond a la categorie
                selectedCategory.setText("Categorie : " + categorieName);
                
                // On met a jour la liste deroulante
                listAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }
	
	/**
	 * Adaptation de la fenetre a l'action de modification d'une entree du lexique.
	 * @param num(int): Le numero de l'entree dans l'objet AppData.
	 **/
	private void setLexiconToDisplay(final String mot) {
		// On affiche le nom de la categorie dans le champ prevu a cet effet
		Lexicon lex =data.getWordByName(mot);
		String word =lex.getWord();
		Category c = lex.getCategory();
		selectedCategory.setText("Categorie : " + c.getName());
		this.categorieName = c.getName();
		
		// On identifie la provenance de l'image
		final String picturePath =lex.getPictureSource();
		this.setPictureOrigin(picturePath);
		
		// On charge l'image
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitMap = BitmapFactory.decodeStream(inputStream);
			lexiconImageView.setImageBitmap(bitMap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// On charge le nom de l'entree
		lexiconName.setText(word);
		
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
				        // On supprime l'entree a partir de sa localisation et de son nom
				        // Puis on actualise le fichier XML du lexique
				        // Enfin on retourne en arriere
				        case DialogInterface.BUTTON_POSITIVE:
				        	deleteLexicon(picturePath, mot);
				        	XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
				            onBackPressed();
				            
				        // Aucune modification
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de suppression
				AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
				ab.setTitle("Suppression").setMessage("Voulez-vous vraiment supprimer cette entree ?")
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
				        // On supprime l'entree a partir de sa localisation et de son nom
				        // Puis on actualise le fichier XML du lexique
				        case DialogInterface.BUTTON_POSITIVE:
				        	modifyLexicon(picturePath, mot);
				        	XMLTools.printLexicon(getApplicationContext(), data.getLexicon());

				        // Aucune modification
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de modification
				AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment modifier cette entree ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/**
	 * Methode permettant de definir l'origine de l'image associee a l'entree du lexique.
	 * Cette derniere peut provenir de l'appareil photo OU du dossier "Pictures".
	 * @param picturePath(String) Chemin de l'image.
	 */
	private void setPictureOrigin(String picturePath) {
		if(picturePath.contains(File.separator +"Pictures"+File.separator))
			this.pictureOrigin =new String("Picture");
		else
			this.pictureOrigin =new String("Camera");
	}
	
	/**
	 * Supprime l'entree ainsi que son image.
	 * @param picturePath(String): Le chemin complet de l'image.
	 * @param num(int): Le numero de l'entree dans l'objet AppData.
	 **/
	protected void deleteLexicon(String picturePath, String mot){
		data.deleteWord(mot);
		
		// Dans le cas ou une photo a ete prise par la camera, on la supprime.
		if(this.pictureOrigin.equals("Camera")) {
	    	File picture = new File(picturePath);
	    	picture.delete();
		}
	}
	
	/**
	 * Modifie l'entree ainsi que son image.
	 * @param picturePath(String): Le chemin complet de l'image.
	 * @param num(int): Le numero de l'entree dans l'objet AppData.
	 **/
	protected void modifyLexicon(String picturePath, String mot) {
		
		String newName = lexiconName.getText().toString();
		if(newName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez donner un nom au mot.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Si l'entree existe deja
		else if((!newName.equals(mot)) && (wordIsExist(newName, this.categorieName))) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette entree existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		else {
			Category newCategory = null;
			if(categorieName.equals("")) {
				newCategory = data.getWordByName(mot).getCategory();
			}
			else {
				newCategory = data.getCategories().get(data.getCategoryByName(categorieName));
			}
			
			deleteLexicon(picturePath, mot);
			
			lexiconImageView.buildDrawingCache();
			File fileName = new File(picturePath);
			FileOutputStream out;
			try {
				out = new FileOutputStream(fileName);
				Bitmap bitMap = this.lexiconImageView.getDrawingCache();
				// On compresse la photo-image a une certaine taille
				bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);

				lexiconBit = bitMap;
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			data.addLexicon( new Lexicon(newName, picturePath, newCategory));
			onBackPressed();
		}
	}
	
	/**
	 * Cree le lexique en remplissant le mot et l'image si besoin est.
	 * @param picturePath(String): Chemin du dossier si l'image est une photo OU
	 * Chemin complet de l'image si l'image provient du dossier "Pictures"
	 **/
	protected void addLexicon(String picturePath) {
		// On charge le nom et la photo de l'entree
		String name = this.lexiconName.getText().toString();
		this.lexiconImageView.buildDrawingCache();
		// On verifie que les champs soient bien tous remplis
		if ( name.equals("") || picturePath.equals("") || this.categorieName.equals("") ) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez renseigner toutes les informations correctement.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// Si l'entree existe deja
		else if(wordIsExist(name, this.categorieName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(LexiconAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Cette entree existe deja.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		// On charge l'image, par selection de fichier ou par camera
		else {
			loadPicture(picturePath, name);
		}
	}
	
	/**
	 * Charge l'image en fonction de sa provenance ("Picture" ou "Camera") et l'ajoute a l'objet AppData.
	 * @param picturePath(String): Chemin du dossier si l'image est une photo OU
	 * Chemin complet de l'image si l'image provient du dossier "Pictures"
	 **/
	private void loadPicture(String picturePath, String name) {
		// Si la photo-image provient d'un fichier, on garde le meme picturePath
		// Sinon on le met a jour
		if(!this.pictureOrigin.equals("Pictures")) {
			picturePath += name+".png";
			File fileName = new File(picturePath);
			FileOutputStream out;
			
			// Chargement d'une ancienne photo-image
			if(lexiconBit!=null) {
				try {
					out = new FileOutputStream(fileName);
					// On compresse la photo-image a une certaine taille
					lexiconBit.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// Creation d'une nouvelle photo-image
			else {
				try {
					out = new FileOutputStream(fileName);
					Bitmap bitMap = this.lexiconImageView.getDrawingCache();
					// On compresse la photo-image a une certaine taille
					bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);

					lexiconBit = bitMap;
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Ajout du nouveau lexique.
		int numCat =data.getCategoryByName(this.categorieName);
		data.addLexicon( new Lexicon(name, picturePath, LexiconAdministrationActivity.data.getCategories().get(numCat)) );
		
		// On affiche toutes les entrees et on revient sur l'activite precedente
		XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
		onBackPressed();
	}
	
	/** Adaptation de la fenetre a l'action de creaction d'une entree du lexique. **/
	private void createLexiconToDisplay() {
		// Action du bouton de creaction
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On detecte l'origine de l'image
				String picturePath;
				if(pictureOrigin.equals("Pictures"))
					picturePath = picturePathOnDevice;
				else if(pictureOrigin.equals("Camera")) {
					String fp   = File.separator;
					String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "image" + fp;
					picturePath = Environment.getExternalStorageDirectory() + path;
				}
				else
					picturePath = new String("");
				
				// On ajoute l'image aux donnees
				addLexicon(picturePath);
			}
		});
	}
    
    /**
	 * Methode permettant de tester les valeurs retournees par les activites filles.
	 * /!\ Cette methode met automatiquement a jour la provenance de l'image ("pictureOrigin") ET 
	 * l'image associee de l'entree du lexique ("lexiconBit")
	 */
	private void testExtras() {
		// On verifie que le retour a l'activite vienne de l'appareil photo.
		if(this.lexiconBit!=null) {
			lexiconImageView.setImageBitmap(this.lexiconBit);
			this.pictureOrigin =new String("Camera");
		}
		// On verifie que le retour a l'activite vienne de la selection de fichier.
		if(this.picturePathOnDevice!=null && !this.picturePathOnDevice.equals("")) {
			try {
				this.lexiconBit =BitmapFactory.decodeStream(new FileInputStream(this.picturePathOnDevice));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.pictureOrigin =new String("Pictures");
		}
	}
	
	/** 
	* Redessine la liste deroulante en affichant les filles de la categorie selectionnee.
	* @param groupPosition(int): Indice de la colonne de la liste deroulante, ici 0
	* @param childPosition(int): Indice de la categorie dans la liste deroulante
	**/
	public static void nextTree(int groupPosition, final int childPosition) {
		// On recupere le nom de la categorie selectionnee
		String subCategoryName = listDataChildAllCat.get(listDataHeaderAllCat.get(groupPosition)).get(childPosition);
		
		// On recupere la categorie correspondante
        Category c = data.getCategories().get(data.getCategoryByName(subCategoryName));
        
        // On recharge la liste deroulante
        List<Category> categories = c.getCategories();
        int size = categories.size();
    	if(size > 0) {
    		// On efface la liste deroulante
    		topAllCat.clear();
    		
    		// On y ajoute toutes les sous-categories de la categorie selectionnee
            for (int i =0; i < size; i++) {
            	topAllCat.add(categories.get(i).getName());
            }
            
            // On incremente la profondeur de l'arborescence et on y ajoute les sous-categories
            tree.put(++compteur, categories);
            
            // On met a jour la liste deroulante
            listAdapter.notifyDataSetChanged();
    	}
	}
	
	/** 
	* Retourne si le couple (mot, categorie) entree existe deja ou non.
	* @param word(String): Le mot correspondant a l'entree
	* @param category(String): la categorie correspondant a l'entree
	* @return (boolean) : true si l'entree existe deja, false sinon
	**/
	public static boolean wordIsExist(String word, String category) {
		// On recupere l'entree correspondante
        Lexicon l = data.getWordByName(word);
        // Si elle existe
        if(l != null) {
        	// On compare sa categorie
        	String c = l.getCategory().getName();
        	if(c.equals(category)) {
        		return true;
        	}
        }
        return false;
	}
}
