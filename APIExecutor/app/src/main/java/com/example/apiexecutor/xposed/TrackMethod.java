package com.example.apiexecutor.xposed;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.apiexecutor.receive.LocalActivityReceiver;
import com.example.apiexecutor.trackData.ActivityUtil;
import com.example.apiexecutor.trackData.ObjectPool;

import de.robv.android.xposed.XC_MethodHook;


public class TrackMethod extends XC_MethodHook {
    Class pclazz[];
    private Activity activity;
    private ObjectPool objectPool;
    private String fileName = "methodLog.txt";
    public TrackMethod(Class pclazz[],String packageName){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        this.pclazz = pclazz;
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
        objectPool.addGlobalHookParam(param);

        int menuId = -1;

        Class clazz = param.method.getDeclaringClass();
        String info = clazz.getName()+".";
        info+=param.method.getName();
        info+="(";
        View view;
        for(int i=0;i<pclazz.length;i++){
            info+=pclazz[i].getName();
            if(param.args[i]!=null){
                info+="/"+param.args[i].getClass().getName();
            }
            if(param.args[i] instanceof  View){
                view = (View) param.args[i];
                info+="ViewId: "+view.getId();
            }else if(param.args[i] instanceof MenuItem){
                menuId = ((MenuItem)param.args[0]).getItemId();
                info+=menuId;
            }
            if(i!=pclazz.length-1){
                info+=",";
            }
        }
        info+=")";
//        Log.i("LZH-Method","before: "+info);
    }


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);

        Class clazz = param.method.getDeclaringClass();
        sendMessage(clazz.getName(),param.method.getName());
        String info = clazz.getName()+".";
        info+=param.method.getName();
        info+="(";
        for(int i=0;i<pclazz.length;i++){

            info+=pclazz[i].getName();
            if(i!=pclazz.length-1){
                info+=",";
            }
        }
        info+=")";
//        Log.i("LZH-Method","after: "+info);
    }
    private void sendMessage(String className,String methodName){
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.AFTER_METHOD);
        intent.putExtra(LocalActivityReceiver.METHOD_NAME,className+"/"+methodName);
        String name = "com.douban.amonsul.core.CrashStatSender/a";
        if(!(className+"/"+methodName).equals(name)){
            return;
        }
        activity = ActivityUtil.getActivityInstance();
        if(activity==null){
            return;
        }
        activity.sendBroadcast(intent);
    }
}
