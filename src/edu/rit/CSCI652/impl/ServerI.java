package edu.rit.CSCI652.impl;

import edu.rit.CSCI652.demo.Message;

public interface ServerI
{
    void gotMessage(Message message, String ip);

    void failure();

    void sentMessageSuccess();

    void sendMessageFailed();



}
