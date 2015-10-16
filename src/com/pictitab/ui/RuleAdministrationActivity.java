package com.pictitab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Grammar;
import com.pictitab.utils.ExpandableTreeAdapter;

public class RuleAdministrationActivity extends Activity {

	private static AppData data;

	private ImageButton previousButton;
	private Button addRule;

	private LinearLayout listRulesLayout;
	ExpandableListView categoriesAdapter;
	static ExpandableTreeAdapter listAdapter;
	static List<String> listDataHeaderAllCat;
	static List<String> listDataCategories; // List of the grammar's rules
	static HashMap<String, List<String>> listDataChildCat; // List of
															// sub-categories
															// grouped by rule

	static SparseArray<List<Category>> tree;
	static int compteur;
	private ArrayList<Category> rule;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = (AppData) getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY)
				.getParcelable(MainActivity.DATA_KEY);
		this.toDisplay();
	}

	@Override
	public void onBackPressed() {
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		this.getIntent().putExtra("nom", getIntent().getStringExtra("nom"));
		setResult(RESULT_OK, this.getIntent());
		finish();
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == TRAITEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Display the rule administration window.
	 **/
	private void toDisplay() {
		setContentView(R.layout.activity_rule_administration);

		previousButton = (ImageButton) findViewById(R.id.previousButton);
		addRule = (Button) findViewById(R.id.addRuleInGram);
		categoriesAdapter = (ExpandableListView) findViewById(R.id.CategoryGrammarList);
		listRulesLayout = (LinearLayout) findViewById(R.id.resultLayout);

		prepareListData();

		listAdapter = new ExpandableTreeAdapter(this, 0, listDataHeaderAllCat,
				listDataChildCat);

		categoriesAdapter.setAdapter(listAdapter);

		rule = new ArrayList<Category>();

		final String nom = getIntent().getStringExtra("nom");
		final int indice = getIntent().getIntExtra("indice", -1);

		// Rule creation case
		if (indice == -1) {

			addRule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Grammar g = data.getGrammarByName(nom);
					g.addRule(rule);
					onBackPressed();
				}
			});
		}
		// Rule modification case
		else {
			rule = data.getGrammarByName(nom).getRuleAt(indice);
			ArrayList<Category> rulebis = new ArrayList<Category>();
			for (int i = 0; i < rule.size(); i++) {
				final String name = rule.get(i).getName();
				int index = data.getCategoryByName(name);
				if (index != -1) {
					Category c = data.getCategories().get(index);
					rulebis.add(c);
				}
			}
			rule = rulebis;
			drawRuleLayout();

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

	/**
	 * Redraw the scroll list with children (sub-categories) of the selected
	 * categories.
	 **/
	public static void nextTree(int groupPosition, final int childPosition) {
		String subCategoryName = listDataChildCat.get(
				listDataHeaderAllCat.get(groupPosition)).get(childPosition);
		Category c = data.getCategories().get(
				data.getCategoryByName(subCategoryName));
		List<Category> categories = c.getCategories();
		int size = categories.size();
		if (size > 0) {
			listDataCategories.clear();
			for (int i = 0; i < size; i++) {
				listDataCategories.add(categories.get(i).getName());
			}
			tree.put(++compteur, categories);
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Redraw the rule's layout.
	 **/
	private void drawRuleLayout() {
		// clear the content of the layout
		listRulesLayout.removeAllViews();

		for (int i = 0; i < rule.size(); i++) {

			final String name = rule.get(i).getName();
			final Button tmp = new Button(RuleAdministrationActivity.this);
			tmp.setText(name);

			// add the button in the layout
			listRulesLayout.addView(tmp);

			// Action of the button
			tmp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Category c = data.getCategories().get(
							data.getCategoryByName(name));
					rule.remove(c);
					drawRuleLayout();
				}
			});
		}
	}

	/**
	 * Prepare the categories list.
	 **/
	private void prepareListData() {
		List<Category> categories = data.getNotChildCategories();

		listDataHeaderAllCat = new ArrayList<String>();
		listDataChildCat = new HashMap<String, List<String>>();

		compteur = 0;
		tree = new SparseArray<List<Category>>();

		String selected_element = getResources().getString(
				R.string.selected_element);
		listDataHeaderAllCat.add(selected_element);

		// Fill the list
		listDataCategories = new ArrayList<String>();
		for (int i = 0; i < categories.size(); i++) {
			listDataCategories.add(categories.get(i).getName());
		}
		tree.put(compteur, categories);
		listDataChildCat.put(listDataHeaderAllCat.get(0), listDataCategories);

		// When a sub-categories is selected
		categoriesAdapter.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				String subCategoryName = listDataChildCat.get(
						listDataHeaderAllCat.get(groupPosition)).get(
						childPosition);
				Category c = data.getCategories().get(
						data.getCategoryByName(subCategoryName));
				rule.add(c);
				drawRuleLayout();

				return true;
			}
		});

		// Back button
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (compteur >= 1) {
					compteur -= 1;
					List<Category> categories = tree.get(compteur);
					listDataCategories.clear();
					for (int i = 0; i < categories.size(); i++) {
						listDataCategories.add(categories.get(i).getName());
					}
					listAdapter.notifyDataSetChanged();
				}
			}
		});
	}
}
