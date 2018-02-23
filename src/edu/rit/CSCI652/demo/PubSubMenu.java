package edu.rit.CSCI652.demo;

import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Amol on 1/29/2018.
 */
public class PubSubMenu {

    PubSubMenuInterface pubSubMenuInterface;

    public interface PubSubMenuInterface {

        void invokePublish();

        void invokeAdvertise();

        void invokeSubscribe();

        void invokeRead();

        void invokeReadFromKeyword();

        void invokeUnsubscribe();

    }

    public interface topicInterface {

        void selectedTopic(Topic topic);
    }

    public void showTopics(ArrayList<Topic> topicArrayList, topicInterface topicInterface) {

        int idx = 1;
        for (Topic topic : topicArrayList) {
            System.out.println(idx + ". " + topic.getName());
            idx++;
        }

        System.out.print("\nChoose a topic:");
        Scanner in = new Scanner(System.in);
        int choice;

        try {
            choice = in.nextInt();
        } catch (Exception e) {
            showTopics(topicArrayList, topicInterface);
            return;
        }
        if (choice > 0 && choice <= topicArrayList.size())
            topicInterface.selectedTopic(topicArrayList.get(choice - 1));
        else
            showTopics(topicArrayList, topicInterface);

    }

    public void printMenu(){

        System.out.println("\n***** PubSub Menu *****");

        System.out.println("1. Publish");
        System.out.println("2. Advertise");
        System.out.println("3. Subscribe");
        System.out.println("4. Unsubscribe");
        System.out.println("0. Exit\n");
        System.out.print("Choose menu option : ");
    }


    public void setPubSubMenuInterface(PubSubMenuInterface pubSubMenuInterface) {
        this.pubSubMenuInterface = pubSubMenuInterface;
    }

    public void startAgent() {
        pubSubMenuInterface.invokeRead();
    }

    public void showMenu() {
        Scanner in = new Scanner(System.in);

        printMenu();

        boolean exit = false;
        int option = 0;
        try {
            option = in.nextInt();


        } catch (Exception e) {
            showMenu();
            return;
        }
        switch (option) {
            case 1:
                System.out.println("**** Starting publish ****\n");
                pubSubMenuInterface.invokePublish();
                return;
            case 2:
                System.out.println("**** Starting advertise ****\n");
                pubSubMenuInterface.invokeAdvertise();
                return;
            case 3:
                System.out.println("**** Starting subscribe ****\n");
                pubSubMenuInterface.invokeSubscribe();
                return;
            case 4:
                System.out.println("**** Starting unsubscribe ****\n");
                pubSubMenuInterface.invokeUnsubscribe();
                return;
            case 0:
                exit = true;
                System.out.println("**** Exiting PubSub Menu ****\n");
                return;
            default:
                System.out.println("Invalid choice \n");
        }


        System.out.println("**** Exiting PubSub Menu ****\n");
    }


}