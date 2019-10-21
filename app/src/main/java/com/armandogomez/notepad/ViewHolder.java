package com.armandogomez.notepad;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
	TextView title;
	TextView body;
	TextView date;

	ViewHolder(View view) {
		super(view);
		title = view.findViewById(R.id.title);
		body = view.findViewById(R.id.body);
		date = view.findViewById(R.id.date);
	}
}
