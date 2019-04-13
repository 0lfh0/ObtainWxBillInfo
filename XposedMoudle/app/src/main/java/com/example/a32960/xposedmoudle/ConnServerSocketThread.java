package com.example.a32960.xposedmoudle;

import android.os.Handler;

import java.io.OutputStream;
import java.net.Socket;

import de.robv.android.xposed.XposedBridge;

public class ConnServerSocketThread extends Thread {

    private String ipAddress;
    private int port;
    private String xmlData;
    public ConnServerSocketThread(String xmlData)
    {
        this.xmlData = xmlData;
        this.ipAddress = "127.0.0.1";
        this.port = 55533;
    }

    @Override
    public void run()
    {
        try
        {
            XposedBridge.log("开始连接服务器");
            Socket socket = new Socket(ipAddress, port);
            XposedBridge.log("连接服务器成功");
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(xmlData.getBytes("UTF-8"));
            outputStream.close();
            socket.close();
            XposedBridge.log("关闭客户端");
        }catch(Exception e)
        {
            XposedBridge.log("连接服务器错误：" + e.getMessage());
        }


    }
}
