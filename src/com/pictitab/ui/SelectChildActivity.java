package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Child;
import com.pictitab.utils.UITools;

public class SelectChildActivity extends Activity {

	private AppData data;

	private LinearLayout layout;
	private Button addChild;
	private GridLayout gridChildrenLayout;
	private ArrayList<GridLayout> childrenButtons;
	private ScrollView listChildren;

	private int pictureWidth = 80; // Width of the picture button
	private int pictureHeight = 130; // Height of the picture button
	private int padding = 5; // MPadding between the picture buttons

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */
	// Common part (crate and redraw function + save when go back button pressed
	// + waiting intent result function)
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
		Intent intent = this.getIntent();
		if (intent != null) {
			String mode = intent.getStringExtra(MainActivity.TYPE_SOURCE);
			createChildActivity(mode);
		}
	}

	/**
	 * Draw the window according to the utilization mode (where do we come from
	 * ?).
	 * 
	 * @param mode
	 *            (String): Utilization mode (child or educator)
	 **/
	private void createChildActivity(String mode) {
		// If it is the educator mode then add a child creation button
		if (mode.equals("educator_admin")) {
			createAddButton();
		}

		listChildren = new ScrollView(this);
		gridChildrenLayout = new GridLayout(this);

		// Define the column number in the grid layout
		Display display = getWindowManager().getDefaultDisplay();
		int orientation = getResources().getConfiguration().orientation;
		int nbCols = UITools.getNbColumn(display, orientation, pictureWidth,
				padding);
		Point size = new Point();
		display.getSize(size);

		gridChildrenLayout.setColumnCount(nbCols);

		// Children list
		createChildrenGridToDisplay(mode);

		// Add views in the layout
		listChildren.addView(gridChildrenLayout);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		if (mode.equals("educator_admin")) {
			layout.addView(addChild);
		}
		layout.addView(listChildren);

		// Add the layout in the final view
		setContentView(layout);
	}

	/**
	 * Draw the creation child profile button.
	 **/
	private void createAddButton() {
		// Dynamic creation
		addChild = new Button(this);
		addChild.setHeight(100);
		addChild.setText(R.string.add);

		// Action of the button
		addChild.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectChildActivity.this,
						ChildAdministrationActivity.class);

				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 2);
			}
		});
	}

	/**
	 * Draw the grid of the children profiles.
	 * 
	 * @param mode
	 *            (String): Utilization mode (child or educator)
	 **/
	private void createChildrenGridToDisplay(final String mode) {
		// Build the list
		final List<Child> profils = data.getProfils();
		childrenButtons = new ArrayList<GridLayout>();
		for (int i = 0; i < profils.size(); i++) {
			// Get the information for each child.
			final String nom = profils.get(i).getName();
			final String prenom = profils.get(i).getFirstname();
			final String photo = profils.get(i).getPhoto();
			final int an = profils.get(i).getBirthyear();
			final int mois = profils.get(i).getBirthmonth();
			final int jour = profils.get(i).getBirthday();
			final int num = i;

			// Get the picture.
			ImageButton tmpButton = loadPicture(photo);

			// Add Text
			TextView tmpName = new TextView(this);
			tmpName.setText(prenom + " " + nom);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER;
			tmpName.setLayoutParams(params);

			GridLayout tmpLayout = createChild(tmpButton, tmpName);

			// Add the button in the layout
			childrenButtons.add(tmpLayout);
			gridChildrenLayout.addView(tmpLayout);

			// Action of the picture button
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = null;
					if (mode.equals("educator_admin")) {
						intent = new Intent(SelectChildActivity.this,
								ChildAdministrationActivity.class);
					} else if (mode.equals("main_activity")) {
						intent = new Intent(SelectChildActivity.this,
								ChildCommunicationActivity.class);
					}

					Bundle b = new Bundle();
					b.putParcelable(MainActivity.DATA_KEY, data);
					if (mode.equals("main_activity")) {
						b.putInt(ChildCommunicationActivity.SELECTED_CHILD, num);
					}

					intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
					intent.putExtra("nom", nom);
					intent.putExtra("prenom", prenom);
					intent.putExtra("photo", photo);

					if (mode.equals("educator_admin")) {
						intent.putExtra("an", an);
						intent.putExtra("mois", mois);
						intent.putExtra("jour", jour);
						startActivityForResult(intent, 2);
					} else {
						startActivity(intent);
					}

				}
			});
		}
	}

	/**
	 * Load the picture of a profile and resize it.
	 * 
	 * @param pictureName
	 *            (String): Picture's name
	 **/
	private ImageButton loadPicture(String pictureName) {
		// Get the path
		ImageButton tmpButton = new ImageButton(this);
		String fp = File.separator;
		String path = fp + "Android" + fp + "data" + fp
				+ getApplicationContext().getPackageName() + fp + "photo" + fp
				+ pictureName;

		// OLoad the picture
		String picturePath = Environment.getExternalStorageDirectory() + path;
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			// Resize
			bitmap = Bitmap.createScaledBitmap(bitmap, pictureWidth,
					pictureHeight, true);
			tmpButton.setImageBitmap(bitmap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return tmpButton;
	}

	/**
	 * Draw the picture button in the grid.
	 * 
	 * @param button
	 *            (ImageButton): Profile associated button
	 * @param name
	 *            (TextView): first name + last name
	 **/
	private GridLayout createChild(ImageButton button, TextView name) {
		GridLayout childLayout = new GridLayout(this);
		childLayout.setColumnCount(1);
		childLayout.setPadding(padding, padding, padding, padding);

		// Add the button and the Text to the view
		childLayout.addView(button);
		childLayout.addView(name);
		return childLayout;
	}
}
