package com.pictitab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Grammar;
import com.pictitab.utils.ExpandableTreeAdapter;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ExpandableListView.OnChildClickListener;
import android.app.Activity;

public class RuleAdministrationActivity extends Activity {
	
	// Les donnees de l'application
	private static AppData data;
	
	// Utilitaires
	private ImageButton previousButton;							// Bouton de parcours des categories meres
	private Button addRule;										// Bouton d'ajout de regle dans la grammaire
	
	// Choix des categories de la regle
	private LinearLayout listRulesLayout;						// Layout de la fenetre d'administration des regles
    ExpandableListView categoriesAdapter;						// Vue de la liste deroulante des categories
    static ExpandableTreeAdapter listAdapter;					// Adaptateur de la liste deroulante
    static List<String> listDataHeaderAllCat;					// Header de la liste deroulate des categories
    static List<String> listDataCategories;						// Liste des regles de la grammaire
    static HashMap<String, List<String>> listDataChildCat;		// Liste des sous-categories rangees par regles
    
    // Informations generales de la regle
    static SparseArray<List<Category>> tree;					// Arborescence de la liste des categories
    static int compteur;										// Profondeur de l'arborescence des categories
    private ArrayList<Category> rule;							// Les categories de la regle
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// On recupere toutes les donnees relatives aux regles
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
		// On affiche l'activite
		this.toDisplay();
	}
	
	@Override  
	public void onBackPressed() {
		
		// On actualise nos donnees
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);

		// On ajoute un parametre a l'intention
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		this.getIntent().putExtra("nom", getIntent().getStringExtra("nom"));
		
		// Si le retour de l'intent (donc de l'activite fille) est OK on termine l'activite courante
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** Mise en place de la fenetre d'administration d'une regle d'une grammaire. **/
	private void toDisplay() {
		setContentView(R.layout.activity_rule_administration);
		
		// Initialisation des elements graphiques.
		previousButton = (ImageButton) findViewById(R.id.previousButton);
		addRule = (Button) findViewById(R.id.addRuleInGram);
		categoriesAdapter = (ExpandableListView) findViewById(R.id.CategoryGrammarList);
		listRulesLayout = (LinearLayout) findViewById(R.id.resultLayout);
		
		// On prepare les donnees de la liste
		prepareListData();
		
		// On definit un adaptateur en precisant l'entete et les enfants de la liste
        listAdapter = new ExpandableTreeAdapter(this, 0, listDataHeaderAllCat, listDataChildCat);
        
        // On affecte l'adaptateur a la liste
        categoriesAdapter.setAdapter(listAdapter);
        
        // On prepare le bandeau superieur contenant la regle en cours d'edition
		rule = new ArrayList<Category>();
		
		final String nom = getIntent().getStringExtra("nom");
		final int indice = getIntent().getIntExtra("indice",-1);
		
		// Cas de la creation d'une regle
		if(indice == -1) {
			// Bouton de validation, creation de la regle
			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Grammar g = data.getGrammarByName(nom);
					g.addRule(rule);
					onBackPressed();
				}		
			});
		}
		// Cas de la modification d'une regle
		else {
			//addRule.setText("Valider");
			rule = data.getGrammarByName(nom).getRuleAt(indice);
			ArrayList<Category> rulebis = new ArrayList<Category>();
			for(int i =0; i < rule.size(); i++) {
				final String name = rule.get(i).getName();
				int index = data.getCategoryByName(name);
				if(index != -1) {
					Category c = data.getCategories().get(index);
					rulebis.add(c);	
				}
			}
			rule = rulebis;
			drawRuleLayout();
			// Bouton de validation, modification de la regle
			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Grammar g = data.getGrammarByName(nom);
					g.setRuleAt(indice, rule);
					onBackPressed();
				}		
			});	
		}
	}
	
	/** Redessine la liste deroulante en affichant les filles de la categorie selectionnee. **/
	public static void nextTree(int groupPosition, final int childPosition) {
		String subCategoryName = listDataChildCat.get(listDataHeaderAllCat.get(groupPosition)).get(childPosition);
        Category c = data.getCategories().get(data.getCategoryByName(subCategoryName));
        List<Category> categories = c.getCategories();
        int size = categories.size();
    	if(size > 0) {
    		listDataCategories.clear();
            for (int i =0; i < size; i++) {
            	listDataCategories.add(categories.get(i).getName());
            }
            tree.put(++compteur, categories);
            // On MAJ la liste
            listAdapter.notifyDataSetChanged();
    	}
	}
	
	/** Redessine le layout de la regle. **/
	private void drawRuleLayout() {
    	// Efface le contenu du layout
    	listRulesLayout.removeAllViews();
    	
    	// Ajoute les boutons categorie de la regle dans la layout
        for(int i=0; i < rule.size(); i++) {
        	
        	final String name = rule.get(i).getName();
			final Button tmp = new Button(RuleAdministrationActivity.this);
			tmp.setText(name);
			
			// Ajoute le bouton dans le layout
			listRulesLayout.addView(tmp);
			
			// Action du bouton
			tmp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Category c = data.getCategories().get(data.getCategoryByName(name));
					rule.remove(c);
					drawRuleLayout();
				}
			});
        }
	}
	
	/** Prepare la liste des categories dans la liste deroulante. **/
    private void prepareListData() {
    	List<Category> categories = data.getNotChildCategories();
    	
    	// Liste de toutes les categories
        listDataHeaderAllCat = new ArrayList<String>();
        listDataChildCat = new HashMap<String, List<String>>();
        
        compteur = 0;
        tree = new SparseArray<List<Category>>();
        
        // On donne une entete a la liste
        listDataHeaderAllCat.add("Selectionner une categorie...");
        
        // On remplit la liste
        listDataCategories = new ArrayList<String>();
        for (int i =0; i < categories.size(); i++) {
        	listDataCategories.add(categories.get(i).getName());
        }
        tree.put(compteur, categories);
        listDataChildCat.put(listDataHeaderAllCat.get(0), listDataCategories);
        
        // Lors d'un clic sur un element (un enfant) de la liste
        categoriesAdapter.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	
                String subCategoryName = listDataChildCat.get(listDataHeaderAllCat.get(groupPosition)).get(childPosition);
                Category c = data.getCategories().get(data.getCategoryByName(subCategoryName));
            	rule.add(c);
            	drawRuleLayout();

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
					listDataCategories.clear();
	                for (int i =0; i < categories.size(); i++) {
	                	listDataCategories.add(categories.get(i).getName());
	                }
	                listAdapter.notifyDataSetChanged();
				}
			}
        });
    }
}
