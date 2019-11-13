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
import com.example.apiexecutor.trackData.MethodTrackPool;
import com.example.apiexecutor.trackData.ObjectPool;

import de.robv.android.xposed.XC_MethodHook;


public class TrackMethod extends XC_MethodHook {
    Class pclazz[];
    private Activity activity;
    private ObjectPool objectPool;
    private String fileName = "methodLog.txt";
    private MethodTrackPool methodTrackPool;
    public TrackMethod(Class pclazz[],String packageName){
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
//        this.pclazz = pclazz;
        methodTrackPool = MethodTrackPool.getInstance();
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param)  {
        if(param.method.getName().contains("getLayoutDirection")){
            if(param.method.getDeclaringClass().getName().contains("com.douban.frodo.baseproject.view.flowlayout.DouFlowLayout")){
                Log.i("LZH","com.douban.frodo.baseproject.view.flowlayout.DouFlowLayout/getLayoutDirection");
            }
        }
        if(param.method.getName().contains("onStop")){
            if(param.method.getDeclaringClass().getName().contains("com.douban.frodo.baseproject.activity.BaseActivity")){
                Log.i("LZH","com.douban.frodo.baseproject.activity.BaseActivity/onStop");
            }
        }

        String callerName = param.method.getDeclaringClass().getName();
        String methodName = param.method.getName();
        String message = "before: "+callerName+"/"+methodName;
        if(Thread.currentThread().getId()==1){
            methodTrackPool.sendMessage(message);
        }
//        Log.i("LZH-Method",message);
    }


    @Override
    protected void afterHookedMethod(MethodHookParam param){
        String callerName = param.method.getDeclaringClass().getName();
        String methodName = param.method.getName();
        String message = "after: "+callerName+"/"+methodName;
        if(Thread.currentThread().getId()==1){
            methodTrackPool.sendMessage(message);
        }
//        Log.i("LZH-Method","after: "+message);
    }
}
