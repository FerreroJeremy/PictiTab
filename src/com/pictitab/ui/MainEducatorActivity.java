package com.pictitab.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pictitab.data.AppData;

public class MainEducatorActivity extends Activity {

	private AppData data;

	// Buttons representing the home menu of the educator interface
	private Button categorydButton;
	private Button lexiconButton;
	private Button grammarButton;
	private Button childButton;

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
		this.data = (AppData) getIntent().getBundleExtra(
				MainActivity.DATAEXTRA_KEY)
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
		/*
		 * Bundle b = new Bundle(); b.putParcelable(MainActivity.DATA_KEY,
		 * data); this.getIntent().putExtra(MainActivity.DATAEXTRA_KEY, b);
		 * setResult(RESULT_OK, this.getIntent()); finish();
		 */

		Intent intent = new Intent(MainEducatorActivity.this,
				MainActivity.class);
		Bundle b = new Bundle();
		b.putParcelable(MainActivity.DATA_KEY, data);
		intent.putExtra(MainActivity.DATAEXTRA_KEY, b);
		setResult(RESULT_OK, intent);
		startActivity(intent);
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
		setContentView(R.layout.activity_main_educator);
		
		getActionBar().hide();

		// Each button send to the corresponding element manager window
		categorydButton = (Button) findViewById(R.id.selectCategoryAdministration);
		lexiconButton = (Button) findViewById(R.id.selectLexiconAdministration);
		grammarButton = (Button) findViewById(R.id.selectGrammarAdministration);
		childButton = (Button) findViewById(R.id.selectChildAdministration);

		// Categories
		categorydButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this,
						SelectCategoryActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 1);
			}
		});

		// Lexicon entries
		lexiconButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this,
						SelectLexiconActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 4);
			}
		});

		// Grammars
		grammarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this,
						SelectGrammarActivity.class);
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 1);
			}
		});

		// Children profiles
		childButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainEducatorActivity.this,
						SelectChildActivity.class);
				i.putExtra(MainActivity.TYPE_SOURCE, "educator_admin");
				Bundle b = new Bundle();
				b.putParcelable(MainActivity.DATA_KEY, data);
				i.putExtra(MainActivity.DATAEXTRA_KEY, b);
				startActivityForResult(i, 1);
			}
		});
	}
}
