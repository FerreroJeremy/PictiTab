package com.pictitab.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pictitab.data.AppData;
import com.pictitab.data.Automate;
import com.pictitab.data.Child;
import com.pictitab.data.Entry;
import com.pictitab.data.Grammar;
import com.pictitab.data.Lexicon;
import com.pictitab.utils.UITools;
import com.pictitab.utils.XMLTools;

public class ChildCommunicationActivity extends Activity {

	public static final String SELECTED_CHILD = "selected_child";
	static DateFormat dateFormat = DateFormat.getDateTimeInstance(
			DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

	private AppData data;

	// main frame of the window
	private LinearLayout mainLayout; // Main layout
	private LinearLayout topLayout; // Layout of the buttons
	private LinearLayout sentenceLayout; // Layout of the sentence
	private ScrollView botLayout; // ScrollView of the available words

	// Buttons
	private ImageButton stopSentenceButton; // Stop (go back)
	private ImageButton clearSentenceButton; // Clear (erase the current
												// sentence)

	// Current sentence
	private HorizontalScrollView sentenceScrollView; // Horizontal scrollView of
														// the sentence layout
	private List<GridLayout> wordsSentenceButtons; // Grid representing the
													// sentence

	// Keyboard
	private GridLayout listWordLayout; // Available words
	private ArrayList<GridLayout> wordsToSelectButtons; // Grid representing the
														// Keyboard

	private Child profil; // Child profile
	private ArrayList<Automate> automates; // Grammar automate of child
	private ArrayList<Lexicon> wordsToSelect; // Available words for the child
												// according to the automate
	private ArrayList<Lexicon> wordsFromSentence; // Words already entered by
													// the child
	private List<Entry> logs; // Historic of the child

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

		// Get the data
		this.data = (AppData) getIntent().getBundleExtra(
				MainActivity.DATAEXTRA_KEY)
				.getParcelable(MainActivity.DATA_KEY);
		int num = (int) getIntent().getBundleExtra(MainActivity.DATAEXTRA_KEY)
				.getInt(SELECTED_CHILD, -1);
		if (num == -1) {
			AlertDialog.Builder ab = new AlertDialog.Builder(
					ChildCommunicationActivity.this);
			ab.setTitle(R.string.warning)
					.setMessage(R.string.no_selected_element)
					.setIcon(android.R.drawable.ic_notification_clear_all)
					.setNeutralButton(R.string.ok, null).show();
			finish();
		}
		// Get the child profile and its historic
		this.profil = data.getProfils().get(num);
		logs = XMLTools.loadLogs(this, this.profil.getName(),
				this.profil.getFirstname(), data);

		this.initialize();
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
		// When activity close or go back, save the logs
		super.onBackPressed();

		Date actuelle = new Date();
		String date = dateFormat.format(actuelle);

		ArrayList<Lexicon> copy = new ArrayList<Lexicon>(wordsFromSentence);
		logs.add(new Entry(date, copy));

		XMLTools.printLogs(getApplicationContext(), logs,
				this.profil.getName(), this.profil.getFirstname());
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
	 * Initialize the keyboard
	 */
	private void initialize() {
		this.wordsFromSentence = new ArrayList<Lexicon>();
		this.wordsToSelect = new ArrayList<Lexicon>();
		this.automates = new ArrayList<Automate>();

		Grammar tmpGram;
		for (int i = 0; i < this.profil.getGrammars().size(); i++) {
			tmpGram = this.profil.getGrammars().get(i);
			if (tmpGram != null) {
				this.automates.add(new Automate(this.data
						.getGrammarByName(tmpGram.getName()), this.data
						.getLexicon(), this.data));
			}
		}
		this.setListWords();
	}

	/**
	 * Display the window.
	 **/
	private void toDisplay() {
		setContentView(R.layout.activity_child_communication);

		getActionBar().hide();
		
		this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		this.topLayout = new LinearLayout(this);
		this.sentenceScrollView = new HorizontalScrollView(this);
		this.sentenceLayout = new LinearLayout(this);
		this.stopSentenceButton = (ImageButton) findViewById(R.id.imageButtonReturn);
		this.clearSentenceButton = (ImageButton) findViewById(R.id.imageButtonClear);
		this.botLayout = new ScrollView(this);
		int nbCol = UITools.getNbColumn(getWindowManager().getDefaultDisplay(),
				getResources().getConfiguration().orientation, 90, 5);
		this.listWordLayout = new GridLayout(this);
		this.listWordLayout.setColumnCount(nbCol);
		this.wordsSentenceButtons = new ArrayList<GridLayout>();
		this.wordsToSelectButtons = new ArrayList<GridLayout>();
		/*--------------------------------------*/
		this.mainLayout.addView(this.topLayout);
		this.mainLayout.addView(this.botLayout);
		this.topLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.topLayout.addView(this.sentenceScrollView);
		this.sentenceScrollView.addView(this.sentenceLayout);

		// Stop button
		this.stopSentenceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// Action of Stop button, save logs (historic)
		this.clearSentenceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date actuelle = new Date();
				String date = dateFormat.format(actuelle);
				ArrayList<Lexicon> copy = new ArrayList<Lexicon>(
						wordsFromSentence);
				logs.add(new Entry(date, copy));
				deleteWordFromSentenceAction(0);
			}
		});

		this.sentenceLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.sentenceLayout.setBackgroundColor(Color.LTGRAY);

		this.sentenceLayout.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 110));
		this.botLayout.addView(this.listWordLayout);

		this.drawWordsSentence();
		this.drawWordsToSelect();
	}

	/**
	 * Draw the sentence.
	 **/
	private void drawWordsSentence() {
		for (int i = 0; i < this.wordsFromSentence.size(); i++) {
			// For each available word, create a button
			ImageButton tmpButton = new ImageButton(this);
			GridLayout tmpLayout = new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);

			// Get the picture of the word
			Bitmap bit = BitmapFactory.decodeFile(this.wordsFromSentence.get(i)
					.getPictureSource());
			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(bit, 80, 80,
					false));

			// Set the text
			TextView tmpName = new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(this.wordsFromSentence.get(i).getWord());

			// Add the picture button to the view
			tmpLayout.addView(tmpButton);
			tmpLayout.addView(tmpName);

			this.wordsSentenceButtons.add(tmpLayout);

			this.sentenceLayout.addView(tmpLayout);

			final int wordIndex = i;
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteWordFromSentenceAction(wordIndex);
				}
			});
		}
	}

	/**
	 * Draw the keyboard.
	 **/
	private void drawWordsToSelect() {
		for (int i = 0; i < this.wordsToSelect.size(); i++) {
			ImageButton tmpButton = new ImageButton(this);
			GridLayout tmpLayout = new GridLayout(this);
			tmpLayout.setColumnCount(1);
			tmpLayout.setPadding(5, 5, 5, 5);

			Bitmap bit = BitmapFactory.decodeFile(this.wordsToSelect.get(i)
					.getPictureSource());
			tmpButton.setImageBitmap(Bitmap.createScaledBitmap(bit, 80, 80,
					false));

			TextView tmpName = new TextView(this);
			tmpName.setGravity(Gravity.CENTER);
			tmpName.setText(this.wordsToSelect.get(i).getWord());

			tmpLayout.addView(tmpButton);
			tmpLayout.addView(tmpName);

			this.wordsToSelectButtons.add(tmpLayout);

			this.listWordLayout.addView(tmpLayout);

			final int wordIndex = i;
			tmpButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectWordAction(wordIndex);
				}
			});
		}
	}

	/**
	 * Go back the automate of each method of the child.
	 * 
	 * @param wordIndex
	 *            (int): id of the picture/word to delete (the nexts are delete
	 *            equally).
	 */
	private void deleteWordFromSentenceAction(int wordIndex) {
		this.sentenceLayout.removeAllViews();
		this.listWordLayout.removeAllViews();
		this.wordsToSelect.clear();

		for (int i = this.wordsFromSentence.size() - 1; i >= 0; i--) {
			this.wordsFromSentence.remove(i);
			if (i == wordIndex)
				break;
		}
		for (int i = 0; i < this.automates.size(); i++) {
			this.automates.get(i).moveBackward(this.wordsFromSentence);
			this.getEligibleWord(this.automates.get(i));
		}
		this.drawWordsSentence();
		this.drawWordsToSelect();
	}

	/**
	 * Go to the next step in the automate, add word in the sentence and update
	 * the keyboard.
	 * 
	 * @param wordIndex
	 *            (int): id of the new word.
	 */
	private void selectWordAction(int wordIndex) {
		Lexicon word = this.wordsToSelect.remove(wordIndex);
		String catName = word.getCategory().getName();

		this.wordsToSelect.clear();
		this.sentenceLayout.removeAllViews();
		this.listWordLayout.removeAllViews();

		this.wordsFromSentence.add(word);

		for (int i = 0; i < this.automates.size(); i++) {
			this.automates.get(i).moveForwardToNextCat(catName);
			this.getEligibleWord(this.automates.get(i));
		}
		this.drawWordsSentence();
		this.drawWordsToSelect();
	}

	/**
	 * Display the first available words according to the grammars of the child.
	 **/
	private void setListWords() {
		for (int i = 0; i < this.automates.size(); i++) {
			this.getEligibleWord(this.automates.get(i));
		}
	}

	/**
	 * Get the eligible word by the child according to a grammar/a automate.
	 * 
	 * @param automate
	 *            (Automate): Automate.
	 */
	private void getEligibleWord(Automate automate) {
		/*
		// Build a Term frequency Vector of word from the historic of current child.
		Map<String, Integer> termFrequencyVector = new HashMap<String, Integer>();
		// If child have historic.
		if (logs != null) {
			// For each sequence in historic.
			for (int i = 0; i < logs.size(); i++) {
				// Get sequence.
				List<Lexicon> sequence = logs.get(i).getSequence();
				if (sequence.size() > 0) {
					// For each word of the sequence.
					for (int j = 0; j < sequence.size(); j++) {
						if (sequence.get(j) != null) {
							// Build the Term Frequency Vector.
							String word = sequence.get(j).getWord();
							Integer count = termFrequencyVector.get(word);
							// If the word doesn't exist in the TF vector yet.
							if (count == null) {
								termFrequencyVector.put(word, 1);
							} else { // Else increment !
								termFrequencyVector.put(word, count + 1);
							}

						}
					}
				}
			}
		}
		*/
		// Get the eligible words from automate.
		ArrayList<Lexicon> eligibleWords;
		eligibleWords = automate.getWordsToDisplay();
		/*
		// Lexicon Frequency Vector according to Term Frequency Vector.
		Map<Lexicon, Integer> lexiconFrequencyVector = new HashMap<Lexicon, Integer>();
		// Build a Lexicon Frequency Vector from the eligible words according to the pre-processed Term Frequency Vector.
		if (eligibleWords.size() > 0) {
			// For each eligible word.
			for (int j = 0; j < eligibleWords.size(); j++) {
				String word = eligibleWords.get(j).getWord();
				// Get its frequency according to its word and build the Lexicon Frequency Vector with it.
				lexiconFrequencyVector.put(eligibleWords.get(j), termFrequencyVector.get(word));
			}
		}
		// Sort in asc order the Lexicon Frequency Vector.
		Map<Lexicon, Integer> sortedLexiconFrequencyVector = sortByComparator(lexiconFrequencyVector);

		// Rebuild a List of Lexicon item according their frequency in the Lexicon Frequency Vector.
		ArrayList<Lexicon> eligibleWordsInDescOrder = null;
		// For each Lexicon item in the Lexicon Frequency Vector (thus for each eligible words).
		for (java.util.Map.Entry<Lexicon, Integer> entry : sortedLexiconFrequencyVector.entrySet()) {
			Lexicon key = entry.getKey();
			// Rebuild a ArrayList of Lexicon item like the eligibleWords ArrayList.
			eligibleWordsInDescOrder.add(key);
		}
		// Reverse the ArrayList order, to sort it in desc order.
		Collections.reverse(eligibleWordsInDescOrder);
		// Add the final sorted eligible words list in the GridLayout.
		*/
		this.wordsToSelect.removeAll(eligibleWords);
		this.wordsToSelect.addAll(eligibleWords);
	}

	@SuppressWarnings("unused")
	private static Map<Lexicon, Integer> sortByComparator(Map<Lexicon, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<Lexicon, Integer>> list = new LinkedList<Map.Entry<Lexicon, Integer>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Lexicon, Integer>>() {
			public int compare(Map.Entry<Lexicon, Integer> o1,
					Map.Entry<Lexicon, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Lexicon, Integer> sortedMap = new LinkedHashMap<Lexicon, Integer>();
		for (Iterator<Map.Entry<Lexicon, Integer>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<Lexicon, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}