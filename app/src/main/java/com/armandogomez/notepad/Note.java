package com.armandogomez.notepad;

import java.text.DateFormat;
import java.util.Date;

public class Note {
	private String title;
	private String body;
	private String date;

	Note(String title, String body, Date date) {
		this.title = title;
		this.body = body;
		this.date = DateFormat.getDateTimeInstance().format(date);
	}

	public String getBody(){return body;}

	public void setBody(String body) {this.body = body;}

	public String getTitle(){return title;}

	public void setTitle(String title) {this.title = title;}

	public String getDate() {return date.toString();}

	public void setDate(Date date) {this.date = DateFormat.getDateTimeInstance().format(date);}
}
