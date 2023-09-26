package com.springboot.retrospective;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Retrospective {

	public Retrospective() {
	    this.feedBack = new ArrayList<>();
	}
	
	private String name;
	private String summary;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private List<String> participants;

	@JsonProperty
	private List<Feedback> feedBack;



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public List<Feedback> getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(List<Feedback> feedBack) {
		this.feedBack = feedBack;
	}




}
