package edu.rit.CSCI652.impl;
import com.google.gson.Gson;
import edu.rit.CSCI652.demo.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PubSubAgent implements PublisherI, SubscriberI
{

	public static final String SERVER_IP = "6789";


	public static void main(String[] args){


		int port = 9999;
		if(args.length == 1){
			port = Integer.parseInt(args[0]);
		}

		UDPSystem udpSystem = new UDPSystem(port);
		udpSystem.getMessages(new ServerI() {

			@Override
			public void success(Message message, int port) {

				System.out.println("Server port:" + port);

				switch (message.getType()){

					case 1:

						ArrayList<Topic> topicArrayList = message.getTopicList();
						for(Topic topic:topicArrayList){
							System.out.println(topic);
						}

						PubSubMenu pubSubMenu = new PubSubMenu();
						pubSubMenu.showTopics(topicArrayList, new PubSubMenu.TopicsInterface() {
							@Override
							public void selectedTopic(Topic topic) {

							}
						});

						break;
					case 2:
						break;
					case 3:
						break;
				}

			}

			@Override
			public void success(Message message, String ip) {

			}
		});

		PubSubMenu pubSubMenu = new PubSubMenu();
		pubSubMenu.showMenu(new PubSubMenu.PubSubMenuInterface() {

			@Override
			public void invokePublish() {

				Message message = new Message();
				message.setType(Message.REQUEST_TOPICS);

				try {
					udpSystem.sendMessageLocal(message, Integer.parseInt(SERVER_IP));
					//udpSystem.sendMessage(message, SERVER_IP);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void invokeAdvertise() {

			}

			@Override
			public void invokeSubscribe() {

			}

			@Override
			public void invokeRead() {

			}

			@Override
			public void invokeUnsubscribe() {

			}
		});
	}

	@Override
	public void displayTopics(List<Topic> topics)
	{
		//please change the following.

	}

	@Override
	public void displayEvents(List<Event> events)
	{

	}

	@Override
	public void subscribe(Topic topic)
	{

	}

	@Override
	public void subscribe(String keyword)
	{
	}

	@Override
	public void unsubscribe(Topic topic)
	{

	}

	@Override
	public void unsubscribe()
	{


	}

	@Override
	public void listSubscribedTopics()
	{

	}

	@Override
	public void publish(Event event)
	{

	}

	@Override
	public void advertise(Topic newTopic)
	{


	}

}
