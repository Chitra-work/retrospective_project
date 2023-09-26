package com.springboot.retrospective;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feedback {
	
	@JsonProperty
	private List<Item> item;

	public List<Item> getItem() {
		return item;
	}

	public void setItem(List<Item> item) {
		this.item = item;
	}

}
