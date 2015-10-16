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

	private AppData data;

	ExpandableListView grammarRules;
	static ExpandableTreeAdapter grammarsAdapter;
	static Button editGram;
	static Button delGram;
	private Button addGrammar;

	static List<String> listDataGram; // List of all rules
	static List<String> listDataHeaderGrammarRules;
	static HashMap<String, List<String>> hmGrammarRules; // list of rules
															// grouped by
															// grammar
	static int groupPositionInTree; // id of the group in the list
	static int childPositionInTree; // id of the sub-group in the list

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */
	// Classic stuff
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (AppData) getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY)
				.getParcelable(MainActivity.DATA_KEY);
		this.toDisplay();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.toDisplay();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.data = (AppData) data.getBundleExtra(MainActivity.DATAEXTRA_KEY)
				.getParcelable(MainActivity.DATA_KEY);
	}

	@Override
	public void onBackPressed() {
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		setResult(RESULT_OK, this.getIntent());
		finish();
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == PROCESS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Display the window.
	 **/
	private void toDisplay() {
		setContentView(R.layout.activity_select_grammar);

		// portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		grammarRules = (ExpandableListView) findViewById(R.id.expandableListSelectGram);
		prepareListData();

		grammarsAdapter = new ExpandableTreeAdapter(this, 4,
				listDataHeaderGrammarRules, hmGrammarRules);
		grammarRules.setAdapter(grammarsAdapter);

		initialiseButtons();
	}

	/**
	 * Initialize the buttons of the window.
	 **/
	private void initialiseButtons() {
		addGrammar = (Button) findViewById(R.id.addGram);

		// Action of the grammar creation button
		addGrammar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectGrammarActivity.this,
						GrammarAdministrationActivity.class);

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 42);
			}
		});

		// Dynamic creation of the modification button
		editGram = new Button(this);

		// Action of the modification button
		editGram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get the selected grammar
				String grammarName = hmGrammarRules.get(
						listDataHeaderGrammarRules.get(groupPositionInTree))
						.get(childPositionInTree);

				Intent i = new Intent(SelectGrammarActivity.this,
						GrammarAdministrationActivity.class);

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				i.putExtra("nom", grammarName);
				startActivityForResult(i, 44);
			}
		});

		// Dynamic creation of the suppression button
		delGram = new Button(this);

		// Action of delete button
		delGram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Delete
							String grammarName = hmGrammarRules.get(
									listDataHeaderGrammarRules
											.get(groupPositionInTree)).get(
									childPositionInTree);
							data.deleteGrammar(grammarName);
							// Update the xml file
							XMLTools.printCategories(getApplicationContext(),
									data.getCategories());

							List<Grammar> grammars = data.getGrammars();

							listDataGram.clear();

							for (int i = 0; i < grammars.size(); i++) {
								listDataGram.add(grammars.get(i).getName());
							}

							grammarsAdapter.notifyDataSetChanged();

						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				// Alert box
				AlertDialog.Builder ab = new AlertDialog.Builder(
						SelectGrammarActivity.this);
				ab.setTitle(R.string.modify)
						.setMessage(R.string.dialog_box_delete)
						.setPositiveButton(R.string.yes, dialogClickListener)
						.setNegativeButton(R.string.no, dialogClickListener)
						.setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}

	/**
	 * Update the selected grammar.
	 **/
	public static void modifyGrammar(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editGram.performClick();
	}

	/**
	 * Delete the selected grammar.
	 **/
	public static void deleteGrammar(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delGram.performClick();
	}

	/**
	 * Prepare the categories list.
	 **/
	private void prepareListData() {

		List<Grammar> grammars = data.getGrammars();

		listDataHeaderGrammarRules = new ArrayList<String>();
		hmGrammarRules = new HashMap<String, List<String>>();

		String selected_element = getResources().getString(
				R.string.selected_element);
		listDataHeaderGrammarRules.add(selected_element);
		listDataGram = new ArrayList<String>();

		for (int i = 0; i < grammars.size(); i++) {
			listDataGram.add(grammars.get(i).getName());
		}
		hmGrammarRules.put(listDataHeaderGrammarRules.get(0), listDataGram);
	}
}
