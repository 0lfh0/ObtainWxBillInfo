package com.example.a32960.xposedmoudle;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import JSONJava.JSONArray;
import JSONJava.JSONObject;
import JSONJava.XML;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedProgram implements IXposedHookLoadPackage {

    private BuildServerSocketThread serverSocketThread;
    private List<String> billCollections;
    private int billId = 0;
    private ServerSocket serverSocket;

    private Class<?> moduleTestClass;
    private Object moduleTestClassObj;
    private Method moduleTestClass_billNotify;

    private String testStr = "hello world";
    private Context wxContext;
    public XposedProgram()
    {
        XposedBridge.log("创建Xposed Program实例");
        XposedBridge.log("--------------------");
        XposedBridge.log("开启server socket");
        XposedBridge.log("--------------------");
        XposedBridge.log(" XposedProgram() 当前线程：" + Thread.currentThread().getId());
        XposedBridge.log("--------------------");
        //billCollections = new ArrayList<String>();
//        BuildServerSocketThread serverSocketThread = new BuildServerSocketThread();
//        serverSocketThread.start();

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable
    {


        if(loadPackageParam.packageName.equals("com.tencent.mm"))
        {
            XposedBridge.log("打开微信包");
            hookWxContext(loadPackageParam.classLoader);
            hookBill(loadPackageParam.classLoader);
            return;
        }



    }

    private void hookWxContext(final ClassLoader appClassLoader)
    {
        try {
            Class<?> ContextClass = XposedHelpers.findClass("android.content.ContextWrapper", appClassLoader);
            XposedHelpers.findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (wxContext != null)
                        return;
                    wxContext = (Context) param.getResult();
                    Toast.makeText(wxContext, "hello "+ wxContext.getPackageName(), Toast.LENGTH_LONG).show();
                    XposedBridge.log("得到上下文");

                }
            });
        } catch (Throwable t) {
            XposedBridge.log("获取上下文出错");
            XposedBridge.log(t);
            wxContext = null;
        }
    }

    private void hookBill(final ClassLoader appClassLoader)
    {
        final String data = "<id>" + 12345 + "</id>" +
                "<time>" + "12:00" + "</time>" +
                "<topline>" + "<key>" + "收款金额" + "</key>" + "<value>" + "100" + "</value>" + "</topline>" +
                "<line>" + "<key>" + "汇总" + "</key>" + "<value>" + "测试测试" + "</value>" + "</line>" +
                "<line>" + "<key>" + "备注" + "</key>" + "<value>" + "测试测试" + "</value>" + "</line>";

        final Intent intent = new Intent("com.example.a32960.moudletest");
        intent.putExtra("xmlData", data);
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", appClassLoader, "insert", String.class, String.class, ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        //把信息广播出去
                        XposedBridge.log("把信息广播出去");
                        wxContext.sendBroadcast(intent);

                        try {
                            ContentValues contentValues = (ContentValues) param.args[2];
                            String tableName = (String) param.args[0];
                            if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {

                                XposedBridge.log("不是message或者是空的消息");
                                return;
                            }
                            Integer type = contentValues.getAsInteger("type");
                            if (null == type) {
                                return;
                            }

                            XposedBridge.log("\n\n\n遍历content里的信息：");
                            for(Map.Entry<String, Object> item : contentValues.valueSet())
                            {
                                XposedBridge.log(item.getKey() + " , " + item.getValue().toString());
                            }
                            XposedBridge.log("遍历content里的信息完成\n\n\n");


                            if (type == 318767153) {
                                String contentStr = contentValues.getAsString("content");
                                JSONObject msg = XML.toJSONObject(contentStr);
                                XposedBridge.log("收款信息(json)："+msg);

                                JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");
                                //获取时间
                                long time = mmreader.getJSONObject("template_header").getLong("pub_time");
                                Date currentTime = new Date(time);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String dateString = formatter.format(currentTime);


                                //获取收款明细
                                JSONObject billDetail = mmreader.getJSONObject("template_detail").getJSONObject("line_content");

                                //获取收款标题、金额等
                                JSONObject topLine = billDetail.getJSONObject("topline");
                                //收款标题
                                String topLineKey = topLine.getJSONObject("key").getString("word");
                                //收款金额
                                String topLineValue = topLine.getJSONObject("value").getString("word");
                                float money = Float.parseFloat(topLineValue.replace("￥", ""));

                                //获取汇总、备注等信息
                                JSONArray line = billDetail.getJSONObject("lines").getJSONArray("line");
                                //获取汇总
                                JSONObject line0Detail = line.getJSONObject(0);
                                String line0Title = line0Detail.getJSONObject("key").getString("word");
                                String line0Msg = line0Detail.getJSONObject("value").getString("word");
                                //获取备注
                                JSONObject line1Detail = line.getJSONObject(1);
                                String line1Title = line1Detail.getJSONObject("key").getString("word");
                                String line1Msg = line1Detail.getJSONObject("value").getString("word");

                                XposedBridge.log("\n\n\n获取到时间：" + dateString  );
                                XposedBridge.log(topLineKey + money);
                                XposedBridge.log(line0Title + " " + line0Msg);
                                XposedBridge.log(line1Title + " " + line1Msg);

                                XposedBridge.log("开始通知客户端");
                                XposedBridge.log("--------------------");
//                                String data = "<id>" + (billId ++ ) + "</id>" +
//                                        "<time>" + dateString + "</time>" +
//                                        "<topline>" + "<key>" + topLineKey + "</key>" + "<value>" + money + "</value>" + "</topline>" +
//                                        "<line>" + "<key>" + line0Title + "</key>" + "<value>" + line0Msg + "</value>" + "</line>" +
//                                        "<line>" + "<key>" + line1Title + "</key>" + "<value>" + line1Msg + "</value>" + "</line>";
                                //billCollections.add(data);
                                //ConnServerSocketThread connServerSocketThread = new ConnServerSocketThread(data);
                                //connServerSocketThread.start();
                                //把信息广播出去
//                                Context context = (Context)param.thisObject;
//                                Intent intent = new Intent("com.example.a32960.moudletest");
//                                intent.putExtra("xmlData", data);
//                                context.sendBroadcast(intent);

                            }
                        } catch (Exception e) {
                            XposedBridge.log("获取信息出错： "+ e.getMessage());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                    }
                });
    }



}
