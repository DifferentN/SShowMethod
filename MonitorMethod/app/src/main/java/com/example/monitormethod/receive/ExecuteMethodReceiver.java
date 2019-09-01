package com.example.monitormethod.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExecuteMethodReceiver extends BroadcastReceiver {
    public static final String EXECUTE_METHOD = "EXECUTE_METHOD";
    private long preTime;
    private Activity activity;
    private String activityName;
    public ExecuteMethodReceiver(Activity activity){
        this.activity = activity;
        activityName = activity.getComponentName().getClassName();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case EXECUTE_METHOD:
                long curTime = System.currentTimeMillis();
                if(activityName.equals("com.ichi2.anki.NoteEditor")&&curTime-preTime>300){
                    Log.i("Execute_Method","execute saveNote();"+preTime);
                    executeMethod("saveNote");
                }
                preTime = curTime;

        }
    }

    private void executeMethod(String methodName) {
        Class clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method:methods){
            method.setAccessible(true);
            if (method.getName().equals(methodName)){
                try {
                    method.invoke(activity,null);
                    Log.i("LZH","execute method: "+methodName);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
