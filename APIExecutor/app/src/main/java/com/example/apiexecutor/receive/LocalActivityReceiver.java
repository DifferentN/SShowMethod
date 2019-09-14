package com.example.apiexecutor.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.core.CoordinatorReceiver;
import com.example.apiexecutor.core.MethodExecutor;
import com.example.apiexecutor.test.MyRunnable;
import com.example.apiexecutor.trackData.MethodTrackPool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主要用来播放Intent，输入，点击事件。
 * 抽取，传递页面信息
 */
public class LocalActivityReceiver extends BroadcastReceiver implements CallBackToServer{
    private Activity selfActivity;
    public static final String intent = "intent";
    public static final String currentActivity = "currentActivity";
    public static final String openTargetActivityByIntent = "openTargetActivityByIntent";
    public static final String openTargetActivityByDeepLink = "openTargetActivityByDeepLink";
    public static final String INPUT_TEXT = "INPUT_TEXT";
    public static final String TEXT_KEY = "TEXT_KEY";
    public static final String INPUT_EVENT = "INPUT_EVENT";
    public static final String EVENTS = "EVENTS";
    public static final String DEEP_LINK_KEY = "DEEP_LINK_KEY";
    public static final String DEEP_LINK = "DEEP_LINK";


    public static final String GenerateIntentData = "GenerateIntentData";
    public static final String GenerateDeepLink = "GenerateDeepLink";

    public static final String fromActivityPlay = "fromActivityPlay";
    public static final String tarPackageName = "tarPackageName";
    public static final String TARGET_INTENT = "targetIntent";

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_CLICK = "ON_CLICK";
    public static final String CREATE_DESK = "CREATE_DESK";
    public static final String FIND_SPECIFY = "FIND_SPECIFY";
    public static final String CLICK_DELETE = "CLICK_DELETE";

    public static final String AFTER_METHOD = "AFTER_METHOD";
    public static final String METHOD_NAME = "METHOD_NAME";

    public static final String obtainActivityText = "obtainActivityText";

    private String showActivityName = "";
    private String selfActivityName = "";
    private String selfPackageName;
    private String selfAppName;
    private String curPackageName = "";
    private String curAppName;
    private String textKey ;
    private byte[] eventBytes;
    private String startActivityFrom;
    private String startActivityFromApp;
    private String selfpackageName;

    private int clickTime = 0;
    public long curTime,preTime;
    private boolean isSetText = false;

    private MethodExecutor methodExecutor;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();
//        selfAppName = AppUtil.getAppName(selfActivity);
        selfPackageName = activity.getPackageName();
        methodExecutor = new MethodExecutor();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case CoordinatorReceiver.ON_RESUME:
                showActivityName = intent.getStringExtra(CoordinatorReceiver.RESUME_ACTIVITY);
                Log.i("LZH","showActivity: "+showActivityName);
//                curPackageName = (String)bundle.get("curPackage");
//                curAppName = (String) bundle.get("curApp");
                break;
            case LocalActivityReceiver.openTargetActivityByIntent:
                Intent tarIntent = intent.getParcelableExtra(LocalActivityReceiver.TARGET_INTENT);
//                startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityStart);
//                startActivityFromApp = intent.getStringExtra(LocalActivityReceiver.fromAppStart);
//                Log.i("LZH","self: "+selfAppName+"start: "+startActivityFromApp);
                if(showActivityName.compareTo(selfActivityName)!=0){
                    break;
                }
                selfActivity.startActivity(tarIntent);
                break;
            case LocalActivityReceiver.AFTER_METHOD:
                //2131298054
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivity.getClass().getName().contains("com.douban.frodo.search.activity.NewSearchActivity")){
                    break;
                }
                if(isSetText){
                    IsEndMethod();
                }
                break;
            case LocalActivityReceiver.INPUT_TEXT:
                //2131298054 com.douban.frodo.search.activity.NewSearchActivity
//                amodule.activity.HomeSearch  2131296313
                if(selfActivityName.contains("NewSearchActivity")){
//                    doClick(0);
                    MethodTrackPool.getInstance().clearRunTimeRecord();
                    final TextView textView = selfActivity.findViewById(2131298054);
                    textView.setText("哪吒之魔童降世");
//                    textView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText("哪吒之魔童降世");
//                        }
//                    },1000);
//                    textView.setText("上海堡垒");
//                    textView.setText("追龙");
//                    textView.setText("锅盔");
                    isSetText = true;
//                    doClick(clickTime);
                }
                break;
        }
    }

    private void IsEndMethod() {
//        curTime = System.currentTimeMillis();
//        selfActivity.getWindow().getDecorView().postDelayed(new MyRunnable(this,curTime,clickTime),1000);
        doClick(clickTime);
    }

    public void doClick(int eTime) {
        if(eTime!=clickTime){
            return;
        }
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = 72;
        int y = 184;
        int metaState = 0;
        clickTime++;
        if(eTime==0){
            Log.i("LZH","doclick");
            x = 193;
            y = 283;
//            x = 663;
//            y = 75;
            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);
            action = MotionEvent.ACTION_UP;
            motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);

        }else if(eTime==1){
            x = 140;
            y = 134;
//            x = 322;
//            y = 186;
            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);
            action = MotionEvent.ACTION_UP;
            motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);

        }else if(eTime==2){
            x = 484;
            y = 245;
            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);
            action = MotionEvent.ACTION_UP;
            motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            selfActivity.dispatchTouchEvent(motionEvent);
        }

    }

    private void executeMethods(String methods,Activity activity){
        JSONArray array = JSONArray.parseArray(methods);
        JSONObject methodJson;
        for(int i=0;i<array.size();i++){
            methodJson = array.getJSONObject(i);
            methodExecutor.executeMethod(methodJson,activity);
        }
    }
    @Override
    public String getContent() {
        return content;
    }

}