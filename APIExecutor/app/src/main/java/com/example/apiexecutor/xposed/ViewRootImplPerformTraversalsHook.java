package com.example.apiexecutor.xposed;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.example.apiexecutor.receive.LocalActivityReceiver;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;

public class ViewRootImplPerformTraversalsHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Context context = null;
        Class clazz = param.thisObject.getClass();
        Field mContext = clazz.getDeclaredField("mContext");
        mContext.setAccessible(true);
        context = (Context) mContext.get(param.thisObject);
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.DRAW_OVER);
        if(context!=null){
            context.sendBroadcast(intent);
//            Log.i("LZH","send performTraversals");
        }else {
//            Log.i("LZH","context is null");
        }


    }
}
