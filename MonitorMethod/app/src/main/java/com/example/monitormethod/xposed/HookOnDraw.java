package com.example.monitormethod.xposed;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.monitormethod.receive.LocalActivityReceiver;
import com.example.monitormethod.trackData.MyTextWatcher;
import com.example.monitormethod.util.LogWriter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class HookOnDraw extends XC_MethodHook {
    private LogWriter logWriter;
    private String fileName = "APIFile/methodLog.txt";
    public HookOnDraw(String packageName){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,packageName);
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        View view = (View) param.thisObject;
        Activity activity = getActivity(view);
        if(view!=null&&view instanceof TextView){
            ((TextView) view).addTextChangedListener(new MyTextWatcher(view));
        }
        if(!LocalActivityReceiver.imitateStart){
            return;
        }
        if(view instanceof TextView){
            TextView textView = (TextView) view;
            String text = textView.getText().toString();
            if(text.equals("罗小黑战记")&&logWriter.num==0){
                if(textView.getId()==2131298054){
                    //跳过搜索框
                    return;
                }
                imitateClick(view,activity);
                logWriter.num++;
            }else if(text.equals("影视")&&logWriter.num==1){
                imitateClick(view,activity);
                logWriter.num++;
            }else if(text.contains("罗小黑战记")&&text.contains("2019")&&logWriter.num==2){
                imitateClick(view,activity);
                Log.i("LZH","罗小黑战记(2019)");
                logWriter.num++;
            }
        }
    }
    private boolean checkAdd(TextView view){
        Class clazz = view.getClass();
        try {
            Field field = clazz.getDeclaredField("mListeners");
            field.setAccessible(true);
            ArrayList<TextWatcher> list = (ArrayList<TextWatcher>) field.get(view);
            Object listener;
            if(list==null){
                return false;
            }
            for(int i=0;i<list.size();i++){
                listener = list.get(i);
                if(listener instanceof MyTextWatcher){
//                    Log.i("LZH","有TextWatcher");
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Log.i("LZH","无TextWatcher");
        return false;
    }
    private void imitateClick(View view, Activity activity){
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
        activity.getWindow().getDecorView().dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        activity.getWindow().getDecorView().dispatchTouchEvent(motionEvent);
    }
    private Activity getActivity(View view){
        if(view!=null){
            Context context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    return (Activity)context;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        return null;
    }
}
