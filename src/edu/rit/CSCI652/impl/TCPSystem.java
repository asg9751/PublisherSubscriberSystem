package edu.rit.CSCI652.impl;

import com.google.gson.Gson;
import edu.rit.CSCI652.demo.Message;

import java.io.*;
import java.net.*;

/**
 * @author Thomas Binu
 * @author Ruzan Sasuri
 * @author Amol Gaikwad
 *         <p>
 *         Utility class to handle TCP
 */
public class TCPSystem
{


    boolean close = false;
    public int sendPort;
    public int receivePort;
    ServerI serverI;

    public TCPSystem(int sendPort, int receivePort)
    {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
    }


    public void sendMessage(Message message, String ipAddress) throws IOException
    {

        Gson gson = new Gson();
        String messageStr = gson.toJson(message);
        sendToClient(messageStr, ipAddress, sendPort);
    }

    public void setTCPInterface(ServerI serverI){
        this.serverI = serverI;
    }


    public void startMessageServer()
    {
        new Thread()
        {
            @Override
            public void run()
            {

                try
                {
                    ServerSocket serverSocket = new ServerSocket(receivePort);
//                    System.out.println("Connected.");
                    while (!close)
                    {
                        Socket receiverSocket = serverSocket.accept();

                        if (receiverSocket.isConnected())
                        {

                            Thread thread = new Thread()
                            {
                                @Override
                                public void run() {
                                    handleMessage();
                                }
                                public synchronized void handleMessage() {
                                    String recieverIp = receiverSocket.getInetAddress().getHostAddress();

                                    try
                                    {
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));
                                        String messageStr = bufferedReader.readLine();

                                        receiverSocket.close(); //Close connection after reading

                                        if (messageStr != null)
                                        {
                                            Gson gson = new Gson();
                                            Message message = gson.fromJson(messageStr, Message.class);
                                            serverI.gotMessage(message, recieverIp);
                                        }

                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            thread.start();

                        }

                    }
                } catch (IOException e)
                {
                    System.out.println("I/O error: " + e);
                }
            }

        }.start();

    }


    public void close()
    {
        close = true;
    }

    public void sendToClient(String line, String ipAddress, int port)
    {

        try
        {
            InetAddress inetAddress = Inet4Address.getByName(ipAddress);
            System.out.println("Sending to:" + ipAddress);
            Socket receiverSocket = new Socket(inetAddress, port);
            DataOutputStream dataOutputStream = new DataOutputStream(receiverSocket.getOutputStream());
            dataOutputStream.writeBytes(line);
            receiverSocket.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}

