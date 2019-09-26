package com.example.apiexecutor.xposed;

import com.example.apiexecutor.trackData.MethodTrackPool;

import de.robv.android.xposed.XC_MethodHook;

public class ActivityOnDraw extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
        MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
        methodTrackPool.LaunchUserAction();
    }
}
