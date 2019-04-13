package com.example.a32960.moudletest;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import JSONJava.JSONObject;


public class BillDetailListAdapter extends BaseAdapter
{

    private Context context;
    private List<JSONObject> billInfo;
    private LayoutInflater inflater;
    public BillDetailListAdapter(Context context, List<JSONObject> billInfo)
    {
        this.context = context;
        this.billInfo = billInfo;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return billInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return billInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BillDetailItemHolder billDetailItemHolder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.bill_detail, parent, false);
            billDetailItemHolder = new BillDetailItemHolder(convertView);
            convertView.setTag(billDetailItemHolder);

        }else{
            billDetailItemHolder = (BillDetailItemHolder)convertView.getTag();
        }


        billDetailItemHolder.init(billInfo.get(position));

        return billDetailItemHolder.convertView;
    }
}
