package com.example.monitormethod.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.monitormethod.trackData.SystemDataCollection;
import com.example.monitormethod.util.LogWriter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用来播放Intent，输入，点击事件。
 * 抽取，传递页面信息
 */
public class LocalActivityReceiver extends BroadcastReceiver{
    private Activity selfActivity;
    public static final String intent = "intent";
    public static final String currentActivity = "currentActivity";
    public static final String openTargetActivityByIntent = "openTargetActivityByIntent";
    public static final String openTargetActivityByDeepLink = "openTargetActivityByDeepLink";
    public static final String INPUT_TEXT = "INPUT_TEXT";
    public static final String INPUT_EVENT = "INPUT_EVENT";

    public static final String GenerateIntentData = "GenerateIntentData";
    public static final String GenerateDeepLink = "GenerateDeepLink";

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_CLICK = "ON_CLICK";
    public static final String CREATE_DESK = "CREATE_DESK";
    public static final String FIND_SPECIFY = "FIND_SPECIFY";
    public static final String CLICK_DELETE = "CLICK_DELETE";

    public static final String obtainActivityText = "obtainActivityText";

    private String selfActivityName = "";
    private String selfPackageName;

    private int eventTime = 0;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();
        selfPackageName = activity.getPackageName();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case LocalActivityReceiver.WRITE_LOG:

                if(selfPackageName.contains("com.douban.movie")){
                    //设置LogWriter可以写入日志
                    LogWriter.turnWriteAble();
                }
                //模拟用户在豆瓣电影中搜索“哪吒”的操作
                if(selfActivityName.contains("NewSearchActivity")){
                    final TextView textView = selfActivity.findViewById(2131298054);
                    textView.setText("哪吒");
                    String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/methodLog.txt";
                    LogWriter.getInstance(fileName,"com.douban.movie").TempIsSetText = true;
                }
                break;
        }
    }
    private List<View> getRootViews(Activity activity){
        ArrayList<View> views = null;
        ClassLoader loader;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        try {
            Class clazz = activity.getClassLoader().loadClass("android.view.WindowManagerImpl");
            Field field = clazz.getDeclaredField("mGlobal");
            field.setAccessible(true);
            Object global = field.get(windowManager);

            Class globalClazz = global.getClass();
            field = globalClazz.getDeclaredField("mViews");
            field.setAccessible(true);
            views = (ArrayList<View>) field.get(global);
        } catch (ClassNotFoundException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        }
        return views;
    }

}
