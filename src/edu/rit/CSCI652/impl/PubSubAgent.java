package edu.rit.CSCI652.impl;

import edu.rit.CSCI652.demo.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Thomas Binu
 * @author Ruzan Sasuri
 * @author Amol Gaikwad
 * <p>
 * PubSubAgent plays the role of publisher and subscriber
 */

public class PubSubAgent {

    public static final String SERVER_IP = "172.17.0.2";
    //    public static final String SERVER_IP = "localhost";
    public static final int SEND_PORT = 6789;
    public static final int RECEIVE_PORT = 6790;

    public static void authenticate(TCPSystem tcpSystem, String username) {
        Message message = new Message();
        message.setType(Message.USER_AUTHENTICATION);
        message.setUsername(username);

        try {
            tcpSystem.sendMessage(message, SERVER_IP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        PubSubMenu pubSubMenu = new PubSubMenu();

        TCPSystem tcpSystem = new TCPSystem(SEND_PORT, RECEIVE_PORT);
        tcpSystem.setTCPInterface(new ServerI() {

            @Override
            public void gotMessage(Message message, String ip) {
                Logging.print("Server PORT:" + SEND_PORT + ", Message Type:" + message.getType());

                switch (message.getType()) {


                    case Message.NOTIFICATION_TOPIC:


                        Topic topic = message.getTopic();
                        System.out.println("***** Topics notification *****");
                        System.out.println("Topic " + topic.getName() + " has been added");
                        System.out.println("***** Topics end *****\n");

                        System.out.println("Choose option:");
                        //pubSubMenu.showMenu();

                        break;


                    case Message.NOTIFICATION_EVENT:

                        ArrayList<Event> eventList1 = message.getEventList();
                        System.out.println();
                        if (eventList1.size() == 0) {
                            System.out.println("There are no events to display");
                        } else {
                            System.out.println("***** Events received for subscribed topics *****");
                            for (Event event : eventList1) {
                                System.out.println("Title: " + event.getTitle());
                                System.out.println("Content: " + event.getContent());
                                System.out.println();
                            }
                            System.out.println("***** Events end *****\n");
                        }

                        System.out.print("Choose menu option: ");


                        break;

                    case Message.PUBLISH_REQUEST_TOPICS:

                        if (message.getTopicList().size() == 0) {
                            System.out.println("There are no topics in the server");
                        } else {

                            new PubSubMenu().showTopics(message.getTopicList(), new PubSubMenu.topicInterface() {
                                @Override
                                public void selectedTopic(Topic topic) {
                                    Scanner in = new Scanner(System.in);
                                    System.out.print("Enter your title for the content:");
                                    String title = in.nextLine();
                                    System.out.print("Enter your content:");
                                    String content = in.nextLine();

                                    Event event = new Event(topic.getId(), title, content);
                                    Message sendMessage = new Message();
                                    sendMessage.setType(Message.PUBLISH_SEND_EVENT);
                                    sendMessage.setEvent(event);
                                    try {
                                        //TODO
                                        tcpSystem.sendMessage(sendMessage, ip);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        pubSubMenu.showMenu();
                        break;

                    case Message.SUBSCRIBE_REQUEST_TOPICS:

                        if (message.getTopicList().size() == 0) {
                            System.out.println("There are no topics in the server");
                        } else {

                            new PubSubMenu().showTopics(message.getTopicList(), new PubSubMenu.topicInterface() {
                                @Override
                                public void selectedTopic(Topic topic) {
                                    Message sendMessage = new Message();
                                    sendMessage.setType(Message.SUBSCRIBE_SELECTED_TOPIC);
                                    sendMessage.setTopic(topic);
                                    try {
                                        //TODO
                                        tcpSystem.sendMessage(sendMessage, ip);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        pubSubMenu.showMenu();
                        break;

                    case Message.UNSUB_REQUEST_TOPICS:

                        if (message.getTopicList().size() == 0) {
                            System.out.println("There are no topics in the server");
                        } else {
                            new PubSubMenu().showTopics(message.getTopicList(), new PubSubMenu.topicInterface() {
                                @Override
                                public void selectedTopic(Topic topic) {

                                    Message sendMessage = new Message();
                                    sendMessage.setType(Message.UNSUB_SELECT_TOPIC);
                                    sendMessage.setTopic(topic);
                                    try {

                                        //TODO
                                        tcpSystem.sendMessage(sendMessage, ip);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                        pubSubMenu.showMenu();
                        break;


                    case Message.READ_REQUEST_EVENTS:

                        ArrayList<Event> eventList = message.getEventList();
                        if (eventList.size() == 0) {
                            System.out.println("There are no events to display");
                        } else {
                            System.out.println("***** Events received for subscribed topics *****");
                            for (Event event : message.getEventList()) {
                                System.out.println("Title: " + event.getTitle());
                                System.out.println("Content: " + event.getContent());
                                System.out.println();
                            }
                            System.out.println("***** Events end *****\n");
                        }
                        pubSubMenu.showMenu();

                        break;

                    case Message.READ_FROMKEYWORD_REQUEST_EVENTS:

                        ArrayList<Event> eventsListKey = message.getEventList();

                        if (eventsListKey.size() == 0) {
                            System.out.println("There are no events to display");
                        } else {

                            for (Event event : eventsListKey) {
                                System.out.println("Title: " + event.getTitle());
                                System.out.println("Content: " + event.getContent());
                                System.out.println();
                            }
                        }
                        pubSubMenu.showMenu();

                        break;

                    case Message.ADVERTISE_REQUEST_TOPICS:
                        System.out.println("Topics have been advertised");

                        break;

                    case Message.USER_AUTHENTICATION:
                        System.out.println("Authentication done. Showing menu");
                        pubSubMenu.startAgent();
                        break;
                }

            }

            @Override
            public void failure() {

            }

            @Override
            public void sentMessageSuccess() {

            }

            @Override
            public void sendMessageFailed() {

            }
        });

        tcpSystem.startMessageServer();


        pubSubMenu.setPubSubMenuInterface(new PubSubMenu.PubSubMenuInterface() {

            @Override
            public void invokePublish() {

                Message message = new Message();
                message.setType(Message.PUBLISH_REQUEST_TOPICS);

                try {

                    tcpSystem.sendMessage(message, SERVER_IP);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void invokeAdvertise() {

                Scanner in = new Scanner(System.in);
                System.out.print("Enter your topic to advertise:");
                String topicName = in.nextLine();

                System.out.print("Enter your keywords:");
                String keywords = in.nextLine();

                Message message = new Message();
                message.setTopic(new Topic(0, topicName, keywords));
                message.setType(Message.ADVERTISE_REQUEST_TOPICS);

                try {

                    //TODO
                    tcpSystem.sendMessage(message, SERVER_IP);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                pubSubMenu.showMenu();

            }

            @Override
            public void invokeSubscribe() {

                Message message = new Message();
                message.setType(Message.SUBSCRIBE_REQUEST_TOPICS);

                try {

                    tcpSystem.sendMessage(message, SERVER_IP);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void invokeRead() {

                Message message = new Message();
                message.setType(Message.READ_REQUEST_EVENTS);

                try {

                    tcpSystem.sendMessage(message, SERVER_IP);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void invokeReadFromKeyword() {
                Scanner in = new Scanner(System.in);
                System.out.print("Enter the keywords:");
                String keywords = in.next();
                Message message = new Message();
                message.setTopic(new Topic(0, "test", keywords));
                message.setType(Message.READ_FROMKEYWORD_REQUEST_EVENTS);

                try {

                    //TODO
                    tcpSystem.sendMessage(message, SERVER_IP);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void invokeUnsubscribe() {

                Message message = new Message();
                message.setType(Message.UNSUB_REQUEST_TOPICS);

                try {

                    tcpSystem.sendMessage(message, SERVER_IP);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        Scanner in = new Scanner(System.in);
        System.out.print("Please enter your username: ");
        String username = in.nextLine();
        authenticate(tcpSystem, username);

    }


}
