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

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_RESUME = "ON_RESUME";
    public static final String RESUME_ACTIVITY = "RESUME_ACTIVITY";

    public static final String obtainActivityText = "obtainActivityText";

    private String selfActivityName = "";
    private String showActivityName = "";
    private String selfPackageName;

    public static boolean imitateStart = false;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case ON_RESUME:
                showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
//                imitateExecution();
                break;
            case LocalActivityReceiver.WRITE_LOG:
                //com.douban.movie com.tencent.qqmusic
                //com.jnzc.shipudaquan com.yongche.android
                if(selfPackageName.contains("cn.cuco")){
                    //设置LogWriter可以写入日志
                    LogWriter.turnWriteAble();
                }
//                click();
//                imitateStart = true;
//                imitateExecution();
                break;
        }
    }
    private void imitateExecution(){
        Log.i("LZH","imitateStart: "+imitateStart);
        if(!imitateStart){
            return;
        }
        if(selfActivityName.equals(showActivityName)&&selfActivityName.equals("com.douban.movie.activity.MainActivity")){
            List<String> list = new ArrayList<>();
            list.add("电影");
            list.add("电视剧");
            View view = getTargetView(list);
            imitateClick(view);
        }else if(selfActivityName.equals(showActivityName)&&selfActivityName.equals("com.douban.frodo.search.activity.NewSearchActivity")){
            List<String> list = new ArrayList<>();
            list.add("电影");
            list.add("电视剧");
//            View view = selfActivity.findViewById(2131298054);
//            imitateClick(view);
//
//            ((TextView)view).setText("罗小黑战记");
        }
    }
    private void imitateClick(View view){
        int clickPos[] = new int[2];
        view.getLocationInWindow(clickPos);
        clickPos[0]+=view.getWidth()/2;
        clickPos[1]+=view.getHeight()/2;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = clickPos[0];
        int y = clickPos[1];
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        view.getRootView().dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        view.getRootView().dispatchTouchEvent(motionEvent);
    }
    private View getTargetView(List<String> texts){
        View decor = selfActivity.getWindow().getDecorView();
        ViewGroup viewGroup;
        View temp = null,child = null;
        List<View> queue = new ArrayList<>();
        queue.add(decor);
        while(!queue.isEmpty()){
            temp = queue.remove(0);
            if(temp instanceof  ViewGroup){
                viewGroup = (ViewGroup) temp;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    queue.add(viewGroup.getChildAt(i));
                }
            }else if(check(temp,texts)){
                return temp;
            }
        }
        return null;
    }
    private boolean check(View view,List<String> texts){
        if(!(view instanceof TextView)){
            return false;
        }
        String viewContent = ((TextView) view).getText().toString();
        for(String item:texts){
            if(!viewContent.contains(item)){
                return false;
            }
        }
        return true;
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
