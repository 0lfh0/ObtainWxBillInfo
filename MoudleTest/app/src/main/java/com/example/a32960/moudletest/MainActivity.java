package com.example.a32960.moudletest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import JSONJava.JSONObject;
import JSONJava.XML;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private List<JSONObject> billInfo = new ArrayList<JSONObject>();
    private BillDetailListAdapter billDetailListAdapter ;

    private String testStr="mainactivity11111111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long time = 1555067194;
        Date currentTime = new Date();
        currentTime.setTime(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = formatter.format(currentTime);
        System.out.println("时间时间：" +dateString);
        testStr = "main activity 2222222";
        billNotify("本地进程");
//        ListView billDetailList = (ListView)findViewById(R.id.bill_detail_list);
//
//        billDetailListAdapter = new BillDetailListAdapter(this, billInfo);
//        billDetailList.setAdapter(billDetailListAdapter);
//        Toast.makeText(this, "开始搭建服务器...", Toast.LENGTH_SHORT).show();
//        buildServerSocket();
    }

    private void buildServerSocket()
    {
        final Context context = this;
        Handler myHandler = new Handler() {
            public void handleMessage(Message msg) {
                int type = msg.what;
                switch(type)
                {
                    case -1:
                        Toast.makeText(context, "服务器异常，已停止!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //已连接服务器
                        Toast.makeText(context, "有新的连接!", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //接受信息
                        Toast.makeText(context, "有新的数据!", Toast.LENGTH_SHORT).show();
                        String xmlData = (String)msg.obj;
                        JSONObject billInfoJson = XML.toJSONObject(xmlData);
                        billInfo.add(billInfoJson);
                        billDetailListAdapter.notifyDataSetChanged();
                        break;

                    case 3:
                        Toast.makeText(context, "搭建服务端成功!", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        };
        BuildServerSocketThread buildServerSocketThread = new BuildServerSocketThread(myHandler);
        buildServerSocketThread.start();
    }

    public void billNotify(String xmlData)
    {
        Toast.makeText(this, xmlData + ": "+ testStr, Toast.LENGTH_SHORT).show();
//        JSONObject billInfoJson = XML.toJSONObject(xmlData);
//        billInfo.add(billInfoJson);
//        billDetailListAdapter.notifyDataSetChanged();
    }



}
