package com.example.monitormethod.util;

import android.content.Context;

public class ContextUtil {
    private static Context context;
    private static String activityName;
    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextUtil.context = context;
    }

    public static void setActivityName(String activityName) {
        ContextUtil.activityName = activityName;
    }

    public static String getActivityName() {
        return activityName;
    }
}
