package com.example.monitormethod.xposed;

import android.util.Log;
import android.view.View;

import com.example.monitormethod.trackData.TrackOnClickListener;

import de.robv.android.xposed.XC_MethodHook;

public class FindViewByIdHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
//        Log.i("LZH","before findViewById: ");
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
        int id = (int) param.args[0];
        View view = (View) param.getResult();

        TrackOnClickListener trackOnClickListener = TrackOnClickListener.getInstance();
        if(view != null){
            trackOnClickListener.add(view ,id);
        }
    }
}
