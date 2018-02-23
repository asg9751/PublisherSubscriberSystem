package edu.rit.CSCI652.demo;

import java.util.ArrayList;

/**
 * Created by Ruzan on 2/1/2018.
 */
public class Message
{
    private String username;
    private int type;
    private ArrayList<Topic> topicList;
    private ArrayList<Event> eventList;
    private Subscriber subscriber;
    private Event event;
    private Topic topic;

    public static final int USER_AUTHENTICATION = 0;
    public static final int PUBLISH_REQUEST_TOPICS = 1;
    public static final int PUBLISH_SEND_EVENT = 2;
    public static final int SUBSCRIBE_REQUEST_TOPICS = 3;
    public static final int SUBSCRIBE_SELECTED_TOPIC = 4;
    public static final int READ_REQUEST_EVENTS = 5;
    public static final int READ_FROMKEYWORD_REQUEST_EVENTS = 6;
    public static final int ADVERTISE_REQUEST_TOPICS = 7;
    public static final int UNSUB_REQUEST_TOPICS = 8;
    public static final int UNSUB_SELECT_TOPIC = 9;
    public static final int NOTIFICATION_EVENT = 10;
    public static final int NOTIFICATION_TOPIC = 11;

    public Message() {

    }

    public Message(int type, ArrayList<Topic> topicList, ArrayList<Event> eventList, Subscriber subscriber)
    {
        this.type = type;
        this.topicList = topicList;
        this.eventList = eventList;
        this.subscriber = subscriber;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public ArrayList<Topic> getTopicList()
    {
        return topicList;
    }

    public void setTopicList(ArrayList<Topic> topicList)
    {
        this.topicList = topicList;
    }

    public ArrayList<Event> getEventList()
    {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList)
    {
        this.eventList = eventList;
    }

    public Subscriber getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber)
    {
        this.subscriber = subscriber;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
