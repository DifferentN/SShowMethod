package com.example.apiexecutor.xposed;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.apiexecutor.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventActivityHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        if (param.thisObject instanceof View){
            Log.i("LZH","ViewId: "+((View)param.thisObject).getId()+" viewClassName: "+param.thisObject.getClass().getSimpleName());
            Log.i("LZH","viewPath: "+ ViewUtil.getViewPath((View) param.thisObject));
        }
        Log.i("LZH"," click: "+motionEvent.toString());
//        Log.i("LZH","click: x:"+motionEvent.getX()+" y: "+motionEvent.getY()+" action: "+motionEvent.getAction());
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
    }
}
