package com.example.a32960.xposedmoudle;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class BuildServerSocketThread extends  Thread
{

    private int port = 55533;
    ServerSocket serverSocket;
    List<Socket> clients;
    public BuildServerSocketThread()
    {
        clients = new ArrayList<Socket>();
    }


    @Override
    public void run()
    {
//        int i = 0;
//        while(true)
//        {
//            XposedBridge.log(i + "  run() 当前线程：" + Thread.currentThread().getId());
//            XposedBridge.log("--------------------");
//            try
//            {
//                Thread.sleep(1000);
//            }catch(Exception e){
//                XposedBridge.log(e.getMessage());
//            }
//
//        }

        try
        {
            XposedBridge.log("run开启server socket");
            XposedBridge.log("--------------------");
            serverSocket= new ServerSocket(port);
            XposedBridge.log("socket 服务端绑定成功");
            XposedBridge.log("--------------------");
            Socket client = serverSocket.accept();
                clients.add(client);

                XposedBridge.log("有新的socket 连接");
                XposedBridge.log("--------------------");

//            while(true)
//            {
//                Socket client = serverSocket.accept();
//                clients.add(client);
//
//                XposedBridge.log("有新的socket 连接");
//                XposedBridge.log("--------------------");
//            }

        }catch(IOException ioe){
            XposedBridge.log("socket 服务端绑定异常" + ioe.getMessage());
            XposedBridge.log("--------------------");

        }finally{
            if(serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                    XposedBridge.log("服务端已关闭");
                    XposedBridge.log("--------------------");
                }catch(IOException e){
                    XposedBridge.log("关闭资源错误");
                    XposedBridge.log("--------------------");
                }finally {
                    serverSocket = null;
                    clients.clear();
                }

            }
        }

    }

    public void notifyClient(String data)
    {
        if(serverSocket == null)
        {
            XposedBridge.log("没有绑定服务端！");
            XposedBridge.log("--------------------");
            return;
        }

        for(Socket client : clients)
        {
            try
            {
                PrintWriter clientOut = new PrintWriter(client.getOutputStream());
                clientOut.println(data);
                clientOut.flush();
                XposedBridge.log("通知客户端成功");
                XposedBridge.log("--------------------");

            }catch(IOException e){
                XposedBridge.log("通知客户端失败" + e.getMessage());
                XposedBridge.log("--------------------");
                clients.remove(client);
            }
        }

    }

}
