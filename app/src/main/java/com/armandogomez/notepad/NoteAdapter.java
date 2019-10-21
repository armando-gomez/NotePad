package com.armandogomez.notepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<ViewHolder> {
	private static final String TAG = "NoteAdapter";
	private List<Note> noteList;
	private MainActivity mainAct;

	NoteAdapter(List<Note> noteList, MainActivity mainActivity){
		this.noteList  = noteList;
		mainAct = mainActivity;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.note_list_row, parent, false);
		itemView.setOnClickListener((View.OnClickListener) mainAct);
		itemView.setOnLongClickListener((View.OnLongClickListener) mainAct);

		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
		Note note = noteList.get(pos);
		holder.title.setText(note.getTitle());
		holder.body.setText(note.getBody());
		holder.date.setText(new Date().toString());
	}

	@Override
	public int getItemCount() {return noteList.size();}
}
