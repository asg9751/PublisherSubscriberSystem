package edu.rit.CSCI652.impl;

import com.google.gson.Gson;
import edu.rit.CSCI652.demo.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class UDPSystem {

    /**
     * @author Thomas Binu
     * @author Ruzan Sasuri
     * @author Amol Gaikwad
     * <p>
     * Handles UDP for both client and server
     */

    private DatagramSocket socket;
    private boolean running;
    private boolean receiving;

    TimerI timerI;

    private boolean gotReply = true;

    public UDPSystem() {
        try {
            socket = new DatagramSocket(6789);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Connected.");
        receiving = false;
    }

    public UDPSystem(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println("Connected.");
        receiving = false;
    }


    public void setTimerI(TimerI timerI) {
        this.timerI = timerI;
    }

    public void startTimerTask() {
        if (timerI != null && !gotReply) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {

                            if (!gotReply) {
                                timerI.timedOut();
                                gotReply = true;
                            }
                        }
                    },
                    10000
            );
        }
    }

    public void sendMessage(Message message, String iPAddress, int port, boolean timer) throws IOException {

        Gson gson = new Gson();
        String messageStr = gson.toJson(message);
        byte[] buf = messageStr.getBytes();
        InetAddress address = InetAddress.getByName(iPAddress);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        Logging.print(iPAddress + ": message:" + messageStr);


        if (timer) {
            gotReply = false;
            startTimerTask();
        }
    }


    //For localhost
    public void sendMessageLocal(Message message, int port) throws IOException {


        InetAddress address = InetAddress.getByName("localhost");
        Logging.print("test:" + socket.getLocalPort());


        Gson gson = new Gson();
        String messageStr = gson.toJson(message);
        byte[] buf = messageStr.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        gotReply = false;
        startTimerTask();
    }

    public void sendMessageLocal(Message message, List<Integer> ports) throws IOException {
        byte[] buf;
        for (int port : ports) {
            sendMessageLocal(message, port);
        }
    }

    public void close() {
        socket.close();
    }

    public void getMessages(ServerI serverI) {
        if (!receiving) {
            receiving = true;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    running = true;
                    while (running) {
                        byte[] buf = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                        try {
                            socket.receive(receivePacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        gotReply = true;

                        InetAddress address = receivePacket.getAddress();
                        System.out.println("Got message from " + address.getHostAddress());
                        int port = receivePacket.getPort();

                        String messageStr = new String(receivePacket.getData(), 0, receivePacket.getLength());

                        new Thread() {
                            @Override
                            public void run() {

                                Gson gson = new Gson();
                                Message message = gson.fromJson(messageStr, Message.class);
                                serverI.gotMessage(message, address.getHostAddress());
                            }
                        }.start();

                    }
                    socket.close();
                }
            };
            thread.start();
        }
    }

}
