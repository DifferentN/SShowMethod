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
import com.example.monitormethod.util.ContextUtil;
import com.example.monitormethod.util.FileUtil;
import com.example.monitormethod.util.LogWriter;
import com.example.monitormethod.util.ViewUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
                ContextUtil.setContext(selfActivity);
                ContextUtil.setActivityName(showActivityName);
//                imitateExecution();
                break;
            case LocalActivityReceiver.WRITE_LOG:
                //com.douban.movie com.tencent.qqmusic com.ichi2.anki
                //com.jnzc.shipudaquan com.yongche.android com.xiangha
                //com.dangdang.buy2 cn.cuco com.zhangshangjianzhi.newapp
                //com.ss.android.ugc.aweme  yst.apk com.cqrenyi.huanyubrowser
                //com.yr.qmzs com.jrtd.mfxszq com.netease.pris com.wondertek.paper
                //com.infzm.ireader com.ifeng.news2 com.duxiaoman.umoney
                //com.boohee.food com.boohee.one com.boohee.food com.smartisan.notes
                if(selfPackageName.contains("com.example.musicplayer")){
                    //设置LogWriter可以写入日志
                    LogWriter.turnWriteAble();
//                    Log.i("LZH","send");
//                    sendRecordPermission();
                    if(selfActivityName.equals(showActivityName)){
                        final HashMap<String,String> pageContent = ViewUtil.capturePageContent(selfActivity);
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/APIPageContent/pageContent.txt";
                        FileUtil.writePageContent(filePath,pageContent);
                    }
                }
                break;
        }
    }
    private void sendRecordPermission(){
        Intent intent = new Intent();
        intent.setAction(RecordMethodLogReceiver.RECORD_SWITCH);
        selfActivity.sendBroadcast(intent);
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
