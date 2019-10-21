package com.example.monitormethod.trackData;

import android.view.View;

import java.lang.ref.WeakReference;

public class TouchedView {
    private static WeakReference<View> viewRefer;
    public static void setView(View view){
        viewRefer = new WeakReference<>(view);
    }
    public static View getView(){
        return viewRefer==null?null:viewRefer.get();
    }
}
