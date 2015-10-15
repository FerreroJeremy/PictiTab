package com.pictitab.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.utils.UITools;

public class SelectPictureActivity extends Activity {

	public static final String DATA_Picture = "Picture_path";

	private LinearLayout layout;
	private ScrollView listPictureLayout;
	private GridLayout gridPictureLayout;
	private ArrayList<GridLayout> pictureButtons;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS - normal stuff == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.toDisplay();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.toDisplay();
	}

	@Override
	public void onBackPressed() {
		getIntent().putExtra(SelectPictureActivity.DATA_Picture, "");
		setResult(RESULT_CANCELED, this.getIntent());
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
	 * Display the window.
	 **/
	private void toDisplay() {
		listPictureLayout = new ScrollView(this);
		gridPictureLayout = new GridLayout(this);

		// The picture are 80x80p dimension picture, so, to get the column
		// number, do screen size / 80
		int nbCol = UITools.getNbColumn(getWindowManager().getDefaultDisplay(),
				getResources().getConfiguration().orientation, 90, 5);
		gridPictureLayout.setColumnCount(nbCol);
		pictureButtons = new ArrayList<GridLayout>();

		// Get the pictures and their names.
		String folderPath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "Pictures"; // <-- CHANGE HERE THE PICTURES
												// PATH IF NECESSARY
		List<Bitmap> pictures = this.getPictures(folderPath);
		List<String> picturesPath = this.getPicturesName(folderPath);

		for (int i = 0; i < picturesPath.size(); i++) {

			ImageButton tmpButton = new ImageButton(this);
			GridLayout tmpLayout = new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);
			TextView tmpName = new TextView(this);
			tmpName.setGravity(Gravity.CENTER);

			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(pictures.get(i),
					80, 80, false));
			tmpName.setText(picturesPath.get(i));
			// Add button into the view
			tmpLayout.addView(tmpButton);
			tmpLayout.addView(tmpName);
			// Add this view in a array
			pictureButtons.add(tmpLayout);
			// Add this view into the gridlayout
			gridPictureLayout.addView(tmpLayout);

			final String selectedPicture = new String(folderPath
					+ File.separator + picturesPath.get(i));

			// Action of the picture button
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getIntent().putExtra(SelectPictureActivity.DATA_Picture,
							selectedPicture);
					setResult(RESULT_OK, getIntent());
					finish();
				}
			});
		}

		// Add views in the layout
		layout = new LinearLayout(this);
		listPictureLayout.addView(gridPictureLayout);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(listPictureLayout);

		// Add final layout in the window
		setContentView(layout);
	}

	/**
	 * Return the name of pictures contained in a directory.
	 * 
	 * @param folderPath
	 *            (String): Path.
	 * @return Pictures names in a array.
	 */
	private List<String> getPicturesName(String folderPath) {
		List<String> paths = new ArrayList<String>();
		File dir = new File(folderPath);
		if (dir.listFiles() == null) {
			return paths;
		} else {
			File childfile[] = dir.listFiles();
			for (int i = 0; i < childfile.length; i++) {
				if ((childfile[i].getPath().endsWith(".png"))
						|| (childfile[i].getPath().endsWith(".jpg")))
					paths.add(childfile[i].getName());
			}
		}
		return paths;
	}

	/**
	 * Return pictures contained in a directory.
	 * 
	 * @param folderPath
	 *            (String): Path.
	 * @return Bitmap in a array.
	 */
	private List<Bitmap> getPictures(String folderPath) {
		List<Bitmap> pics = new ArrayList<Bitmap>();
		File dir = new File(folderPath);

		if (dir.listFiles() == null) {
			return pics;
		} else {
			File childfile[] = dir.listFiles();
			for (int i = 0; i < childfile.length; i++) {
				if ((childfile[i].getPath().endsWith(".png"))
						|| (childfile[i].getPath().endsWith(".jpg"))) {
					Bitmap bit = BitmapFactory.decodeFile(childfile[i]
							.getPath());
					pics.add(bit);
				}
			}
		}
		return pics;
	}
}
