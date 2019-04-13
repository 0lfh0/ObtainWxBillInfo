package com.example.a32960.moudletest;

import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnThread extends Thread {

    private String ipAddress;
    private int port;
    private Socket socket;
    private Handler handler;
    public ConnThread(Handler handler, String ipAddress, int port)
    {
        this.handler = handler;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void run()
    {

        try
        {
            socket = new Socket(ipAddress, port);
        }catch(IOException e) {
            Message failedConnMessage = new Message();
            failedConnMessage.what = -1;
            handler.sendMessage(failedConnMessage);
            e.printStackTrace();
            return;
        }
        System.out.println("成功连接服务器");
        Message successConnMessage = new Message();
        successConnMessage.what = 1;
        successConnMessage.obj = socket;
        handler.sendMessage(successConnMessage);
        DataInputStream input ;
        while (true) {
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                //读取服务器端数据

                input = new DataInputStream(socket.getInputStream());
                byte[] buffer;
                buffer = new byte[input.available()];
                if(buffer.length != 0){
                    //System.out.println("length="+buffer.length);
                    // 读取缓冲区
                    input.read(buffer);
                    // 转换字符串
                    String three = new String(buffer , "UTF-8");
                    Message message = new Message();
                    message.what = 2;
                    message.obj = three;
                    handler.sendMessage(message);

                    System.out.println("服务端： "+ three);
                }
            } catch (Exception e) {
                Message message = new Message();
                message.what = 4;
                handler.sendMessage(message);
                return;
            }
        }
    }
}
