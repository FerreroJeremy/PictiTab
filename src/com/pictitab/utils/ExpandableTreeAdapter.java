package com.pictitab.utils;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pictitab.ui.GrammarAdministrationActivity;
import com.pictitab.ui.LexiconAdministrationActivity;
import com.pictitab.ui.R;
import com.pictitab.ui.RuleAdministrationActivity;
import com.pictitab.ui.SelectCategoryActivity;
import com.pictitab.ui.SelectGrammarActivity;

@SuppressLint("InflateParams")
public class ExpandableTreeAdapter extends BaseExpandableListAdapter {

	private Context context;
	private int mode; // Mode of usage of the list
	private List<String> listDataHeader; // Header of the column of the list
	private HashMap<String, List<String>> listDataChild; // children list of
															// each element

	ImageButton nextButton; // Button to go to the next element of the list
	ImageButton editButton; // Button to update the element of the list
	ImageButton delButton; // Button to delete the element from the list

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == CONSTRUCTORS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Constructor of ExpandableTreeAdapter class.
	 * 
	 * @param context
	 *            (Context): Context
	 * @param mode
	 *            (int): Mode of list utilization
	 * @param listDataHeader
	 *            (List<String>): list of header of list
	 * @param listChildData
	 *            ( HashMap<String, List<String>>): children list for each
	 *            element
	 **/
	public ExpandableTreeAdapter(Context context, int mode,
			List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this.context = context;
		this.mode = mode;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listChildData;
	}

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == GETTERS & SETTERS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Return the element of the group with the id groupPosition and of the
	 * sub-element with id childPosition in the list.
	 * 
	 * @param groupPosition
	 *            (int): group id
	 * @param childPosition
	 *            (int): sub-element id
	 * @return Element.
	 **/
	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	/**
	 * Return the id of the element in the list.
	 * 
	 * @param groupPosition
	 *            (int): group id
	 * @param childPosition
	 *            (int): sub-element id
	 * @return id.
	 **/
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * Return the view of the list.
	 * 
	 * @param groupPosition
	 *            (int): group id
	 * @param childPosition
	 *            (int): sub-element id
	 * @param isLastChild
	 *            (boolean): If the element is the last child of not
	 * @param convertView
	 *            (View): converted View
	 * @param parent
	 *            (ViewGroup): Parent view
	 * @return View.
	 **/
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String childText = (String) getChild(groupPosition, childPosition);

		// Simple case (selection of elements)
		// List of the selection of the grammars in the administration menu of
		// children
		if (mode == 5) {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_item, null);
			}
		}
		// Categories tree case (only next arrow button)
		// List of the selection of the categories in the edition of the rule or
		// a lexicon entry
		if (mode == 0 || mode == 1) {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_item_button,
						null);
			}
			nextButton = (ImageButton) convertView
					.findViewById(R.id.nextButton);
			nextButton.setFocusable(false);

			nextButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (mode) {
					case 0: // RULES
						RuleAdministrationActivity.nextTree(groupPosition,
								childPosition);
						break;
					case 1: // LEXICON
						LexiconAdministrationActivity.nextTree(groupPosition,
								childPosition);
						break;
					default:
						break;
					}
				}
			});
		}
		// List with delete, modify and arrow buttons cases
		if (mode != 0 && mode != 1 && mode != 5) {
			// Category selection case
			if (mode == 2) {
				if (convertView == null) {
					LayoutInflater infalInflater = (LayoutInflater) this.context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = infalInflater.inflate(
							R.layout.list_item_3_buttons, null);
				}
				nextButton = (ImageButton) convertView
						.findViewById(R.id.nextButton);
				nextButton.setFocusable(false);

				nextButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						SelectCategoryActivity.nextTree(groupPosition,
								childPosition);
					}
				});
			}
			// List with only delete and modify button case
			// Selection of a rule or grammar case
			else {
				if (convertView == null) {
					LayoutInflater infalInflater = (LayoutInflater) this.context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = infalInflater.inflate(
							R.layout.list_item_2_buttons, null);
				}
			}

			delButton = (ImageButton) convertView
					.findViewById(R.id.DeleteTreeButton);
			delButton.setFocusable(false);
			editButton = (ImageButton) convertView
					.findViewById(R.id.EditTreeButton);
			editButton.setFocusable(false);

			delButton.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);

			// If the category is the root category ("TOUT" in French = "ALL" in
			// English)
			if (childText.equals("TOUT")) {
				delButton.setVisibility(View.INVISIBLE);
				editButton.setVisibility(View.INVISIBLE);
			}

			delButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (mode) {
					case 2: // CATEGORY
						SelectCategoryActivity.deleteCategory(groupPosition,
								childPosition);
						break;
					case 3: // EDIT GRAMMAR
						GrammarAdministrationActivity.deleteRule(groupPosition,
								childPosition);
						break;
					case 4: // SELECT GRAMMAR
						SelectGrammarActivity.deleteGrammar(groupPosition,
								childPosition);
						break;
					default:
						break;
					}
				}
			});

			editButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (mode) {
					case 2: // CATEGORY
						SelectCategoryActivity.modifyCategory(groupPosition,
								childPosition);
						break;
					case 3: // EDIT GRAMMAR
						GrammarAdministrationActivity.modifyRule(groupPosition,
								childPosition);
						break;
					case 4: // SELECT GRAMMAR
						SelectGrammarActivity.modifyGrammar(groupPosition,
								childPosition);
						break;
					default:
						break;
					}
				}
			});
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);
		txtListChild.setText(childText);

		return convertView;
	}

	/**
	 * Return the sub-elements number.
	 * 
	 * @param groupPosition
	 *            (int): Group id
	 * @return Sub-elements number
	 **/
	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition))
				.size();
	}

	/**
	 * Return the group of elements
	 * 
	 * @param groupPosition
	 *            (int): Group id
	 * @return Group of elements
	 **/
	@Override
	public Object getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	/**
	 * Return the group number
	 * 
	 * @return Group number
	 **/
	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	/**
	 * Return the group id
	 * 
	 * @return Group id
	 **/
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * Return the view of the group of elements.
	 * 
	 * @param groupPosition
	 *            (int): group id
	 * @param isExpanded
	 *            (boolean): If the group is expanded or reduced
	 * @param convertView
	 *            (View): Convert view
	 * @param parent
	 *            (ViewGroup): Parent view
	 * @return View of the group.
	 **/
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}
		ExpandableListView mExpandableListView = (ExpandableListView) parent;
		mExpandableListView.expandGroup(groupPosition);
		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * Return if the sub-element is selected.
	 * 
	 * @param groupPosition
	 *            (int): group id
	 * @param childPosition
	 *            (int): sub-element id
	 * @param convertView
	 *            (View): Convert view
	 * @param parent
	 *            (ViewGroup): Parent view
	 * @return true if the element is selected, else false.
	 **/
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}