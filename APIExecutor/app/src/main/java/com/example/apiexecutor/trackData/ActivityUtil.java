package com.example.apiexecutor.trackData;

import android.app.Activity;
import android.content.Context;

public class ActivityUtil {
    private static Activity activity;
    public static Activity getActivityInstance(){
        return activity;
    }
    public static void setActivity(Activity a){
        activity = a;
    }
}
