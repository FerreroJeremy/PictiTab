package com.pictitab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

public class SelectCategoryActivity extends Activity {

	// Les donnees de l'application
	private static AppData data;
		
	static Button editCat;
	static Button delCat;
	private ImageButton previousButton;							// Boutton pour retourner a la categorie mere
	static ExpandableTreeAdapter listAdapter;					// Adaptateur de la liste deroulante des meres
    ExpandableListView lvExpAllCat;								// Vue de la liste deroulante des categories meres
    static List<String> topAllCat;								// Header de la liste deroulante des meres
    static List<String> listDataHeaderAllCat;					// Header de la liste deroulante des filles
    static HashMap<String, List<String>> listDataChildAllCat;	// Liste des sous-categories rangees par categories meres qui ne sont pas filles
    
    static int compteur;										// Indice de la profondeur de l'arborescence
    static SparseArray<List<Category>> tree;					// Indique la profondeur d'une categorie dans l'arborescence
    static int groupPositionInTree;								// Indice du groupe dans la liste deroulante
    static int childPositionInTree;								// Indice du sous-groupe dans la liste deroulante
	
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
		SelectCategoryActivity.data =(AppData)data.getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
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
	
	/** Mise en place de la fenetre de selection d'une categorie. **/
	private void toDisplay() {
		// Definition de la vue
		setContentView(R.layout.activity_select_category);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//  Affichage de la liste des categories deja existants
		previousButton = (ImageButton) findViewById(R.id.previousButton);
		lvExpAllCat = (ExpandableListView) findViewById(R.id.CategoryGrammarList);
		
		List<Category> categories = data.getCategories();
		if(categories != null) {
			int size = categories.size();
			if(size == 0) {
				data.addCategory(new Category("TOUT"));
			}
		}
		
		// On initialise les bouttons de la vue
		initialiseButtons();
		
		// On prepare les donnees de la liste
		prepareListData();
		
		// On definit un adaptateur en precisant l'entete et les enfants de la liste
        listAdapter = new ExpandableTreeAdapter(this, 2, listDataHeaderAllCat, listDataChildAllCat);
        // On affecte l'adaptateur a la liste
        lvExpAllCat.setAdapter(listAdapter);
	}
	
	/** Initialisation les bouttons de la vue **/
	private void initialiseButtons() {	
		
		// Creation dynamique du bouton de modification d'une categorie
		editCat = new Button(this);
		
		// Action du bouton de modification d'une categorie
		editCat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String subCategoryName = listDataChildAllCat.get(listDataHeaderAllCat.get(groupPositionInTree)).get(childPositionInTree);
				Intent intent = new Intent(SelectCategoryActivity.this, CategoryAdministrationActivity.class);
				
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("mode", 1);
				intent.putExtra("nom", subCategoryName);
				
				startActivityForResult(intent, 66);
			}
		});
		
		// Creation dynamique du bouton de modification d'une categorie
		delCat = new Button(this);
		
		// Action du bouton de modification d'une categorie
		delCat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        
				        case DialogInterface.BUTTON_POSITIVE:
				        	String subCategoryName = listDataChildAllCat.get(listDataHeaderAllCat.get(groupPositionInTree)).get(childPositionInTree);
				        	if (subCategoryName.equals("TOUT")) {
				    			AlertDialog.Builder ab = new AlertDialog.Builder(SelectCategoryActivity.this);
				    			ab.setTitle("Avertissement").setMessage("Vous ne pouvez pas supprimer la categorie racine.")
				    										.setIcon(android.R.drawable.ic_notification_clear_all)
				    										.setNeutralButton("Ok", null).show();
				    		}
				    		else {
								// On supprime la categorie selectionnee
				    			data.deleteCategoryAndItsChildren(subCategoryName);
								
					    		// On efface la liste deroulante
					    		topAllCat.clear();
	
						        // On recharge la liste deroulante à partir de la racine
						        List<Category> categories =  data.getNotChildCategories();
						        if(categories != null) {
						    		// On y ajoute toutes les sous-categories de la categorie selectionnee
						            for (int i =0; i < categories.size(); i++) {
						            	topAllCat.add(categories.get(i).getName());
						            }  	
						        }
						    	 // On MAJ la liste
					            listAdapter.notifyDataSetChanged();
						    	// Puis on actualise le fichier XML des categories
								XMLTools.printCategories(getApplicationContext(), data.getCategories());
								XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
								XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
				    		}
				        // On fait rien
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de suppression
				AlertDialog.Builder ab = new AlertDialog.Builder(SelectCategoryActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment supprimer cette categorie ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/**
	* Modifie la categorie selectionnee.
	**/
	public static void modifyCategory(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editCat.performClick();
	}
	
	/**
	* Supprime la categorie selectionnee.
	**/
	public static void deleteCategory(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delCat.performClick();
	}
	
	/**
	* Redessine la liste deroulante en affichant les filles de la categorie selectionnee.
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
            // On MAJ la liste
            listAdapter.notifyDataSetChanged();
    	}
	}
	
	/**
	* Prepare la liste des categories dans la liste deroulante.
	**/
    private void prepareListData() {
    	List<Category> categories = data.getNotChildCategories();
    	
    	// Liste de toutes les categories
        listDataHeaderAllCat = new ArrayList<String>();
        listDataChildAllCat = new HashMap<String, List<String>>();
        
        compteur = 0;
        tree = new SparseArray<List<Category>>();
        
        // On donne une entete a la liste
        listDataHeaderAllCat.add("Selectionner une categorie a laquelle ajouter une fille");
        topAllCat = new ArrayList<String>();
        // On remplit la liste
        for (int i =0; i < categories.size(); i++) {
        	topAllCat.add(categories.get(i).getName());
        }
        tree.put(compteur, categories);
        listDataChildAllCat.put(listDataHeaderAllCat.get(0), topAllCat);
        
        // Lors d'un clique sur un element (un enfant) de la liste
        lvExpAllCat.setOnChildClickListener(new OnChildClickListener() {
        	
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	
                String subCategoryName = listDataChildAllCat.get(listDataHeaderAllCat.get(groupPosition)).get(childPosition);
				Intent intent = new Intent(SelectCategoryActivity.this, CategoryAdministrationActivity.class);
				
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("mode", 0);
				intent.putExtra("nom", subCategoryName);
				
				startActivityForResult(intent, 30);

            	return true;
            }
        });

        
        // Bouton de retour
        previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(compteur >=1) {
					compteur -= 1;
					List<Category> categories = tree.get(compteur);
					topAllCat.clear();
	                for (int i =0; i < categories.size(); i++) {
	                	topAllCat.add(categories.get(i).getName());
	                }
	                listAdapter.notifyDataSetChanged();
				}
			}
        });
    }
}
