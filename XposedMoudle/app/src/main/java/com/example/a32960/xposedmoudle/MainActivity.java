package com.example.a32960.xposedmoudle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;



import JSONJava.JSONObject;
import JSONJava.XML;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String xmlStr = "<sex>man</sex><ss>abc</ss>";
        JSONObject jjj = null;
        try
        {
            jjj = XML.toJSONObject(xmlStr);

        }catch (Throwable t)
        {
            Toast.makeText(this, "xml error", Toast.LENGTH_LONG).show();
        }

        if(jjj != null)
        {
            Toast.makeText(this, jjj.toString(4), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "null", Toast.LENGTH_LONG).show();
        }



    }
}
