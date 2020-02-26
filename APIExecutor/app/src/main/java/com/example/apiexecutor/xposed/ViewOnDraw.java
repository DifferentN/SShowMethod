package com.example.apiexecutor.xposed;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;

import com.example.apiexecutor.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class ViewOnDraw extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        boolean res = (boolean) param.getResult();
        boolean flag = (boolean) param.getResult();
        Log.i("LZH","state: "+flag);

        
    }
}
