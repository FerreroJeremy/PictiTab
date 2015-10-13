package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pictitab.data.AppData;
import com.pictitab.data.Child;
import com.pictitab.data.Grammar;
import com.pictitab.ui.R;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ChildAdministrationActivity extends Activity {
	
	private static AppData data;
	
	private EditText family_name;	// last name
	private EditText first_name;	// first name
	private DatePicker birth_date;	// Birth date
	private Bitmap bitMapChild;		// Picture
	private ImageButton imgButton;	// Picture button
	private ImageButton logButton;	// Child history button (logs)
	
	private Button valideButton;	// Suppression button
	private Button deleteButton;	// Add button
	
	// Avalaible grammars list
	ExpandableTreeAdapter listAdapterAllGram;				// Adaptater
	ExpandableListView lvExpAllGram;						// Header
	List<String> listDataHeaderAllGram;						// List of avalaible grammars
	HashMap<String, List<String>> listDataChildAllGram;		// Map
	
	// Selected grammars list
	ExpandableTreeAdapter listAdapterChildGram;				// Adaptater
    ExpandableListView lvExpChildGram;						// Header
    List<String> listDataHeaderChildGram;					// List
    HashMap<String, List<String>> listDataChildChildGram;	// Map
    
    List<String> topChildGram;		// Selected grammars
	
	/*====================================================================================================================*/
	/*==													EVENEMENTS													==*/
	/*====================================================================================================================*/
	// Overrided functions to manage the AppData !
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load children data
		data =(AppData)getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY).getParcelable(MainActivity.DATA_KEY);
		
		this.toDisplay();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        // Change the picture
		if(resultCode==RESULT_OK) {
			// Get the camera picture.
			this.bitMapChild = (Bitmap) data.getExtras().get("data");
			if(this.bitMapChild!=null) {
				imgButton.setImageBitmap(this.bitMapChild);
			}
            // Set the picture on the picture's button
			this.imgButton.setImageBitmap(bitMapChild);
		}
	}
	
	@Override  
	public void onBackPressed() {
		setResult(RESULT_OK, this.getIntent());
		finish();
	}
	
	/*====================================================================================================================*/
	/*==                                                        PROCESS													==*/
	/*====================================================================================================================*/
	
	/** 
     * Display the administration window.
     **/
	private void toDisplay() {
		setContentView(R.layout.activity_child_administration);
		
		// Set the orientation in portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Graphical elements
		family_name = (EditText) findViewById(R.id.category_name);
		first_name = (EditText) findViewById(R.id.first_name);
		birth_date = (DatePicker) findViewById(R.id.birth_date);
		imgButton = (ImageButton) findViewById(R.id.picImg);
		logButton = (ImageButton) findViewById(R.id.imageButtonClear);
		
		valideButton = (Button) findViewById(R.id.valider);
		deleteButton = (Button) findViewById(R.id.supprimer);
		
		// Set invisible the delete button
		deleteButton.setVisibility(View.INVISIBLE);
		
		// Get the child name
		final String nom = getIntent().getStringExtra("nom");
		final String prenom = getIntent().getStringExtra("prenom");
		
		lvExpAllGram = (ExpandableListView) findViewById(R.id.expandableListView1);
		lvExpChildGram = (ExpandableListView) findViewById(R.id.expandableListView2);

        topChildGram = new ArrayList<String>();
        
        prepareListData(nom, prenom);
        
        listAdapterAllGram = new ExpandableTreeAdapter(this, 5, listDataHeaderAllGram, listDataChildAllGram);
        listAdapterChildGram = new ExpandableTreeAdapter(this, 5, listDataHeaderChildGram, listDataChildChildGram);
        lvExpAllGram.setAdapter(listAdapterAllGram);
        lvExpChildGram.setAdapter(listAdapterChildGram);
		
		// Modification
		if(nom != null) {
			setChildToDisplay(nom);
		}
		// Creation
		else {
			createChildToDisplay(nom, prenom);
			logButton.setVisibility(View.INVISIBLE);
		}
		
		// Action of the picture's button
		imgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to the camera
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				// Wait the camera result
				startActivityForResult(intent, 3);
			}
		});
		
		// Historic button
		logButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ChildAdministrationActivity.this, ChildLogActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				i.putExtra("nom", nom);
				i.putExtra("prenom", prenom);
				startActivity(i);
			}
		});
}
	
	 /**
	 * Display the window for the modification.
	 * @param nom(String): Child name in data.
	 **/
	private void setChildToDisplay(final String nom) {
		// Get the child information
		final String prenom = getIntent().getStringExtra("prenom");
		final String photo = getIntent().getStringExtra("photo");
		int an = getIntent().getIntExtra("an",0);
		int mois = getIntent().getIntExtra("mois",0) - 1;
		int jour = getIntent().getIntExtra("jour",0);
		
		// Initialize the info in the window
		family_name.setText(nom);
		first_name.setText(prenom);
		birth_date.updateDate(an, mois, jour);
		
		// Get the picture path
		String fp   = File.separator;
		String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "photo" + fp + photo;
		final String picturePath = Environment.getExternalStorageDirectory() + path;
		
		// Load the picture
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			imgButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// Set visible the delete button
		deleteButton.setVisibility(View.VISIBLE);
		deleteButton.setTextColor(Color.RED);
		
		// Set the text of the valide button from "Valider" (add mode) to "Modifier" (update mode)
		valideButton.setText("Modifier");
		
		// Action of the delete button
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        // Delete the profile, update the xml file and go back
				        case DialogInterface.BUTTON_POSITIVE:
				        	deleteChild(picturePath, nom, prenom);
				        	XMLTools.printChildren(getApplicationContext(), data.getProfils());
				            onBackPressed();
				        // Do nothing
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
				
				// Alert dialog box
				AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				ab.setTitle("Suppression").setMessage("Voulez-vous vraiment supprimer ce profil ?")
										  .setPositiveButton("Oui", dialogClickListener)
										  .setNegativeButton("Non", dialogClickListener)
										  .setIcon(android.R.drawable.ic_delete).show();
			}
		});
		
		// Modification
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        // Update child, data and xml file and go back
				        case DialogInterface.BUTTON_POSITIVE:
				    		if (nom.equals("") || prenom.equals("")) {
				    			AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				    			ab.setTitle("Avertissement").setMessage("Veuillez remplir tous les champs correctement.")
				    										.setIcon(android.R.drawable.ic_notification_clear_all)
				    										.setNeutralButton("Ok", null).show();
				    		}
				    		else {
				    			deleteChild(picturePath, nom, prenom);
					        	addChild();
					        	createLog(nom, prenom);
				    		}
				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};
                
				AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
				ab.setTitle("Modification").setMessage("Voulez-vous vraiment modifier ce profil ?")
										   .setPositiveButton("Oui", dialogClickListener)
										   .setNegativeButton("Non", dialogClickListener)
										   .setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}
	
	/**
	 * Delete a profile and its picture.
	 * @param path(String): Picture path.
	 * @param nom(String): Child last name.
	 * @param prenom(String): Child first name.
	 **/
	protected void deleteChild(String path, String nom, String prenom){
		data.deleteProfil(nom, prenom);
    	File photo = new File(path);
    	photo.delete();
	}
	
	/** 
	 * Create a child profile.
	 **/
	protected void addChild(){
		String familyName = family_name.getText().toString();
		String firstName = first_name.getText().toString();
		imgButton.buildDrawingCache();
		Bitmap bitMap = imgButton.getDrawingCache();
		
		if (familyName.equals("") || firstName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(ChildAdministrationActivity.this);
			ab.setTitle("Avertissement").setMessage("Veuillez remplir tous les champs correctement.")
										.setIcon(android.R.drawable.ic_notification_clear_all)
										.setNeutralButton("Ok", null).show();
		} else {
			int birthDay = birth_date.getDayOfMonth();
			int birthMonth = birth_date.getMonth() + 1;
			int birthYear = birth_date.getYear();
			
			// Get the grammars list of the child
			ArrayList <Grammar> grammars = new ArrayList<Grammar>();
			for(int i=0; i < topChildGram.size(); i++) {
				grammars.add(data.getGrammarByName(topChildGram.get(i)));
			}

			String fp   = File.separator;
			String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "photo";
			String picturePath = Environment.getExternalStorageDirectory() + path;
			String pictureName = new String(familyName + "_" + firstName + ".png");
			
			FileOutputStream out;
			File fileName = new File(picturePath, pictureName);
			
			if(bitMapChild!=null) {
				try {
					out = new FileOutputStream(fileName);
					bitMapChild.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					out = new FileOutputStream(fileName);
					bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// Add the new profile
			data.addProfil(new Child(familyName, firstName, birthDay, birthMonth, birthYear, pictureName, grammars));
			
			XMLTools.printChildren(getApplicationContext(), data.getProfils());
			onBackPressed();
		}
	}
	
	/** 
	 * Display the window for the creation.
	 **/
	protected void createChildToDisplay(final String nom, final String prenom) {
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// add child in data, update xml file and go back
				addChild();
				createLog(nom, prenom);
			}
		});
	}
	
	/**
	* Create or rename a history log file of a child.
	* @param nom(String): Child last name.
	* @param prenom(String): Child first name.
	**/
	protected void createLog(String nom, String prenom) {
		String familyName = family_name.getText().toString();
		String firstName = first_name.getText().toString();
		
		String fp   = File.separator;
		String path = fp + "Android" + fp + "data" + fp + getApplicationContext().getPackageName() + fp + "log" + fp;
		String filePath = Environment.getExternalStorageDirectory() + path;
		File newFileLog = new File(filePath + familyName + "_" + firstName + ".xml");
		File oldFileLog = new File(filePath + nom + "_" + prenom + ".xml");
        
		if (oldFileLog.exists()) {
			oldFileLog.renameTo(newFileLog);
		} else {
			XMLTools.createEmptyXML(filePath + familyName + "_" + firstName + ".xml", "logs");
		}
	}
	
	/**
	* Initialize the grammars list.
    * @param nom(String): Child last name.
    * @param prenom(String): Child first name.
	**/
    private void prepareListData(String nom, String prenom) {
    	List<Grammar> grammars = data.getGrammars();
    	
        listDataHeaderAllGram = new ArrayList<String>();
        listDataChildAllGram = new HashMap<String, List<String>>();
        listDataHeaderAllGram.add("Toutes les grammaires...");
        List<String> topAllCat = new ArrayList<String>();

        for (int i =0; i < grammars.size(); i++) {
        	String name = grammars.get(i).getName();
        	topAllCat.add(name);
        	if((nom!=null) && (nom.equals(name))) {
        		topAllCat.remove(name);
        	}
        }
        listDataChildAllGram.put(listDataHeaderAllGram.get(0), topAllCat);
        
        listDataHeaderChildGram = new ArrayList<String>();
        listDataChildChildGram = new HashMap<String, List<String>>();
        listDataHeaderChildGram.add("Grammaires de l'enfant...");
        
        if(nom != null) {
        	ArrayList<Grammar> grammarsOfChild = data.getChildByName(nom, prenom).getGrammars();
        	if(grammarsOfChild != null) {
                for (int i =0; i < grammarsOfChild.size(); i++) {
                	if(grammarsOfChild.get(i) != null) {
                		topChildGram.add(grammarsOfChild.get(i).getName());
                	}
                }
                listDataChildChildGram.put(listDataHeaderChildGram.get(0), topChildGram);
        	}
        } else {
            listDataChildChildGram.put(listDataHeaderChildGram.get(0), topChildGram);
        }
        
        lvExpAllGram.setOnChildClickListener(new OnChildClickListener() {
        	
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String grammarName = listDataChildAllGram.get(listDataHeaderAllGram.get(groupPosition)).get(childPosition);
                if(!topChildGram.contains(grammarName)) {
                	topChildGram.add(grammarName);
                }
                listAdapterChildGram.notifyDataSetChanged();

                return false;
            }
        });
        
        lvExpChildGram.setOnChildClickListener(new OnChildClickListener() {
        	
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String grammarName = listDataChildChildGram.get(listDataHeaderChildGram.get(groupPosition)).get(childPosition);
                topChildGram.remove(grammarName);
                listAdapterChildGram.notifyDataSetChanged();

                return false;
            }
        });
    }
}
