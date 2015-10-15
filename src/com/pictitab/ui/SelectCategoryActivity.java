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

	private static AppData data;

	static Button editCat;
	static Button delCat;
	private ImageButton previousButton; // Button to go back to parent category
	static ExpandableTreeAdapter listAdapter; // Adaptater of the parent list
	ExpandableListView lvExpAllCat; // View of the parent list
	static List<String> topAllCat; // Header of the parent list
	static List<String> listDataHeaderAllCat; // Header of the child list
	static HashMap<String, List<String>> listDataChildAllCat; // List of
																// sub-categories
																// grouped by
																// categories

	static int compteur; // id of the depth
	static SparseArray<List<Category>> tree; // depth of categories
	static int groupPositionInTree;
	static int childPositionInTree;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS -- common stuff here == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */
	// Overrided functions to manage the AppData !
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
		SelectCategoryActivity.data = (AppData) data.getBundleExtra(
				MainActivity.DATAEXTRA_KEY)
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
		// View definition
		setContentView(R.layout.activity_select_category);

		// portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Draw the existing lists
		previousButton = (ImageButton) findViewById(R.id.previousButton);
		lvExpAllCat = (ExpandableListView) findViewById(R.id.CategoryGrammarList);

		List<Category> categories = data.getCategories();
		if (categories != null) {
			int size = categories.size();
			if (size == 0) {
				data.addCategory(new Category("TOUT"));
			}
		}

		initialiseButtons();

		prepareListData();

		listAdapter = new ExpandableTreeAdapter(this, 2, listDataHeaderAllCat,
				listDataChildAllCat);
		lvExpAllCat.setAdapter(listAdapter);
	}

	/**
	 * Initialize the buttons.
	 **/
	private void initialiseButtons() {

		// Dynamic creation of the modification button
		editCat = new Button(this);

		// Action of the category modification button
		editCat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String subCategoryName = listDataChildAllCat.get(
						listDataHeaderAllCat.get(groupPositionInTree)).get(
						childPositionInTree);
				Intent intent = new Intent(SelectCategoryActivity.this,
						CategoryAdministrationActivity.class);

				// Send the data
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);

				// Add a intention parameter
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("mode", 1);
				intent.putExtra("nom", subCategoryName);

				startActivityForResult(intent, 66);
			}
		});

		// Dynamic creation of the suppression button
		delCat = new Button(this);

		// Action of the category suppression button
		delCat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case DialogInterface.BUTTON_POSITIVE:
							String subCategoryName = listDataChildAllCat.get(
									listDataHeaderAllCat
											.get(groupPositionInTree)).get(
									childPositionInTree);
							if (subCategoryName.equals("TOUT")) {
								AlertDialog.Builder ab = new AlertDialog.Builder(
										SelectCategoryActivity.this);
								ab.setTitle("Avertissement")
										.setMessage(
												"Vous ne pouvez pas supprimer la categorie racine.")
										.setIcon(
												android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
							} else {
								// Delete the selected category
								data.deleteCategoryAndItsChildren(subCategoryName);

								// Clear the list
								topAllCat.clear();

								// Reload the data/list from the root
								List<Category> categories = data
										.getNotChildCategories();
								if (categories != null) {
									// Add all the sub-categories of the selected categories
									for (int i = 0; i < categories.size(); i++) {
										topAllCat.add(categories.get(i)
												.getName());
									}
								}
								// Redraw the loaded list
								listAdapter.notifyDataSetChanged();
								// Save the new data in the xml files
								XMLTools.printCategories(
										getApplicationContext(),
										data.getCategories());
								XMLTools.printLexicon(getApplicationContext(),
										data.getLexicon());
								XMLTools.printGrammars(getApplicationContext(),
										data.getGrammars());
							}
							// Do nothing
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				// Suppression alert dialog box
				AlertDialog.Builder ab = new AlertDialog.Builder(
						SelectCategoryActivity.this);
				ab.setTitle("Modification")
						.setMessage(
								"Voulez-vous vraiment supprimer cette categorie ?")
						.setPositiveButton("Oui", dialogClickListener)
						.setNegativeButton("Non", dialogClickListener)
						.setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}

	/**
	 * Update the selected category
	 **/
	public static void modifyCategory(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		editCat.performClick();
	}

	/**
	 * Delete the selected category
	 **/
	public static void deleteCategory(int groupPosition, final int childPosition) {
		groupPositionInTree = groupPosition;
		childPositionInTree = childPosition;
		delCat.performClick();
	}

	/**
	 * Redraw the list with the children of the selected category.
	 **/
	public static void nextTree(int groupPosition, final int childPosition) {
		// Get the name of the selected category
		String subCategoryName = listDataChildAllCat.get(
				listDataHeaderAllCat.get(groupPosition)).get(childPosition);
		// Get the corresponding category
		Category c = data.getCategories().get(
				data.getCategoryByName(subCategoryName));
		// Reload the list
		List<Category> categories = c.getCategories();
		int size = categories.size();
		if (size > 0) {
			// Clear the list
			topAllCat.clear();
			// Add it all the sub-categories of the selected categories
			for (int i = 0; i < size; i++) {
				topAllCat.add(categories.get(i).getName());
			}
			// Increment the depth list and add it the sub-categories
			tree.put(++compteur, categories);
			// Update the list
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Prepare the categories list.
	 **/
	private void prepareListData() {
		List<Category> categories = data.getNotChildCategories();

		// List all the categories
		listDataHeaderAllCat = new ArrayList<String>();
		listDataChildAllCat = new HashMap<String, List<String>>();

		compteur = 0;
		tree = new SparseArray<List<Category>>();

		listDataHeaderAllCat
				.add("Selectionner une categorie a laquelle ajouter une fille");
		topAllCat = new ArrayList<String>();
		// Fill the list
		for (int i = 0; i < categories.size(); i++) {
			topAllCat.add(categories.get(i).getName());
		}
		tree.put(compteur, categories);
		listDataChildAllCat.put(listDataHeaderAllCat.get(0), topAllCat);

		// Click on a list element
		lvExpAllCat.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				String subCategoryName = listDataChildAllCat.get(
						listDataHeaderAllCat.get(groupPosition)).get(
						childPosition);
				Intent intent = new Intent(SelectCategoryActivity.this,
						CategoryAdministrationActivity.class);

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
				intent.putExtra("mode", 0);
				intent.putExtra("nom", subCategoryName);
				startActivityForResult(intent, 30);

				return true;
			}
		});

		// Go back button
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (compteur >= 1) {
					compteur -= 1;
					List<Category> categories = tree.get(compteur);
					topAllCat.clear();
					for (int i = 0; i < categories.size(); i++) {
						topAllCat.add(categories.get(i).getName());
					}
					listAdapter.notifyDataSetChanged();
				}
			}
		});
	}
}
