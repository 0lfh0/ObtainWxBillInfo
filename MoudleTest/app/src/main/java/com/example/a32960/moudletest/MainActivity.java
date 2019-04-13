package com.example.a32960.moudletest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
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

    private List<JSONObject> billInfo = new ArrayList<JSONObject>();
    private BillDetailListAdapter billDetailListAdapter ;

    private BillInfoReceiver receiver;
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

        ListView billDetailList = (ListView)findViewById(R.id.bill_detail_list);

        billDetailListAdapter = new BillDetailListAdapter(this, billInfo);
        billDetailList.setAdapter(billDetailListAdapter);
        //注册广播
        IntentFilter filter = new IntentFilter("com.example.a32960.moudletest");
        receiver = new BillInfoReceiver( billDetailListAdapter, billInfo);
        registerReceiver(receiver, filter);
    }

    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
