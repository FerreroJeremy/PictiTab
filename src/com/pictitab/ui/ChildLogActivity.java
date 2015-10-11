package com.pictitab.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Entry;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.XMLTools;

public class ChildLogActivity extends Activity {
	
	// Les donnees de l'application
	private AppData data;
	
	private List<Entry> logs;				// Les logs produits par l'enfant
	
	private TextView titre;					// Titre de la page des logs
	private LinearLayout generalLayout;		// La layout principale de la fenetre
	

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
	
	/** Mise en place de la fenêtre d'affichage des logs de l'enfant. **/
	private void toDisplay() {
		setContentView(R.layout.activity_child_log);
		
		// On recupere l'intent parent
		Intent intent = this.getIntent();
		// Si l'intention recuperee n'est pas nulle, on remplit la zone de selection suivant le mode d'utilisation
		if (intent != null) {
			// On recupere le nom et prenom de l'enfant
			final String nom = getIntent().getStringExtra("nom");
			final String prenom = getIntent().getStringExtra("prenom");
			generalLayout = (LinearLayout) findViewById(R.id.generalLayout);
			
			// On remplit le titre de la fenetre
			titre = (TextView) findViewById(R.id.textView1);
			titre.setText("Historique de l'enfant " + prenom);
			
			// On recupere les logs de l'enfant
			logs = XMLTools.loadLogs(this, nom, prenom, data);
			
			// Si ses logs existent
			if(logs != null) {
				for(int i =0; i < logs.size(); i++) {
					
					// On creait une scrollVue horizontale
					HorizontalScrollView logScrollView = new HorizontalScrollView(this);
					
					// On y rajoute une zone de texte pour la date et l'emplacement pour contenir la sequence de mots
					LinearLayout logLayout =new LinearLayout(this);
					logLayout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout dateLayout =new LinearLayout(this);
					dateLayout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout sequenceLayout =new LinearLayout(this);
					
					// On recupere la date du log en position i
					String log = "     " + logs.get(i).getDate();
					TextView text = new TextView(this);
					text.setText(log);
					text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
					dateLayout.addView(text);
					
					// On recupere la sequence
					List<Lexicon> sequence = logs.get(i).getSequence();
					
					// Si la sequence n'est pas vide
					if(sequence.size() > 0) {
						for(int j=0; j < sequence.size(); j++) {
							// On cree l'image du mot d'indice j
							ImageView tmpButton =new ImageView(this);
							GridLayout tmpLayout =new GridLayout(this);
							tmpLayout.setColumnCount(1);
							tmpLayout.setPadding(5, 5, 5, 5);
							TextView tmpName =new TextView(this);
							tmpName.setGravity(Gravity.CENTER);
							
							if(sequence.get(j) != null) {
								// On recupere l'image.
								Bitmap bit =BitmapFactory.decodeFile(sequence.get(j).getPictureSource());
								tmpButton.setImageBitmap(Bitmap.createScaledBitmap(bit, 80, 80, false));
								// On donne la bonne valeur au TextView
								tmpName.setText(sequence.get(j).getWord());
								
							}
							else {
								TextView tmp = new TextView(this);
								tmp.setText("mot n'existant plus...");
								dateLayout.addView(tmp);
							}
							
							// On ajoute le bouton-image a la GridView.
							tmpLayout.addView(tmpButton);
							tmpLayout.addView(tmpName);

							// Ajout de la sequence dans le layout de la sequence.
							sequenceLayout.addView(tmpLayout);
						}
					} else {
						TextView tmp = new TextView(this);
						tmp.setText("session vide...");
						dateLayout.addView(tmp);
					}
					
					logLayout.addView(dateLayout);
					logLayout.addView(sequenceLayout);
					logScrollView.addView(logLayout);
					// On ajoute notre scrollVue horizontale a notre scrollVue generale verticale
					this.generalLayout.addView(logScrollView);
				}
			}
		}
	}

}
