package edu.rit.CSCI652.demo;

import java.util.List;

public class Event {

    public Event(int topicId, String title, String content) {

        this.topicId = topicId;
        this.title = title;
        this.content = content;

    }


    private int topicId;
    private String title;
    private String content;


    public int getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    @Override
    public String toString() {
        return topicId + "," + title + "," + content;
    }
}
