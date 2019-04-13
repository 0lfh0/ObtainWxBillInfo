package com.example.a32960.xposedmoudle;

import java.net.Socket;

public class ClientHandlerThread extends Thread
{
    private Socket client;
    public ClientHandlerThread(Socket client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {

    }
}
