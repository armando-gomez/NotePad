package com.armandogomez.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
	private static final int RESULT_BAD = 404;
	private static final int RESULT_NO = 500;

	private Menu menu;
	private EditText titleText;
	private EditText bodyText;
	private String originalTitle;
	private String originalBody;
	private int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		titleText = findViewById(R.id.titleText);
		bodyText = findViewById(R.id.bodyText);

		Intent intent = getIntent();
		if(intent.hasExtra("CURR_TITLE") && intent.hasExtra("CURR_BODY")) {
			this.originalTitle = intent.getStringExtra("CURR_TITLE");
			this.originalBody = intent.getStringExtra("CURR_BODY");
			titleText.setText(this.originalTitle);
			bodyText.setText(this.originalBody);
			this.pos = intent.getIntExtra("POS", -1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_save:
				Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void saveNote(MenuItem item) {
		String title = titleText.getText().toString();
		String body = bodyText.getText().toString();
		Intent data = new Intent();
		data.putExtra("NOTE_TITLE", title);
		data.putExtra("NOTE_BODY", body);
		if(!title.equals(originalTitle) || !body.equals(originalBody)) {
			data.putExtra("NOTE_POS", this.pos);
		}

		if(title.length() == 0) {
			setResult(RESULT_BAD, data);
		} else {
			setResult(RESULT_OK, data);
		}

		finish();
	}

	@Override
	public void onBackPressed() {
		String title = titleText.getText().toString();
		if(!title.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Save Note");
			builder.setMessage("Do you want to save this note?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveNote(null);
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_NO);
					finish();
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			setResult(RESULT_NO);
			finish();
		}
	}
}
