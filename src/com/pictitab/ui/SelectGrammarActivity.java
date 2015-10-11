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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.pictitab.data.AppData;
import com.pictitab.data.Grammar;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

public class SelectGrammarActivity extends Activity {
	
	// Les donnees de l'application
	private AppData data;
	
    ExpandableListView grammarRules;								// Vue de la liste deroulante des regles de la grammaire
    static ExpandableTreeAdapter grammarsAdapter;					// Adaptateur de la liste deroulante des regles de la grammaire
	static Button editGram;											// Bouton de modification d'une grammaire
	static Button delGram;											// Bouton de suppression d'une grammaire
	private Button addGrammar;										// Bouton d'ajout d'une grammaire
	
	static List<String> listDataGram;								// Liste de toutes regles
    static List<String> listDataHeaderGrammarRules;					// Header de la liste deroulante des regles de la grammaire
    static HashMap<String, List<String>> hmGrammarRules;			// Liste des sous-categories rangees par regles
    static int groupPositionInTree;									// Indice du groupe dans la liste deroulante
    static int childPositionInTree;									// Indice du sous-groupe dans la liste deroulante
	
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
	
	/** Methode creant le contenu de la fenetre de selection des grammaires. **/
	private void toDisplay() {
		setContentView(R.layout.activity_select_grammar);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		grammarRules = (ExpandableListView) findViewById(R.id.expandableListSelectGram);
		// On prepare les donnees de la liste
		prepareListData();
		
        // On definit un adaptateur en precisant l'entete et les enfants de la liste
		grammarsAdapter = new ExpandableTreeAdapter(this, 4, listDataHeaderGrammarRules, hmGrammarRules);
        // On affecte l'adaptateur a la liste
		grammarRules.setAdapter(grammarsAdapter);
		
		// On initialise les bouttons de la vue
		initialiseButtons();
	}
	
	/** Initialisation les bouttons de la vue **/
	private void initialiseButtons() {
		addGrammar = (Button) findViewById(R.id.addGram);
		
		// Action du bouton ajouter grammaire
		addGrammar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectGrammarActivity.this, GrammarAdministrationActivity.class);
				
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				
				// On lance l'intent i, une nouvelle activite, en attendant un resultat
				startActivityForResult(i, 42);
			}
		});
		
		// Creation dynamique du bouton de modification d'une grammaire
		editGram = new Button(this);
		
		// Action du bouton de modification d'une grammaire
		editGram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On recupere la grammaire selectionnee
				String grammarName = hmGrammarRules.get(listDataHeaderGrammarRules.get(groupPositionInTree)).get(childPositionInTree);
				
				Intent i = new Intent(SelectGrammarActivity.this, GrammarAdministrationActivity.class);
				// On envoie les donnees
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				
				// On ajoute un parametre a l'intention.
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				i.putExtra("nom", grammarName);
				
				// On lance l'intent i, une nouvelle activite, en attendant un resultat
				startActivityForResult(i, 44);
			}
		});
		
		// Creation dynamique du bouton de modification d'une grammaire
		delGram = new Button(this);
		
		// Action du bouton de modification d'une grammaire
		delGram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
							// On supprime la grammaire selectionnee
							String grammarName = hmGrammarRules.get(listDataHeaderGrammarRules.get(groupPositionInTree)).get(childPositionInTree);
							data.deleteGrammar(grammarName);
							// Puis on actualise le fichier XML des grammaires
							XMLTools.printCategories(getApplicationContext(), data.getCategories());
							
							// On actualise la liste des grammaires existantes
							List<Grammar> grammars = data.getGrammars();
					        // On efface la liste deroulante
					        listDataGram.clear();
					        // On remplit la liste
					        for (int i =0; i < grammars.size(); i++) {
					        	listDataGram.add(grammars.get(i).getName());
					        }
					        // On MAJ la liste
					        grammarsAdapter.notifyDataSetChanged();

				        // On ne fait rien
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de suppression
				AlertDialog.Builder ab = new AlertDialog.Builder(SelectGrammarActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment supprimer cette grammaire ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}

	/**
	* Modifie la grammaire selectionnee.
	**/
	public static void modifyGrammar(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editGram.performClick();
	}
	
	/**
	* Supprime la grammaire selectionnee.
	**/
	public static void deleteGrammar(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delGram.performClick();
	}
	
	/**
	* Prepare la liste des categories dans la liste deroulante.
	**/
    private void prepareListData() {
    	
    	List<Grammar> grammars = data.getGrammars();
    	
    	// Liste de toutes les categories
        listDataHeaderGrammarRules = new ArrayList<String>();
        hmGrammarRules = new HashMap<String, List<String>>();
        
        // On donne une entete a la liste
        listDataHeaderGrammarRules.add("Selectionner une grammaire");
        listDataGram = new ArrayList<String>();
        // On remplit la liste
        for (int i =0; i < grammars.size(); i++) {
        	listDataGram.add(grammars.get(i).getName());
        }
        hmGrammarRules.put(listDataHeaderGrammarRules.get(0), listDataGram); 
    }
}
