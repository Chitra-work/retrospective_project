package com.springboot.retrospective;

public class Item {
    private String Name;
    private String Body;
    private String FeedbackType;


    public Item() {}

    public Item(String name, String body, String feedbackType) {
        this.Name = name;
        this.Body = body;
        this.FeedbackType = feedbackType;
    }

    // Getters and setters for Name, Body, and FeedbackType
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        this.Body = body;
    }

    public String getFeedbackType() {
        return FeedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.FeedbackType = feedbackType;
    }
}


enum FeedbackType{
	POSITIVE, NEGATIVE, IDEA, PRAISE
}




