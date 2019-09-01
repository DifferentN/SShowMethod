package com.example.monitormethod.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewUtil {
    public static int getViewIndex(View target){
        View decorView = target.getRootView();
        ArrayList<View> list = new ArrayList<>();
        list.add(decorView);
        ViewGroup viewGroup;
        View view;
        int index = 0;
        while(!list.isEmpty()){
            view = list.remove(0);
            index++;
            if(view==target){
                return index;
            }else if(view instanceof ViewGroup){
                viewGroup = (ViewGroup) view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    view = viewGroup.getChildAt(i);
                    list.add(view);
                }
            }
        }
        return -1;
    }
}
