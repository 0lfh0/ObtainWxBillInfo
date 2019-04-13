package com.example.a32960.moudletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import JSONJava.JSONObject;
import JSONJava.XML;

public class BillInfoReceiver extends BroadcastReceiver {

    private List<JSONObject> billInfo;
    private BillDetailListAdapter billDetailListAdapter ;
    public BillInfoReceiver( BillDetailListAdapter billDetailListAdapter, List<JSONObject> billInfo)
    {
        this.billDetailListAdapter = billDetailListAdapter;
        this.billInfo = billInfo;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String xmlData = intent.getStringExtra("xmlData");
        Toast.makeText(context,  "收到信息" , Toast.LENGTH_SHORT).show();
        JSONObject obj = XML.toJSONObject(xmlData);
        this.billInfo.add(obj);
        billDetailListAdapter.notifyDataSetChanged();
    }
}
