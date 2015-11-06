package com.pictitab.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Category;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.ExpandableTreeAdapter;
import com.pictitab.utils.XMLTools;

public class LexiconAdministrationActivity extends Activity {

	private static AppData data;

	private EditText lexiconName;
	private static TextView selectedCategory;

	private static ImageView lexiconImageView;
	private static Bitmap lexiconBit;
	private Button lexiconFileButton;
	private Button lexiconCameraButton;
	private Button lexiconUrlButton;

	private static String pictureOrigin; // Origin of picture : camera or SD
											// card
	private static String picturePathOnDevice; // Physic location of the picture
												// on the
	// device

	// Dynamic list of category
	private ImageButton previousButton; // Button to go back to parent category
	ExpandableListView lvExpAllCat; // view of the scroll list of all the
									// categories
	static ExpandableTreeAdapter listAdapter; // Adaptater of the list
	static List<String> listDataHeaderAllCat; // Header of the list
	static List<String> topAllCat; // Categories list
	static HashMap<String, List<String>> listDataChildAllCat; // Sub-categories
																// grouped by
																// parent
																// categories
	static int compteur; // Depth of the tree
	static SparseArray<List<Category>> tree; // Categories depth in the tree
	private String categorieName; // Selected category name

	private Button valideButton;
	private Button deleteButton;

	/*
	 * ==========================================================================
	 * ==========================================
	 */
	/* == EVENEMENTS == */
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			LexiconAdministrationActivity.lexiconBit = (Bitmap) data
					.getExtras().get("data");
			LexiconAdministrationActivity.picturePathOnDevice = data
					.getExtras().getString(FileChooser.DATA_Picture);
			LexiconAdministrationActivity.testExtras();
			LexiconAdministrationActivity.lexiconImageView
					.setImageBitmap(lexiconBit);
		}
	}

	@Override
	public void onBackPressed() {
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
	 * Display.
	 **/
	private void toDisplay() {
		setContentView(R.layout.activity_lexicon_administration);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		this.lexiconName = (EditText) findViewById(R.id.lexicon_word);
		LexiconAdministrationActivity.selectedCategory = (TextView) findViewById(R.id.textCatSelect);

		LexiconAdministrationActivity.lexiconImageView = (ImageView) findViewById(R.id.lexiconImageView);
		this.lexiconFileButton = (Button) findViewById(R.id.lexiconFileButton);
		this.lexiconCameraButton = (Button) findViewById(R.id.lexiconCameraButton);
		this.lexiconUrlButton = (Button) findViewById(R.id.lexiconUrlButton);

		this.previousButton = (ImageButton) findViewById(R.id.previousButton1);
		this.lvExpAllCat = (ExpandableListView) findViewById(R.id.expListCat);

		this.valideButton = (Button) findViewById(R.id.lexiconValidateButton);
		this.deleteButton = (Button) findViewById(R.id.lexiconDeleteButton);

		// Set invisible the delete button
		this.deleteButton.setVisibility(View.INVISIBLE);

		prepareListData();

		LexiconAdministrationActivity.listAdapter = new ExpandableTreeAdapter(
				this, 1, listDataHeaderAllCat, listDataChildAllCat);

		this.lvExpAllCat.setAdapter(listAdapter);

		LexiconAdministrationActivity.pictureOrigin = new String("");
		this.categorieName = new String("");

		final String mot = getIntent().getStringExtra(
				SelectLexiconActivity.DATA_LEXICON);

		if (mot != null) {
			setLexiconToDisplay(mot);
		} else {
			createLexiconToDisplay();
		}

		// File explorer button
		this.lexiconFileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LexiconAdministrationActivity.this, FileChooser.class);
				startActivityForResult(intent, 60);
			}
		});

		// Camera button
		this.lexiconCameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 7);
			}
		});

		// Get from url button
		this.lexiconUrlButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Set an EditText view to get user input
				final EditText input = new EditText(
						LexiconAdministrationActivity.this);

				new AlertDialog.Builder(LexiconAdministrationActivity.this)
						.setTitle(R.string.enter_url)
						.setMessage("")
						.setView(input)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String url = input.getText().toString();
										LoadImageFromURL loadImage = new LoadImageFromURL(url);
										loadImage.execute();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Do nothing.
									}
								}).show();
			}
		});
	}

	/**
	 * Prepare the categories list.
	 **/
	private void prepareListData() {

		listDataHeaderAllCat = new ArrayList<String>();
		String selected_element = getResources().getString(
				R.string.selected_element);
		listDataHeaderAllCat.add(selected_element);
		listDataChildAllCat = new HashMap<String, List<String>>();

		compteur = 0;
		tree = new SparseArray<List<Category>>();

		// Get the parent category (without child categories) and put them in
		// root depth (= 0)
		List<Category> categories = data.getNotChildCategories();
		tree.put(compteur, categories);

		// Categories name list
		topAllCat = new ArrayList<String>();
		for (int i = 0; i < categories.size(); i++) {
			topAllCat.add(categories.get(i).getName());
		}

		listDataChildAllCat.put(listDataHeaderAllCat.get(0), topAllCat);

		// Back to parent category button
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (compteur >= 1) {
					List<Category> categories = tree.get(--compteur);
					topAllCat.clear();
					for (int i = 0; i < categories.size(); i++) {
						topAllCat.add(categories.get(i).getName());
					}
					listAdapter.notifyDataSetChanged();
				}
			}
		});

		// Action when on element is selected
		lvExpAllCat.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				categorieName = listDataChildAllCat.get(
						listDataHeaderAllCat.get(groupPosition)).get(
						childPosition);
				String categoryTitle = getResources().getString(
						R.string.category);
				selectedCategory.setText(categoryTitle + ' ' + categorieName);
				listAdapter.notifyDataSetChanged();
				return false;
			}
		});
	}

	/**
	 * Display window for modification manager.
	 * 
	 * @param num
	 *            (int): lexicon entry id in data.
	 **/
	private void setLexiconToDisplay(final String mot) {

		Lexicon lex = data.getWordByName(mot);
		String word = lex.getWord();
		Category c = lex.getCategory();
		String categoryTitle = getResources().getString(R.string.category);
		this.categorieName = c.getName();
		selectedCategory.setText(categoryTitle + ' ' + this.categorieName);

		// Identify the picture origin
		final String picturePath = lex.getPictureSource();
		this.setPictureOrigin(picturePath);

		// Load the picture
		try {
			InputStream inputStream = new FileInputStream(picturePath);
			Bitmap bitMap = BitmapFactory.decodeStream(inputStream);
			lexiconImageView.setImageBitmap(bitMap);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		lexiconName.setText(word);

		// Set visible the delete button
		deleteButton.setVisibility(View.VISIBLE);
		deleteButton.setTextColor(Color.RED);

		// Change validate button text
		valideButton.setText(R.string.update);

		// Action of delete button
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						// Delete entry, update xml file, go back
						case DialogInterface.BUTTON_POSITIVE:
							deleteLexicon(picturePath, mot);
							XMLTools.printLexicon(getApplicationContext(),
									data.getLexicon());
							onBackPressed();
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};
				AlertDialog.Builder ab = new AlertDialog.Builder(
						LexiconAdministrationActivity.this);
				ab.setTitle(R.string.delete)
						.setMessage(R.string.dialog_box_delete)
						.setPositiveButton(R.string.yes, dialogClickListener)
						.setNegativeButton(R.string.no, dialogClickListener)
						.setIcon(android.R.drawable.ic_delete).show();
			}
		});

		// Action of modify button
		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							modifyLexicon(picturePath, mot);
							XMLTools.printLexicon(getApplicationContext(),
									data.getLexicon());
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};
				AlertDialog.Builder ab = new AlertDialog.Builder(
						LexiconAdministrationActivity.this);
				ab.setTitle(R.string.modify)
						.setMessage(R.string.dialog_box_update)
						.setPositiveButton(R.string.yes, dialogClickListener)
						.setNegativeButton(R.string.no, dialogClickListener)
						.setIcon(android.R.drawable.ic_menu_edit).show();
			}
		});
	}

	/**
	 * Define the origin of the picture. The picture can be a camera picture or
	 * be in "Pictures" directory on the device.
	 * 
	 * @param picturePath
	 *            (String) Path.
	 */
	private void setPictureOrigin(String picturePath) {
		if (picturePath.contains(File.separator + "Pictures" + File.separator))
			LexiconAdministrationActivity.pictureOrigin = new String("Picture");
		else
			LexiconAdministrationActivity.pictureOrigin = new String("Camera");
	}

	/**
	 * Delete the lexicon entry and its picture.
	 * 
	 * @param picturePath
	 *            (String): Path.
	 * @param num
	 *            (int): id of the lexicon entry in the data.
	 **/
	protected void deleteLexicon(String picturePath, String mot) {
		data.deleteWord(mot);

		if (LexiconAdministrationActivity.pictureOrigin.equals("Camera")) {
			File picture = new File(picturePath);
			picture.delete();
		}
	}

	/**
	 * Update the lexicon entry and its picture.
	 * 
	 * @param picturePath
	 *            (String): path.
	 * @param num
	 *            (int): word id.
	 **/
	protected void modifyLexicon(String picturePath, String mot) {

		String newName = lexiconName.getText().toString();
		if (newName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(
					LexiconAdministrationActivity.this);
			ab.setTitle(R.string.warning).setMessage(R.string.dialog_box_name)
					.setIcon(android.R.drawable.ic_notification_clear_all)
					.setNeutralButton(R.string.ok, null).show();
		}
		// If the lexicon entry already exists
		else if ((!newName.equals(mot))
				&& (wordIsExist(newName, this.categorieName))) {
			AlertDialog.Builder ab = new AlertDialog.Builder(
					LexiconAdministrationActivity.this);
			ab.setTitle(R.string.warning)
					.setMessage(R.string.dialog_box_already_exist)
					.setIcon(android.R.drawable.ic_notification_clear_all)
					.setNeutralButton(R.string.ok, null).show();
		} else {
			Category newCategory = null;
			if (categorieName.equals("")) {
				newCategory = data.getWordByName(mot).getCategory();
			} else {
				newCategory = data.getCategories().get(
						data.getCategoryByName(categorieName));
			}

			deleteLexicon(picturePath, mot);

			lexiconImageView.buildDrawingCache();
			File fileName = new File(picturePath);
			FileOutputStream out;
			try {
				out = new FileOutputStream(fileName);
				Bitmap bitMap = LexiconAdministrationActivity.lexiconImageView
						.getDrawingCache();
				// Resize the picture
				bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);

				lexiconBit = bitMap;
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			data.addLexicon(new Lexicon(newName, picturePath, newCategory));
			onBackPressed();
		}
	}

	/**
	 * Create the lexicon entry in set the word and the picture.
	 * 
	 * @param picturePath
	 *            (String): path
	 **/
	protected void addLexicon(String picturePath) {
		String name = this.lexiconName.getText().toString();
		LexiconAdministrationActivity.lexiconImageView.buildDrawingCache();
		// Check
		if (name.equals("") || picturePath.equals("")
				|| this.categorieName.equals("")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(
					LexiconAdministrationActivity.this);
			ab.setTitle(R.string.warning)
					.setMessage(R.string.dialog_box_incorrect_field)
					.setIcon(android.R.drawable.ic_notification_clear_all)
					.setNeutralButton(R.string.ok, null).show();
		}
		// If the entry already exists
		else if (wordIsExist(name, this.categorieName)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(
					LexiconAdministrationActivity.this);
			ab.setTitle(R.string.warning)
					.setMessage(R.string.dialog_box_already_exist)
					.setIcon(android.R.drawable.ic_notification_clear_all)
					.setNeutralButton(R.string.ok, null).show();
		}
		// Load the picture
		else {
			loadPicture(picturePath, name);
		}
	}

	/**
	 * Load the picture according to its origin ("Picture" or "Camera") and add
	 * it in the Data object.
	 * 
	 * @param picturePath
	 *            (String): path
	 **/
	private void loadPicture(String picturePath, String name) {

		if (!LexiconAdministrationActivity.pictureOrigin.equals("Pictures")) {
			picturePath += name + ".png";
			File fileName = new File(picturePath);
			FileOutputStream out;

			if (lexiconBit != null) {
				try {
					out = new FileOutputStream(fileName);
					// Resize
					lexiconBit.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else {
				try {
					out = new FileOutputStream(fileName);
					Bitmap bitMap = LexiconAdministrationActivity.lexiconImageView
							.getDrawingCache();
					// Resize
					bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);

					lexiconBit = bitMap;
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Add
		int numCat = data.getCategoryByName(this.categorieName);
		data.addLexicon(new Lexicon(name, picturePath,
				LexiconAdministrationActivity.data.getCategories().get(numCat)));

		// save and back
		XMLTools.printLexicon(getApplicationContext(), data.getLexicon());
		onBackPressed();
	}

	/**
	 * Display a lexicon entry creation window.
	 **/
	private void createLexiconToDisplay() {

		valideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String picturePath;
				if (pictureOrigin.equals("Pictures"))
					picturePath = picturePathOnDevice;
				else if (pictureOrigin.equals("Camera")) {
					String fp = File.separator;
					String path = fp + "Android" + fp + "data" + fp
							+ getApplicationContext().getPackageName() + fp
							+ "image" + fp;
					picturePath = Environment.getExternalStorageDirectory()
							+ path;
				} else
					picturePath = new String("");

				addLexicon(picturePath);
			}
		});
	}

	/**
	 * Test the values of the child activity.
	 * 
	 * @throws MalformedURLException
	 */
	private static void testExtras() {
		if (LexiconAdministrationActivity.lexiconBit != null) {
			lexiconImageView
					.setImageBitmap(LexiconAdministrationActivity.lexiconBit);
			LexiconAdministrationActivity.pictureOrigin = new String("Camera");
		}
		if (LexiconAdministrationActivity.picturePathOnDevice != null
				&& !LexiconAdministrationActivity.picturePathOnDevice
						.equals("")) {
			try {
				LexiconAdministrationActivity.lexiconBit = BitmapFactory
						.decodeStream(new FileInputStream(
								LexiconAdministrationActivity.picturePathOnDevice));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			LexiconAdministrationActivity.pictureOrigin = new String("Pictures");
		}
	}

	/**
	 * Redraw the list with the child categories (sub-categories) of the
	 * selected category
	 * 
	 * @param groupPosition
	 *            (int): column id
	 * @param childPosition
	 *            (int): category id in the list
	 **/
	public static void nextTree(int groupPosition, final int childPosition) {
		String subCategoryName = listDataChildAllCat.get(
				listDataHeaderAllCat.get(groupPosition)).get(childPosition);
		Category c = data.getCategories().get(
				data.getCategoryByName(subCategoryName));

		List<Category> categories = c.getCategories();
		int size = categories.size();
		if (size > 0) {
			topAllCat.clear();

			for (int i = 0; i < size; i++) {
				topAllCat.add(categories.get(i).getName());
			}

			tree.put(++compteur, categories);
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Return if a lexicon entry already exists or not.
	 * 
	 * @param word
	 *            (String): word of the lexicon entry
	 * @param category
	 *            (String): category of the lexicon entry
	 * @return (boolean) : true if it is the case, else false
	 **/
	public static boolean wordIsExist(String word, String category) {
		Lexicon l = data.getWordByName(word);

		if (l != null) {
			String c = l.getCategory().getName();
			if (c.equals(category)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a Bitmap from a String (URL link) with asynchronous task
	 */
	public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
		
		private String url;

		public LoadImageFromURL(String url) {
			this.url = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(this.url);
				InputStream is = url.openConnection().getInputStream();
				Bitmap bitMap = BitmapFactory.decodeStream(is);
				return bitMap;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			LexiconAdministrationActivity.lexiconBit = result;
			LexiconAdministrationActivity.picturePathOnDevice = null;
			LexiconAdministrationActivity.testExtras();
			LexiconAdministrationActivity.lexiconImageView.setImageBitmap(lexiconBit);
			
		}
	}
}
