package com.example.a32960.xposedmoudle;

import android.content.ContentValues;
import android.content.Context;
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
        XposedBridge.log("打印testStr: " +testStr);
        testStr += loadPackageParam.packageName;
        XposedBridge.log("testStr 加上包名");
        if(loadPackageParam.packageName.equals("com.example.a32960.moudletest"))
        {
//            try
//            {
//                Class billAppClass = Class.forName("com.example.a32960.moudletest.MainActivity");
//                Object obj = billAppClass.getConstructor().newInstance();
//                Method billAppClass_billNotify = billAppClass.getMethod("billNotify", String.class);
//                billAppClass_billNotify.invoke(obj, "xposed");
//            }catch(ClassNotFoundException cnfe)
//            {
//                XposedBridge.log("找不到类moudletest MainActivity class");
//
//            }catch(NoSuchMethodException nsme)
//            {
//                XposedBridge.log("找不到方法moudletest MainActivity class billNotify method");
//            }
            obtainModuleTest(loadPackageParam);
            return;
        }

        if(loadPackageParam.packageName.equals("com.tencent.mm"))
        {
            XposedBridge.log("打开微信包");

            //hookBill(loadPackageParam.classLoader);
            return;
        }



    }


    private void obtainModuleTest(XC_LoadPackage.LoadPackageParam loadPackageParam)
    {
        final XposedProgram self = this;
        self.moduleTestClass = XposedHelpers.findClass("com.example.a32960.moudletest.MainActivity", loadPackageParam.classLoader);

        try
        {
            self.moduleTestClass_billNotify = moduleTestClass.getMethod("billNotify", String.class);
            moduleTestClassObj = XposedHelpers.newInstance(moduleTestClass);
            moduleTestClass_billNotify.invoke(moduleTestClassObj, "xposed");
//            XposedHelpers.findAndHookMethod(
//                    self.moduleTestClass,
//                    "onCreate",
//                    Bundle.class,
//                    new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    self.moduleTestClassObj =  param.thisObject;
//                    Toast.makeText((Context)(self.moduleTestClassObj), "module test activity oncreate", Toast.LENGTH_LONG).show();
//                    self.moduleTestClass_billNotify.invoke(self.moduleTestClassObj, testStr);
//                    XposedBridge.log("module test 创建了实例");
//                }
//            });

        }catch (NoSuchMethodException noSuchMethodException)
        {
            XposedBridge.log("找不到 billNotify method");
        }catch (Throwable t)
        {
            XposedBridge.log("hook method 失败");
        }

    }

    private void hookBill(final ClassLoader appClassLoader) {
        final XposedProgram self = this;
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", appClassLoader, "insert", String.class, String.class, ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        try {
                            ContentValues contentValues = (ContentValues) param.args[2];
                            String tableName = (String) param.args[0];
                            if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {

                                XposedBridge.log("不是message或者是空的消息：" + (String)param.args[0] + " : , " + (String)param.args[1] + " , " + param.args[2]);

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
                                String data = "<id>" + (billId ++ ) + "</id>" +
                                        "<time>" + dateString + "</time>" +
                                        "<topline>" + "<key>" + topLineKey + "</key>" + "<value>" + money + "</value>" + "</topline>" +
                                        "<line>" + "<key>" + line0Title + "</key>" + "<value>" + line0Msg + "</value>" + "</line>" +
                                        "<line>" + "<key>" + line1Title + "</key>" + "<value>" + line1Msg + "</value>" + "</line>";
                                //billCollections.add(data);
                                //ConnServerSocketThread connServerSocketThread = new ConnServerSocketThread(data);
                                //connServerSocketThread.start();


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
