package com.pictitab.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.UITools;

public class SelectLexiconActivity extends Activity {

	public static final String DATA_LEXICON = "num_lexicon";

	private AppData data;

	private LinearLayout layout;
	private Button addLexicon;
	private GridLayout gridLexiconLayout;
	private ArrayList<GridLayout> lexiconButtons;
	private ScrollView listLexicon;

	private int buttonWidth = 90;
	private int padding = 5;

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
		setContentView(R.layout.activity_select_lexicon);

		// portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		createAddButton();

		listLexicon = new ScrollView(this);
		gridLexiconLayout = new GridLayout(this);

		Display display = getWindowManager().getDefaultDisplay();
		int orientation = getResources().getConfiguration().orientation;
		int nbCols = UITools.getNbColumn(display, orientation, buttonWidth,
				padding);

		gridLexiconLayout.setColumnCount(nbCols);

		createProfilsGridToDisplay();

		listLexicon.addView(gridLexiconLayout);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(addLexicon);
		layout.addView(listLexicon);

		setContentView(layout);
	}

	/**
	 * Redraw the window for the creation template.
	 **/
	private void createProfilsGridToDisplay() {

		List<Lexicon> lexicons = data.getLexicon();
		lexiconButtons = new ArrayList<GridLayout>();
		for (int i = 0; i < lexicons.size(); i++) {

			final String mot = lexicons.get(i).getWord();
			final String src = lexicons.get(i).getPictureSource();

			// Get the picture.
			ImageButton tmpButton = loadPicture(src);

			// Set the text.
			TextView tmpName = new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(mot);

			GridLayout tmpLayout = createLexicon(tmpButton, tmpName);

			lexiconButtons.add(tmpLayout);

			gridLexiconLayout.addView(tmpLayout);

			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SelectLexiconActivity.this,
							LexiconAdministrationActivity.class);
					Bundle b = new Bundle();
					b.putParcelable(MainActivity.DATA_KEY, data);
					intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
					intent.putExtra(SelectLexiconActivity.DATA_LEXICON, mot);
					startActivityForResult(intent, 6);
				}
			});
		}
	}

	/**
	 * Draw the creation button in the window.
	 **/
	private void createAddButton() {
		// Dynamic stuff
		addLexicon = new Button(this);
		addLexicon.setHeight(100);
		addLexicon.setText("Ajouter une entree");

		// Action of the button
		addLexicon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectLexiconActivity.this,
						LexiconAdministrationActivity.class);

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 5);
			}
		});
	}

	/**
	 * Load a picture of a lexicon entry.
	 * 
	 * @param pictureName
	 *            (String): name of the lexicon entry
	 **/
	private ImageButton loadPicture(String pictureName) {
		ImageButton tmpButton = new ImageButton(this);
		try {
			InputStream inputStream = new FileInputStream(pictureName);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
			tmpButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return tmpButton;
	}

	/**
	 * Set a picture button in the grid.
	 * 
	 * @param button
	 *            (ImageButton): button
	 **/
	private GridLayout createLexicon(ImageButton button, TextView name) {
		GridLayout lexiconLayout = new GridLayout(this);
		lexiconLayout.setColumnCount(1);
		lexiconLayout.setPadding(padding, padding, padding, padding);

		lexiconLayout.addView(button);
		lexiconLayout.addView(name);
		return lexiconLayout;
	}
}
