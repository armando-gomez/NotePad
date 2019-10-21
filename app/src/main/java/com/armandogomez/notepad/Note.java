package com.armandogomez.notepad;

public class Note {
	private String title;
	private String body;

	Note(String title, String body) {
		this.title = title;
		this.body = body;
	}

	String getBody(){return body;}

	void setBody(String body) {this.body = body;}

	public String getTitle(){return title;}

	public void setTitle() {this.title = title;}
}
