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


	private static AppData data;
	
	private static EditText grammarName;
	private Button addRule;
	static Button editRule;
	static Button delRule;
	private Button valideButton;
	
	// Grammars choice
    ExpandableListView grammarRules;
    static List<String> listDataHeaderGrammarRules;
    
    // Rules choice
    static ExpandableTreeAdapter rulesAdapter;
    static List<String> listDataHeaderRules;
    static List<String> listDataRules;
    static HashMap<String, List<String>> listDataChildRules;
    
    // Information
    static int groupPositionInTree;						// id of the group of the list
    static int childPositionInTree;						// id of the sub-element of the list
    String currentGrammar;								// current grammar name
    static String grammarReturn = null;
    static String nameByIntent;							// selected grammar name
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	// Overrided functions to manage the AppData !
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
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

    // Common thing here!
	@Override  
	public void onBackPressed() {
		// Update the data
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		GrammarAdministrationActivity.grammarReturn = null;
		
		// Delete the grammar if it is not validate
		//data.deleteGrammar(GrammarAdministrationActivity.grammarName.getText().toString());
		
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==													TRAITEMENTS													==*/
	/*====================================================================================================================*/
	
	/** 
     * Display the window.
     **/
	private void toDisplay() {
		setContentView(R.layout.activity_grammar_administration);
		
		// portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Initialize the graphic elements.
		grammarName = (EditText) findViewById(R.id.gramName);
		GrammarAdministrationActivity.grammarName.setText("");
		grammarRules = (ExpandableListView) findViewById(R.id.rulesList);
		addRule = (Button) findViewById(R.id.ajRegle);
		valideButton = (Button) findViewById(R.id.ajGram);

		nameByIntent = getIntent().getStringExtra("nom");
		
		// Grammar selected = modification
		if(nameByIntent != null) {
			GrammarAdministrationActivity.grammarReturn = null;
			GrammarAdministrationActivity.grammarName.setText(nameByIntent);
			currentGrammar = nameByIntent;
			valideButton.setText("Modifier");

			valideButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveGrammar();
				}		
			});
			
			// Add a rule
			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addRuleInExistingGrammar();
				}		
			});
		} else { // Add mode
			if(GrammarAdministrationActivity.grammarReturn != null) {
				GrammarAdministrationActivity.grammarName.setText(GrammarAdministrationActivity.grammarReturn);
			}

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
			
			// Add rule
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
        
        // Rules part
		
		prepareListDataRules();
		rulesAdapter = new ExpandableTreeAdapter(this, 3, listDataHeaderRules, listDataChildRules);
		grammarRules.setAdapter(rulesAdapter);

		initialiseButtons();
	}
	

	/** 
     * Prepare the rules list in the scroll list.
     **/
    private void prepareListDataRules() {
    	String grammarName = GrammarAdministrationActivity.grammarName.getText().toString();
    	Grammar g = data.getGrammarByName(grammarName);
		
		// Categories
	    listDataHeaderRules = new ArrayList<String>();
	    listDataChildRules = new HashMap<String, List<String>>();
	    
	    listDataHeaderRules.add("Selectionner une regle");
	    listDataRules = new ArrayList<String>();
		
    	int size = 0;
    	if(g != null) {
    		List<ArrayList<Category>> rules = g.getRules();
    		size = rules.size();
    		
    	    for (int i =0; i < size; i++) {
    	    	// The name of the rule is the categories name concatenated.
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
	
	/** 
     * Save the current grammar
     **/
	private void saveGrammar() {
		// Rename
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
			// Save in xml file
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** 
     * Save new grammar
     **/
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
			XMLTools.printGrammars(getApplicationContext(), data.getGrammars());
			onBackPressed();
		}
	}
	
	/** 
     * Create a new grammar to add it a rule follow.
     **/
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

            // Intent things
			Bundle b = new Bundle();
			b.putParcelable(MainActivity.DATA_KEY, data);
			intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
			intent.putExtra("nom", nameInTextView);
            
			startActivityForResult(intent, 58);
		}
	}
	
	/** 
     * Create selected rule
     **/
	private void addRuleInExistingGrammar() {
		
		Intent intent = new Intent(GrammarAdministrationActivity.this, RuleAdministrationActivity.class);
		// Intent things
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
		intent.putExtra("nom", currentGrammar);

		startActivityForResult(intent, 57);
	}
	
	/** 
     * Modify the selected rule.
     **/
	public static void modifyRule(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editRule.performClick();
	}
	
	/** 
     * Delete the selected rule.
     **/
	public static void deleteRule(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delRule.performClick();
	}

	/** 
     * Initialize the dynamic buttons
     **/
	private void initialiseButtons() {
		
        // Modification button
		editRule = new Button(this);
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

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("nom", nameToIntent);
				intent.putExtra("indice", indice);
                
				startActivityForResult(intent, 55);
			}
		});
		
        // Delete button
		delRule = new Button(this);
		delRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
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
						    	listDataRules.clear();
						        for (int i =0; i < rules.size(); i++) {
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

				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Alert box to confirm
				AlertDialog.Builder ab = new AlertDialog.Builder(GrammarAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment supprimer cette regle ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
}
