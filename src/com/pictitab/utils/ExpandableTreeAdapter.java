package com.pictitab.utils;

import java.util.HashMap;
import java.util.List;

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
import com.pictitab.ui.SelectCategoryActivity;
import com.pictitab.ui.SelectGrammarActivity;
import com.pictitab.ui.RuleAdministrationActivity;

public class ExpandableTreeAdapter extends BaseExpandableListAdapter {
	
	// Informations generales de la liste
    private Context context;								// Contexte de l'application, necessaire a l'utilisation de certaines methodes des classes activity
    private int mode;										// Mode d'utilisation de la liste deroulante
    private List<String> listDataHeader;					// Header des colonnes de la liste deroulante
    private HashMap<String, List<String>> listDataChild;	// Liste des fils pour chaque element
    
    ImageButton nextButton;		// Bouton pour parcourir les fils de l'element
    ImageButton editButton;		// Bouton pour modifier l'element
    ImageButton delButton;		// Bouton pour supprimer l'element
    
	/*====================================================================================================================*/
	/*==												   CONSTRUCTEURS												==*/
	/*====================================================================================================================*/
    
    /**
	 * Constructeur de la classe ExpandableTreeAdapter.
	 * @param context(Context): Contexte de l'application
	 * @param mode(int): Mode d'utilisation de la liste deroulante
	 * @param listDataHeader(List<String>): Liste des headers de la liste deroulante
	 * @param listChildData( HashMap<String, List<String>>): Liste des fils pour chaque element
	 */
    public ExpandableTreeAdapter(Context context, int mode, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.mode = mode;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }
 
	/*====================================================================================================================*/
	/*==											  GETTERS & SETTERS													==*/
	/*====================================================================================================================*/
    
    /**
	 * Renvoie l'element du groupe d'indice groupPosition et de sous-element d'indice childPosition dans la liste deroulante.
	 * @param groupPosition(int): Indice du groupe d'elements
	 * @param childPosition(int): Indice du sous-element
	 * @return Le sous-element de la liste deroulante.
	 **/
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }
    
    /**
   	 * Renvoie l'indice de l'element dans la liste deroulante.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @param childPosition(int): Indice du sous-element
   	 * @return L'indice de l'element dans la liste deroulante.
   	 **/
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    /**
   	 * Renvoie la vue de la liste deroulante en fonction de l'action effectuee.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @param childPosition(int): Indice du sous-element
   	 * @param isLastChild(boolean): Si l'element est le dernier fils ou non
   	 * @param convertView(View): La vue a modifier
   	 * @param parent(ViewGroup): La vue parente
   	 * @return La vue de la liste deroulante adaptee a l'action effectuee.
   	 **/
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    	final String childText = (String) getChild(groupPosition, childPosition);
    	
    	// Cas de liste simple (selection d'elements)
    	// Liste deroulante de la selection des grammaires dans le menu d'administration des enfants
    	if(mode == 5) {
  			if (convertView == null) {
  				LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  				convertView = infalInflater.inflate(R.layout.list_item, null);
  			}
    	}
    	// Cas de l'arbre arborescent des categories (seulement bouton fleche suivante)
    	// Dans la selection d'une categorie lors de l'edition d'une regle ou d'un mot
    	if(mode == 0 || mode == 1) {
  			if (convertView == null) {
  				LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  				convertView = infalInflater.inflate(R.layout.list_item_button, null);
  			}
  			nextButton = (ImageButton)convertView.findViewById(R.id.nextButton);
  	        nextButton.setFocusable(false);
  	        
	        nextButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	switch(mode) {
		            	case 0 : // REGLE
		            		RuleAdministrationActivity.nextTree(groupPosition, childPosition);
		            		break;
		            	case 1 : // LEXIQUE
		            		LexiconAdministrationActivity.nextTree(groupPosition, childPosition);
		            		break;
		            	default :
		            		break;
	            	}
	            }
	        });
    	}
        // Cas de liste avec des boutons d'edition, de suppression et de fleche
    	if(mode != 0 && mode != 1 && mode != 5) {
    		if(mode == 2) { // Cas de la selection d'une categorie
    			if (convertView == null) {
	  				LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  				convertView = infalInflater.inflate(R.layout.list_item_3_buttons, null);
	  			}
      			nextButton = (ImageButton)convertView.findViewById(R.id.nextButton);
      	        nextButton.setFocusable(false);
      	        
    	        nextButton.setOnClickListener(new OnClickListener() {
    	            @Override
    	            public void onClick(View view) {
    	            	SelectCategoryActivity.nextTree(groupPosition, childPosition);
    	            }
    	        });
    		}
    		// Cas de liste seulement avec les boutons d'edition et de suppression
    		// Cas de la selection d'une regle ou d'une grammaire
    		else {
    			if (convertView == null) {
	  				LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  				convertView = infalInflater.inflate(R.layout.list_item_2_buttons, null);
	  			}
    		}
    		
  	        delButton = (ImageButton)convertView.findViewById(R.id.DeleteTreeButton);
  	        delButton.setFocusable(false);
  	        editButton = (ImageButton)convertView.findViewById(R.id.EditTreeButton);
  	        editButton.setFocusable(false);

	        delButton.setVisibility(View.VISIBLE);
	        editButton.setVisibility(View.VISIBLE);
  	        
	        // Si la categorie est la racine on affiche pas les boutons de suppression et edition
  	        if(childText.equals("TOUT")) {
  	        	delButton.setVisibility(View.INVISIBLE);
  	        	editButton.setVisibility(View.INVISIBLE);
			}
    			
            delButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	switch(mode) {
                	case 2 : // CATEGORIE
                		SelectCategoryActivity.deleteCategory(groupPosition, childPosition);
                    	break;
                	case 3 : // EDIT GRAMMAIRE
                		GrammarAdministrationActivity.deleteRule(groupPosition, childPosition);
                    	break;
                	case 4 : // SELECT GRAMMAIRE
                		SelectGrammarActivity.deleteGrammar(groupPosition, childPosition);
                    	break;
                	default :
                		break;
                	}
                }
            });
            
            editButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                	switch(mode) {
                	case 2 : // CATEGORIE
                		SelectCategoryActivity.modifyCategory(groupPosition, childPosition);
                    	break;
                	case 3 : // EDIT GRAMMAIRE
                		GrammarAdministrationActivity.modifyRule(groupPosition, childPosition);
                    	break;
                	case 4 : // SELECT GRAMMAIRE
                		SelectGrammarActivity.modifyGrammar(groupPosition, childPosition);
                    	break;
                	default :
                		break;
                	}
                }
            });
    	}

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        
        return convertView;
    }
    
    /**
   	 * Renvoie le nombre de sous-elements.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @return Le nombre de sous-elements.
   	 **/
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }
    
    /**
   	 * Renvoie le groupe d'elements.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @return Le groupe d'elements.
   	 **/
    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }
    
    /**
   	 * Renvoie le nombre de groupes d'elements.
   	 * @return Le nombre de groupes d'elements.
   	 **/
    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }
    
    /**
   	 * Renvoie l'indice du groupe d'elements.
   	 * @return L'indice du groupe d'elements.
   	 **/
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    /**
   	 * Renvoie la vue du groupe d'elements.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @param isExpanded(boolean): Si le groupe est developpe ou reduit
   	 * @param convertView(View): La vue a modifier
   	 * @param parent(ViewGroup): La vue parente
   	 * @return La vue du groupe d'elements.
   	 **/
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }   
    
    /** Indique si les IDs des enfants et des groupes sont stables en fonction des changements effectues sur leur donnees **/
    @Override
    public boolean hasStableIds() {
        return false;
    }
    
    /**
   	 * Indique si le sous-element est selectionnable.
   	 * @param groupPosition(int): Indice du groupe d'elements
   	 * @param childPosition(int): Indice du sous-element
   	 * @param convertView(View): La vue a modifier
   	 * @param parent(ViewGroup): La vue parente
   	 * @return vrai si le sous-element est selectionnable, faux sinon.
   	 **/
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}