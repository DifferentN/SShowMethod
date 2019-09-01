package com.example.apiexecutor.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.receive.LocalActivityReceiver;

import java.net.URISyntaxException;

public class Coordinator {
    private JSONObject taskJSON;
    private String packageName,intentStr,methods;
    private int taskPointer = 0;
    public void setTaskJSON(String jsonString){
        taskJSON = JSONObject.parseObject(jsonString);
    }
    public void startApp(String packageName, Context context){
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(LaunchIntent);
    }
    public void sendIntentToOpenActivity(Context context){
        Intent targetIntent = null;
        try {
            targetIntent = Intent.parseUri(getIntentString(),0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(targetIntent==null){
            Log.i("LZH","targetInent is null");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.openTargetActivityByIntent);
        intent.putExtra(LocalActivityReceiver.TARGET_INTENT,targetIntent);
        context.sendBroadcast(intent);
    }
    public String getPackageName(){
        packageName = taskJSON.getString("packageName");
        return packageName;
    }
    public String getIntentString(){
        intentStr = taskJSON.getString("intent");
        return intentStr;
    }
    public String getMethods(){
        methods = taskJSON.getJSONArray("methods").toJSONString();
        return methods;
    }
}
