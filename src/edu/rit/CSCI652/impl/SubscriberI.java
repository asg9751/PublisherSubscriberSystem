package edu.rit.CSCI652.impl;

import edu.rit.CSCI652.demo.Event;
import edu.rit.CSCI652.demo.Topic;

import java.util.List;

public interface SubscriberI
{
	/*
	 * subscribe to a topic
	 */
	public void subscribe(Topic topic);
	
	/*
	 * subscribe to a topic with matching keywords
	 */
	public void subscribe(String keyword);
	
	/*
	 * unsubscribe from a topic 
	 */
	public void unsubscribe(Topic topic);
	
	/*
	 * unsubscribe to all subscribed topics
	 */
	public void unsubscribe();
	
	/*
	 * show the list of topics current subscribed to 
	 */
	public void listSubscribedTopics();

	/**
	 * Prints the topics that the subscriber has not subscribed to.
	 */
	public void displayTopics(List<Topic> topics);

	/**
	 * Display events.
	 */
	public void displayEvents(List<Event> events);
	
}
