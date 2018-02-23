package edu.rit.CSCI652.demo;

import java.util.List;

public class Topic {

	private int id;
	private String keywords;
	private String name;

	public int getId() {
		return id;
	}
	public Topic(int id, String name, String keywords) {
		this.id = id;
		this.keywords = keywords;
		this.name = name;
	}

	@Override
	public String toString() {
		return keywords + "," + name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
