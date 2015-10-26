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

	private AppData data;

	private List<Entry> logs; // The logs produced bu the child

	private TextView titre; // Title
	private LinearLayout generalLayout;

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
		// Update the data
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
	/* == TRAITEMENTS == */
	/*
	 * ==========================================================================
	 * ==========================================
	 */

	/**
	 * Display the window.
	 **/
	private void toDisplay() {
		setContentView(R.layout.activity_child_log);

		// Get the parent intent
		Intent intent = this.getIntent();

		if (intent != null) {
			// Get the child's name
			final String nom = getIntent().getStringExtra("nom");
			final String prenom = getIntent().getStringExtra("prenom");
			generalLayout = (LinearLayout) findViewById(R.id.generalLayout);

			titre = (TextView) findViewById(R.id.small_screen_text);

			String title = getResources().getString(R.string.child_logs);
			titre.setText(title + ' ' + prenom);

			// Get child's logs
			logs = XMLTools.loadLogs(this, nom, prenom, data);

			if (logs != null) {
				for (int i = 0; i < logs.size(); i++) {

					HorizontalScrollView logScrollView = new HorizontalScrollView(
							this);

					// Add text
					LinearLayout logLayout = new LinearLayout(this);
					logLayout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout dateLayout = new LinearLayout(this);
					dateLayout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout sequenceLayout = new LinearLayout(this);

					// date
					String log = "     " + logs.get(i).getDate();
					TextView text = new TextView(this);
					text.setText(log);
					text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
					dateLayout.addView(text);

					// sequence
					List<Lexicon> sequence = logs.get(i).getSequence();

					if (sequence.size() > 0) {
						for (int j = 0; j < sequence.size(); j++) {
							// picture
							ImageView tmpButton = new ImageView(this);
							GridLayout tmpLayout = new GridLayout(this);
							tmpLayout.setColumnCount(1);
							tmpLayout.setPadding(5, 5, 5, 5);
							TextView tmpName = new TextView(this);
							tmpName.setGravity(Gravity.CENTER);

							if (sequence.get(j) != null) {
								Bitmap bit = BitmapFactory.decodeFile(sequence
										.get(j).getPictureSource());
								tmpButton
										.setImageBitmap(Bitmap
												.createScaledBitmap(bit, 80,
														80, false));
								tmpName.setText(sequence.get(j).getWord());

							} else {
								TextView tmp = new TextView(this);
								tmp.setText(R.string.word_not_found);
								dateLayout.addView(tmp);
							}

							// Add the picture button into the gridview
							tmpLayout.addView(tmpButton);
							tmpLayout.addView(tmpName);

							// Add the sequence into the layout
							sequenceLayout.addView(tmpLayout);
						}
					} else {
						TextView tmp = new TextView(this);
						tmp.setText(R.string.empty_session);
						dateLayout.addView(tmp);
					}

					logLayout.addView(dateLayout);
					logLayout.addView(sequenceLayout);
					logScrollView.addView(logLayout);
					// Add the horizontal scrollview (sequence) into the general
					// vertical scrollview (the window display)
					this.generalLayout.addView(logScrollView);
				}
			}
		}
	}

}
