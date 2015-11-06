package com.pictitab.ui;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.pictitab.data.Item;

public class FileChooser extends ListActivity {

	public static final String DATA_Picture = "Picture_path";

	private File currentDir;
	private FileArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentDir = new File(Environment.getExternalStorageDirectory()
				.getPath());
		fill(currentDir);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		getIntent().putExtra(FileChooser.DATA_Picture, "");
		setResult(RESULT_CANCELED, this.getIntent());
		finish();
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle(f.getAbsolutePath());
		List<Item> dir = new ArrayList<Item>();
		List<Item> fls = new ArrayList<Item>();
		try {
			for (File ff : dirs) {
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if (ff.isDirectory()) {

					File[] fbuf = ff.listFiles();
					int buf = 0;
					if (fbuf != null) {
						buf = fbuf.length;
					} else
						buf = 0;
					String num_item = String.valueOf(buf);
					if (buf <= 1)
						num_item = num_item + " " + getResources().getString(R.string.item);
					else
						num_item = num_item + " " + getResources().getString(R.string.items);

					dir.add(new Item(ff.getName(), num_item, date_modify, ff
							.getAbsolutePath(), "directory_icon"));
				} else {
					fls.add(new Item(ff.getName(), this.formatSize(ff.length()),
							date_modify, ff.getAbsolutePath(), "file_icon"));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new Item("..", getResources().getString(R.string.parent_directory), "", f.getParent(),
					"directory_up"));
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}

	private static final double BASE = 1024, KB = BASE, MB = KB * BASE, GB = MB * BASE;
	private static final DecimalFormat df = new DecimalFormat("#.##");

    public String formatSize(double size) {
        if(size >= GB) {
            return df.format(size/GB) + " GB";
        }
        if(size >= MB) {
            return df.format(size/MB) + " MB";
        }
        if(size >= KB) {
            return df.format(size/KB) + " KB";
        }
        return "" + (int)size + " Bytes";
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if (o.getImage().equalsIgnoreCase("directory_icon")
				|| o.getImage().equalsIgnoreCase("directory_up")) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
		}
	}

	private void onFileClick(Item o) {
		final String selectedPicture = new String(currentDir.toString()
				+ File.separator + o.getName());
		getIntent().putExtra(FileChooser.DATA_Picture, selectedPicture);
		setResult(RESULT_OK, getIntent());
		finish();
	}
}
