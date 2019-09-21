package com.example.monitormethod.xposed;

import android.util.Log;
import android.view.MotionEvent;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventActivityHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        Log.i("LZH","ActivityDispatchTouchEvent-click: "+motionEvent.toString());
//        Log.i("LZH","click: x:"+motionEvent.getX()+" y: "+motionEvent.getY()+" action: "+motionEvent.getAction());
//        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
    }
}
