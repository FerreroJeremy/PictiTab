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
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Grammar;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

public class GrammarAdministrationActivity extends Activity {

	// Les donnees de l'application
	private static AppData data;
	
	// Utilitaires
	private static EditText grammarName;				// Edition du nom de la grammaire
	private Button addRule;								// Bouton d'ajout de regle dans la grammaire
	static Button editRule;								// Bouton d'edition d'une regle
	static Button delRule;								// Bouton de suppression d'une regle
	private Button valideButton;						// Bouton de creation d'une regle
	
	// Choix des grammaires
    ExpandableListView grammarRules;					// Vue de la liste deroulante des grammaires
    static List<String> listDataHeaderGrammarRules;		// Header de la liste deroulante des grammaires
    
    // Choix des regles
    static ExpandableTreeAdapter rulesAdapter;			// Vue de la liste deroulante des regles de la grammaire
    static List<String> listDataHeaderRules;			// Header de la liste deroulate des regles
    static List<String> listDataRules;					// Liste des regles de la grammaire
    static HashMap<String, List<String>> listDataChildRules;	// Liste des sous-categories rangees par regles
    
    // Informations generales de la grammaire
    static int groupPositionInTree;						// Indice du groupe de la liste
    static int childPositionInTree;						// Indice du sous-element de la liste
    String currentGrammar;								// Le nom de la grammaire courante
    static String grammarReturn = null;
    static String nameByIntent;							// Le nom de la grammaire selectionnee
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees relatives aux categories
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
		GrammarAdministrationActivity.data =(AppData)data.getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		GrammarAdministrationActivity.grammarReturn = data.getStringExtra("nom");
	}

	@Override  
	public void onBackPressed() {
		// On actualise nos donnees
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		
		// On ajoute un parametre a l'intention
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		// On remet a null la variable de retour du onBackPressed() de l'Activity RuleAdministrationActivity
		GrammarAdministrationActivity.grammarReturn = null;
		
		// On supprime la grammaire si on ne la valide pas
		//data.deleteGrammar(GrammarAdministrationActivity.grammarName.getText().toString());
		
		// Si le retour de l'intent (donc de l'activite fille) est OK on termine l'activite courante
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fen�tre d'administration d'une grammaire. **/
	private void toDisplay() {
		setContentView(R.layout.activity_grammar_administration);
		
		// On force l'affichage en mode portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialisation des elements graphiques.
		grammarName = (EditText) findViewById(R.id.gramName);
		GrammarAdministrationActivity.grammarName.setText("");
		grammarRules = (ExpandableListView) findViewById(R.id.rulesList);
		addRule = (Button) findViewById(R.id.ajRegle);
		valideButton = (Button) findViewById(R.id.ajGram);

		nameByIntent = getIntent().getStringExtra("nom");
		
		// Si on a selectionne une grammaire, mode modification
		if(nameByIntent != null) {
			GrammarAdministrationActivity.grammarReturn = null;
			// On charge le nom de la grammaire selectionnee
			GrammarAdministrationActivity.grammarName.setText(nameByIntent);
			currentGrammar = nameByIntent;
			valideButton.setText("Modifier");
			// Bouton de validation, modification de la grammaire
			valideButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveGrammar();
				}		
			});
			
			// Bouton d'ajout d'une regle
			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addRuleInExistingGrammar();
				}		
			});
		}
		else { // mode ajout
			if(GrammarAdministrationActivity.grammarReturn != null) {
				GrammarAdministrationActivity.grammarName.setText(GrammarAdministrationActivity.grammarReturn);
			}
			// Bouton de validation, ajout de la grammaire
			currentGrammar = new String("");
			valideButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(GrammarAdministrationActivity.grammarReturn != null) {
						currentGrammar = GrammarAdministrationActivity.grammarReturn;
						saveGrammar();
					}
					else {
						saveNewGrammar();
					}
				}		
			});
			
			// Bouton d'ajout d'une regle
			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(GrammarAdministrationActivity.grammarReturn != null) {
						currentGrammar = GrammarAdministrationActivity.grammarReturn;
						addRuleInExistingGrammar();
					}
					else
						createRuleWithoutGrammar();
				}	
			});
		}
		
		// On prepare les donnees de la liste des regles
		prepareListDataRules();
		
        // On definit un adaptateur en precisant l'entete et les enfants de la liste
		rulesAdapter = new ExpandableTreeAdapter(this, 3, listDataHeaderRules, listDataChildRules);
        // On affecte l'adaptateur a la liste
		grammarRules.setAdapter(rulesAdapter);
		
		// On initialise les bouttons de la vue
		initialiseButtons();
	}
	

	/** Prepare la liste des regles dans la liste deroulante. **/
    private void prepareListDataRules() {
    	// On recupere la grammaire et sa liste de regles
    	String grammarName = GrammarAdministrationActivity.grammarName.getText().toString();
    	Grammar g = data.getGrammarByName(grammarName);
		
		// Liste de toutes les categories
	    listDataHeaderRules = new ArrayList<String>();
	    listDataChildRules = new HashMap<String, List<String>>();
	    
	    // On donne une entete a la liste
	    listDataHeaderRules.add("Selectionner une regle");
	    listDataRules = new ArrayList<String>();
		
    	int size = 0;
    	if(g != null) {
    		List<ArrayList<Category>> rules = g.getRules();
    		size = rules.size();
    		
    		// On remplit la liste des regles
    	    for (int i =0; i < size; i++) {
    	    	// Le nom de la regle est la concatenation des noms de ses categories
    	    	String ruleName = "";
    	    	for(int j=0 ; j < rules.get(i).size() ; j++) {
    	    		if(j == rules.get(i).size())
    	    			ruleName += rules.get(i).get(j).getName();
    	    		else
    	    			ruleName += rules.get(i).get(j).getName() + " ";
    	    	}
    	    	listDataRules.add(ruleName);
    	    }
    	}
	    listDataChildRules.put(listDataHeaderRules.get(0), listDataRules); 
    }
	
	/** Sauvegarde la grammaire courante **/
	private void saveGrammar() {
		// On renomme la grammaire
		final String newName = GrammarAdministrationActivity.grammarName.getText().toString();
		if(newName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(GrammarAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez donner un nom a la grammaire.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		else {
			Grammar g = data.getGrammarByName(currentGrammar);
			g.rename(newName);
			// On ajoute les grammaires contenues dans data dans le XML
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** Sauvegarde la grammaire courante **/
	private void saveNewGrammar() {
		
		final String nameInTextView = GrammarAdministrationActivity.grammarName.getText().toString();
		if(nameInTextView.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(GrammarAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez donner un nom a la grammaire.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		else {
			data.addGrammar(new Grammar(nameInTextView));
			// On ajoute les grammaires contenues dans data dans le XML
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** Creer une grammaire pour lui ajouter une regle par la suite. **/
	private void createRuleWithoutGrammar() {

		final String nameInTextView = GrammarAdministrationActivity.grammarName.getText().toString();
		if(nameInTextView.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(GrammarAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez donner un nom a la grammaire.")
									    .setIcon(android.R.drawable.ic_notification_clear_all)
									    .setNeutralButton("Ok", null).show();
		}
		else {
			data.addGrammar(new Grammar(nameInTextView));
			Intent intent = new Intent(GrammarAdministrationActivity.this, RuleAdministrationActivity.class);
			//---------On envoie nos donnees!---------
			Bundle b = new Bundle();
			b.putParcelable(MainActivity.DATA_KEY, data);
			// On ajoute un parametre a l'intention.
			intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
			intent.putExtra("nom", nameInTextView);
			//----------------------------------------
			startActivityForResult(intent, 58);
		}
	}
	
	/** Creaction de la regle selectionnee **/
	private void addRuleInExistingGrammar() {
		
		Intent intent = new Intent(GrammarAdministrationActivity.this, RuleAdministrationActivity.class);
		//---------On envoie nos donnees!---------
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		// On ajoute un parametre a l'intention.
		intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
		intent.putExtra("nom", currentGrammar);
		//----------------------------------------
		startActivityForResult(intent, 57);
	}
	
	/** Modifie la regle selectionnee. **/
	public static void modifyRule(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editRule.performClick();
	}
	
	/** Supprime la regle selectionnee. **/
	public static void deleteRule(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delRule.performClick();
	}

	/** Initialisation des boutons dynamiques de la vue **/
	private void initialiseButtons() {	
		
		// Creation dynamique du bouton de modification d'une regle
		editRule = new Button(this);
		
		// Action du bouton de modification d'une regle
		editRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int indice = childPositionInTree;
				String nameToIntent = "";
				if(GrammarAdministrationActivity.nameByIntent == null) {
					nameToIntent = GrammarAdministrationActivity.grammarName.getText().toString();
				}
				else 
					nameToIntent = GrammarAdministrationActivity.nameByIntent;
				Intent intent = new Intent(GrammarAdministrationActivity.this, RuleAdministrationActivity.class);
				//---------On envoie nos donnees!---------
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				// On ajoute un parametre a l'intention.
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("nom", nameToIntent);
				intent.putExtra("indice", indice);
				//----------------------------------------
				startActivityForResult(intent, 55);
			}
		});
		
		// Creation dynamique du bouton de suppression d'une regle
		delRule = new Button(this);
		
		// Action du bouton de modification d'une regle
		delRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
							// On supprime la regle selectionnee
							int indice = childPositionInTree;
							String nameToIntent = "";
							if(GrammarAdministrationActivity.nameByIntent == null) {
								nameToIntent = GrammarAdministrationActivity.grammarName.getText().toString();
							}
							else 
								nameToIntent = GrammarAdministrationActivity.nameByIntent;
							data.getGrammarByName(nameToIntent).deleteRule(indice);
							
					    	Grammar g = data.getGrammarByName(nameToIntent);
					    	if(g != null) {
						    	List<ArrayList<Category>> rules = g.getRules();
						    	// On remplit la liste des regles
						    	listDataRules.clear();
						        for (int i =0; i < rules.size(); i++) {
						        	// Le nom de la regle est la concatenation des noms de ses categories
						        	String ruleName = "";
						        	for(int j=0 ; j < rules.get(i).size() ; j++) {
						        		if(j == rules.get(i).size())
						        			ruleName += rules.get(i).get(j).getName();
						        		else
						        			ruleName += rules.get(i).get(j).getName() + " ";
						        	}
						        	listDataRules.add(ruleName);
						        }
						        rulesAdapter.notifyDataSetChanged();
					        }

				        // On ne fait rien
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Boite de dialogue de confirmation de suppression
				AlertDialog.Builder ab = new AlertDialog.Builder(GrammarAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment supprimer cette regle ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
}
