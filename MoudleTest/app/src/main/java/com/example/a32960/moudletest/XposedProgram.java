package com.example.a32960.moudletest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import JSONJava.JSONArray;
import JSONJava.JSONObject;
import JSONJava.XML;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedProgram implements IXposedHookLoadPackage {

    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable
    {
        if(loadPackageParam.packageName.equals("com.tencent.mm"))
        {
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

                }
            });
        } catch (Throwable t) {
            XposedBridge.log("获取上下文出错");
            wxContext = null;
        }
    }

    private void hookBill(final ClassLoader appClassLoader)
    {

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", appClassLoader, "insert", String.class, String.class, ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {

                        try {
                            ContentValues contentValues = (ContentValues) param.args[2];
                            String tableName = (String) param.args[0];
                            if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {
                                return;
                            }
                            Integer type = contentValues.getAsInteger("type");
                            if (null == type) {
                                return;
                            }

//                            XposedBridge.log("\n\n\n遍历content里的信息：");
//                            for(Map.Entry<String, Object> item : contentValues.valueSet())
//                            {
//                                XposedBridge.log(item.getKey() + " , " + item.getValue().toString());
//                            }
//                            XposedBridge.log("遍历content里的信息完成\n\n\n");


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
                                String data = "<time>" + dateString + "</time>" +
                                        "<topline>" + "<key>" + topLineKey + "</key>" + "<value>" + money + "</value>" + "</topline>" +
                                        "<line>" + "<key>" + line0Title + "</key>" + "<value>" + line0Msg + "</value>" + "</line>" +
                                        "<line>" + "<key>" + line1Title + "</key>" + "<value>" + line1Msg + "</value>" + "</line>";
                                //把信息广播出去
                                Intent intent = new Intent("com.example.a32960.moudletest");
                                intent.putExtra("xmlData", data);
                                wxContext.sendBroadcast(intent);

                            }
                        } catch (Exception e) {
                            XposedBridge.log("获取信息出错： "+ e.getMessage());
                        }
                    }

                });
    }
}
