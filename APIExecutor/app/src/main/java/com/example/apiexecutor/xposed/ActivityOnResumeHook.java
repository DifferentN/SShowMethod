package com.example.apiexecutor.xposed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.example.apiexecutor.core.CoordinatorReceiver;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;

public class ActivityOnResumeHook extends XC_MethodHook {

    private long curTime = 0,preTime = 0;
    private HashMap<String ,Long> onResumeHash;
    private boolean hasSend = false;

    public ActivityOnResumeHook() {
        super();
        onResumeHash = new HashMap<>();
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
        Activity activity = (Activity) param.thisObject;
//        ComponentName componentName = activity.getComponentName();
        ComponentName componentName = activity.getComponentName();
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Activity activity = (Activity) param.thisObject;
        ComponentName componentName = activity.getComponentName();
        String activityName = componentName.getClassName();
        Log.i("LZH","after resume "+componentName.getClassName());

        Intent intent = new Intent();
        intent.setAction(CoordinatorReceiver.ON_RESUME);
        intent.putExtra(CoordinatorReceiver.RESUME_ACTIVITY,activityName);
        activity.sendBroadcast(intent);
        if(!hasSend){
            hasSend = true;
        }
    }

}
