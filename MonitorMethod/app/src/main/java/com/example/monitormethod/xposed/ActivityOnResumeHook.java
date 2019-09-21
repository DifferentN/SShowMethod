package com.example.monitormethod.xposed;

import android.app.Activity;
import android.content.ComponentName;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monitormethod.trackData.MyTextWatcher;

import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;

public class ActivityOnResumeHook extends XC_MethodHook {

    public ActivityOnResumeHook() {
        super();
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Activity activity = (Activity) param.thisObject;
        ComponentName componentName = activity.getComponentName();
        String activityName = componentName.getClassName();
        Log.i("LZH","after resume "+componentName.getClassName());
        Log.i("LZH","intent: "+activity.getIntent().toURI());
    }

}
