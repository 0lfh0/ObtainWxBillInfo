package com.example.a32960.moudletest;

import android.content.ContentValues;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import JSONJava.JSONArray;
import JSONJava.JSONObject;


public class BillDetailItemHolder {

    View convertView;

    public BillDetailItemHolder(View view)
    {
        this.convertView = view;
    }

    public void init(JSONObject data)
    {
        TextView billDetailTimeView = (TextView)convertView.findViewById(R.id.bill_detail_time_view);
        TextView billItemMoneyView = (TextView)convertView.findViewById(R.id.bill_item_money_view);
        TextView billItemMoneyTypeView = (TextView)convertView.findViewById(R.id.bill_item_money_type_view);

        TextView billDetailTotalTitleView = (TextView)convertView.findViewById(R.id.bill_detail_total_title_view);
        TextView billDetailTotalView = (TextView)convertView.findViewById(R.id.bill_detail_total_view);

        TextView billDetailRemarkTitleView = (TextView)convertView.findViewById(R.id.bill_detail_remark_title_view);
        TextView billDetailRemarkView = (TextView)convertView.findViewById(R.id.bill_detail_remark_view);

        //时间
        billDetailTimeView.setText(data.getString("time"));

        JSONObject topLine = data.getJSONObject("topline");
        //收款金额
        billItemMoneyTypeView.setText(topLine.getString("key"));
        //￥0.01
        billItemMoneyView.setText("" + topLine.getFloat("value"));

        JSONArray line = data.getJSONArray("line");
        JSONObject line0Detail = line.getJSONObject(0);
        JSONObject line1Detail = line.getJSONObject(1);

        //汇总
        billDetailTotalTitleView.setText(line0Detail.getString("key"));
        billDetailTotalView.setText(line0Detail.getString("value"));

        //备注
        billDetailRemarkTitleView.setText(line1Detail.getString("key"));
        billDetailRemarkView.setText(line1Detail.getString("value"));


    }
}
