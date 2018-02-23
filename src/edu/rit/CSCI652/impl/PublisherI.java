package edu.rit.CSCI652.impl;

import edu.rit.CSCI652.demo.Event;
import edu.rit.CSCI652.demo.Topic;

public interface PublisherI
{
	/*
	 * publish an event of a specific topic with title and content
	 */
	public void publish(Event event);
	
	/*
	 * advertise new topic
	 */
	public void advertise(Topic newTopic);
}
