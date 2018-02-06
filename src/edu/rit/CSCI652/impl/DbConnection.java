package edu.rit.CSCI652.impl;

import edu.rit.CSCI652.demo.Event;
import edu.rit.CSCI652.demo.Topic;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author Thomas Binu
 * @author Amol Gaikwad
 */
public class DbConnection {

    static String databasePath;

    private static final DbConnection INSTANCE = new DbConnection();


    public static DbConnection getInstance()
    {
        return INSTANCE;
    }

    public DbConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        String databaseDir = new File("jdbc:sqlite:" + System.getProperty("user.dir"), "database").toString();
        Logging.print("jdbc:sqlite:" + System.getProperty("user.dir"));
        Logging.print(databaseDir);
        databasePath = new File(databaseDir, "pubsub.db").toString();
        createDatabase();
        createTables();

    }

    public static void createDatabase()
    {
        try (Connection conn = DriverManager.getConnection(databasePath)) {

            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                Logging.print("The driver name is " + meta.getDriverName());
                Logging.print("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertEvent(int topicId, String title, String content)
    {
        int time = (int)System.currentTimeMillis();
        String insertTopicSql = "INSERT INTO event(topic_id, title, content, publishdatetime)\n" +
                "VALUES(\"" + topicId + "\", \"" + title + "\", \"" + content + "\", \"" + time + "\");";

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(insertTopicSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Event> getAllEvents() {

        String sql = "SELECT *  FROM event";
        ArrayList<Event> eventList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                int id = rs.getInt("id");
                int topicId = rs.getInt("topic_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                int publishDateTime = rs.getInt("publishdatetime");
                eventList.add(new Event(topicId, title, content));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventList;
    }

    public ArrayList<Event> getAllEventsForSubscriber(String ipAddress) {

        String sql = "SELECT * FROM subscriber WHERE \n" +
                "ipaddress = '" + ipAddress + "';";

        ArrayList<Event> eventList = new ArrayList<>();
        int sublastactive = 0;
        int subId = 0;

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            sublastactive = rs.getInt("lastactivedatetime");
            subId = rs.getInt("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Logging.print(sublastactive);

        ArrayList<Topic> subTopics = getSubscriberTopics(subId, true);

        String topicIds = "";

        if(subTopics.size()==0)
            return eventList;
        
        for (Topic t : subTopics) {
           topicIds += t.getId() + ",";
            Logging.print(t.getId());
        }



        topicIds = topicIds.substring(0, topicIds.length() - 1);

        sql = "SELECT * FROM event WHERE \n" +
                "topic_id IN (" + topicIds + ") AND publishdatetime >= '" + sublastactive + "';";

        try (Connection conn2 = DriverManager.getConnection(databasePath);
             Statement stmt2 = conn2.createStatement();
             ResultSet rs2 = stmt2.executeQuery(sql)) {

            // loop through the result set
            while (rs2.next()) {
                int id = rs2.getInt("id");
                int topicId = rs2.getInt("topic_id");
                String title = rs2.getString("title");
                String content = rs2.getString("content");
                int publishDateTime = rs2.getInt("publishdatetime");
                eventList.add(new Event(topicId, title, content));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventList;
    }

    public ArrayList<Event> getAllEventsFromKeyword(String ipAddress, String keyword) {
        String sql = "SELECT * FROM subscriber WHERE \n" +
                "ipaddress = '" + ipAddress + "';";


        int sublastactive = 0;

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // loop through the result set
            sublastactive = rs.getInt("lastactivedatetime");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Logging.print(sublastactive);



        sql = "SELECT *  FROM topic WHERE \n" +
                "keywords LIKE '%" + keyword + "%';";

        ArrayList<Topic> topicList = new ArrayList<>();
        ArrayList<Event> eventList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                int id = rs.getInt("id");
                String words = rs.getString("keywords");
                String name = rs.getString("name");
                Topic topic = new Topic(id, name, words);
                topicList.add(topic);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String topicIds = "";

        for (Topic t : topicList) {
            topicIds += t.getId() + ",";
            Logging.print(t.getId());
        }
        topicIds = topicIds.substring(0, topicIds.length() - 1);

        sql = "SELECT * FROM event WHERE \n" +
                "topic_id IN (" + topicIds + ") AND publishdatetime >= '" + sublastactive + "';";

        try (Connection conn2 = DriverManager.getConnection(databasePath);
             Statement stmt2 = conn2.createStatement();
             ResultSet rs2 = stmt2.executeQuery(sql)) {

            // loop through the result set
            while (rs2.next()) {
                int id = rs2.getInt("id");
                int topicId = rs2.getInt("topic_id");
                String title = rs2.getString("title");
                String content = rs2.getString("content");
                eventList.add(new Event(topicId, title, content));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventList;
    }

    public void insertTopic(String name, String keywords) {

        String insertTopicSql = "INSERT INTO topic(name, keywords)\n" +
                "VALUES('" + name + "', '" + keywords + "');";


        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(insertTopicSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Topic> getAllTopics() {

        String sql = "SELECT *  FROM topic";
        ArrayList<Topic> topicList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                int id = rs.getInt("id");
                String words = rs.getString("keywords");
                String name = rs.getString("name");
                Topic topic = new Topic(id, name, words);
                topicList.add(topic);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return topicList;
    }

    public int getTopicId(String name) {

        String sql = "SELECT id  FROM topic WHERE \n" +
                "name = '" + name + "';";
        int topicId = 0;

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                topicId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return topicId;
    }

    public ArrayList<Topic> getSubscriberTopics(int sub_id, boolean isSubscribed) {

        String sql = "";
        if (isSubscribed) {
            sql = "SELECT *  FROM topic WHERE id IN (SELECT topic_id FROM subscriber_topic WHERE \n" +
                    "subscriber_id = '" + sub_id + "');";
        } else {
            sql = "SELECT *  FROM topic WHERE id NOT IN (SELECT topic_id FROM subscriber_topic WHERE \n" +
                    "subscriber_id = '" + sub_id + "');";
        }

        ArrayList<Topic> topicList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {

                int id = rs.getInt("id");
                String words = rs.getString("keywords");
                String name = rs.getString("name");
                Topic topic = new Topic(id, name, words);
                topicList.add(topic);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return topicList;
    }

    public int getSubscriberId(String ipaddress) {

        String sql = "SELECT id  FROM subscriber WHERE \n" +
                "ipaddress = '" + ipaddress + "';";
        int subId = 0;

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set

            subId = rs.getInt("id");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return subId;
    }

    public void insertSubscriber(String ipaddress, long lastactivedatetime) {
        /*
        String insertSubscriberSql = "INSERT INTO subscriber(ipaddress, lastactivedatetime)\n" +
                "VALUES('" + ipaddress + "', '" + lastactivedatetime + "');";
        */
        String insertSubscriberSql = "INSERT INTO subscriber(ipaddress, lastactivedatetime)\n" +
                "SELECT '" + ipaddress + "', '" + lastactivedatetime + "' WHERE NOT EXISTS " +
                "(SELECT 1 FROM subscriber WHERE ipaddress ='" +ipaddress+ "');";
        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(insertSubscriberSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateSubscriberLastActive(String ipaddress) {
        int time = (int)System.currentTimeMillis();
        String updateSubscriberSql = "UPDATE subscriber\n"+
                "SET lastactivedatetime ='"+time+"' WHERE ipaddress ='" +ipaddress+ "'";

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(updateSubscriberSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertSubscriberTopic(int sub_id, int top_id) {

        String insertSubscriberTopicSql = "INSERT INTO subscriber_topic(subscriber_id, topic_id)\n" +
                "VALUES('" + sub_id + "', '" + top_id + "');";

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(insertSubscriberTopicSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeSubscriberTopic(int sub_id, int top_id) {

        String removeSubscriberTopicSql = "DELETE FROM subscriber_topic WHERE \n" +
                "subscriber_id = '" + sub_id + "' AND topic_id = '" + top_id + "';";

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(removeSubscriberTopicSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void createTables() {

        String topicSql = "CREATE TABLE IF NOT EXISTS topic (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text UNIQUE NOT NULL,\n"
                + "	keywords text NOT NULL"
                + ");";


        String eventSql = "CREATE TABLE IF NOT EXISTS event (\n"
                + "	id integer PRIMARY KEY,\n"
                + " topic_id integer NOT NULL,\n"
                + "	title text NOT NULL,\n"
                + "	content text NOT NULL,\n"
                + "	publishdatetime integer NOT NULL,\n"
                + " FOREIGN KEY (topic_id) REFERENCES topic (id)"
                + ");";

        String subscriberSql = "CREATE TABLE IF NOT EXISTS subscriber (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	ipaddress text NOT NULL,\n"
                + "	lastactivedatetime integer NOT NULL"
                + ");";


        String subscriberTopicSql = "CREATE TABLE IF NOT EXISTS subscriber_topic (\n"
                + "	subscriber_id integer NOT NULL,\n"
                + "	topic_id integer NOT NULL,\n"
                + " PRIMARY KEY (subscriber_id, topic_id), \n"
                + " FOREIGN KEY (subscriber_id) REFERENCES subscriber (id),\n"
                + " FOREIGN KEY (topic_id) REFERENCES topic (id)"
                + ");";

        //String eventSql = "DROP TABLE event";
        // String subscriberTopicSql = "DROP TABLE subscriber_topic";

        try (Connection conn = DriverManager.getConnection(databasePath);
             Statement stmt = conn.createStatement()) {

            stmt.execute(topicSql);
            stmt.execute(eventSql);
            stmt.execute(subscriberSql);
            stmt.execute(subscriberTopicSql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DbConnection conn = DbConnection.getInstance();

        conn.insertTopic("a", "c");
        conn.insertTopic("b", "d");
        conn.insertTopic("g", "i");
        ArrayList<Topic> topics = conn.getAllTopics();
        for (Topic topic : topics) {
            System.out.println(topic);
        }

        conn.insertEvent(conn.getTopicId("a"), "Business", "Biz Journal");
        conn.insertEvent(conn.getTopicId("b"), "Sports", "Basketball");

        conn.insertSubscriber("10.10.256.1", 12344);
        conn.insertSubscriber("10.10.256.2", 12345);
        System.out.println(conn.getSubscriberId("10.10.256.1"));
        System.out.println(conn.getTopicId("a"));
        conn.insertSubscriberTopic(conn.getSubscriberId("10.10.256.1"), conn.getTopicId("a"));
        conn.insertSubscriberTopic(conn.getSubscriberId("10.10.256.1"), conn.getTopicId("b"));
        conn.insertSubscriberTopic(conn.getSubscriberId("10.10.256.2"), conn.getTopicId("g"));

        conn.removeSubscriberTopic(conn.getSubscriberId("10.10.256.1"), conn.getTopicId("a"));

        ArrayList<Topic> topics2 = conn.getSubscriberTopics(conn.getSubscriberId("10.10.256.1"), true);

        for (Topic topic2 : topics2) {
            System.out.println("Subscribed " + topic2);
        }

        ArrayList<Topic> topics3 = conn.getSubscriberTopics(conn.getSubscriberId("10.10.256.1"), false);
        for (Topic topic3 : topics3) {
            System.out.println("Unsubscribed " + topic3);
        }

        ArrayList<Event> events = conn.getAllEventsForSubscriber("10.10.256.1");
        //ArrayList<Event> events = conn.getAllEvents();
        for (Event event : events) {
            System.out.println("Subscribed events " + event);
        }


    }
}
