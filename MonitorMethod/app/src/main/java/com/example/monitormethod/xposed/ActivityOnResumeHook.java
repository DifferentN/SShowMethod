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

    private long curTime = 0,preTime = 0;
    private HashMap<String ,Long> onResumeHash;

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
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/andr.syt.xml";
//        Log.i("LZH","ExternalStorageDirectory "+path);
//        File file = new File(path);
//        try{
//            if(!file.exists()){
//                Log.i("LZH","文件不存在 "+path);
//            }else{
//                Log.i("LZH","文件存在 "+path);
//            }
//        }catch (Exception e){
//
//        }
//        Log.i("LZH","before resume "+componentName.getClassName());
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
