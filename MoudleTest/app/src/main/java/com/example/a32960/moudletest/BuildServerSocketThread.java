package com.example.a32960.moudletest;

import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class BuildServerSocketThread extends Thread {
    private int port = 55533;
    private ServerSocket serverSocket;
    //List<Socket> clients;
    private Handler handler;
    public BuildServerSocketThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run()
    {
        try
        {

            serverSocket= new ServerSocket(port);
            //通知ui
            Message message1 = new Message();
            message1.what = 3;
            handler.sendMessage(message1);
            System.out.println("搭建服务端成功");
            //clients.add(client);
            while(true)
            {
                Socket client = serverSocket.accept();
                //通知ui
                Message message0 = new Message();
                message0.what = 1;
                handler.sendMessage(message0);
                System.out.println("有新的连接");

                // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = inputStream.read(bytes)) != -1) {
                    //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                    sb.append(new String(bytes, 0, len,"UTF-8"));
                }
                System.out.println("get message from client: " + sb.toString());
                //通知ui
                Message message = new Message();
                message.what = 2;
                message.obj = sb.toString();
                handler.sendMessage(message);

                //释放资源
                inputStream.close();
                client.close();
            }

        }catch(IOException ioe){
            System.out.println("服务端异常：" + ioe.getMessage());
            //通知ui
            Message message = new Message();
            message.what = -1;
            handler.sendMessage(message);
        }
    }
}
