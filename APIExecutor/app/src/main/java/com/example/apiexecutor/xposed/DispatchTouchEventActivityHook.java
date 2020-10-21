package com.example.apiexecutor.xposed;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.apiexecutor.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventActivityHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        MotionEvent motionEvent = (MotionEvent) param.args[0];
//        if (param.thisObject instanceof View){
//            int [] location = getViewLocation(((View) param.thisObject).getRootView(),
//                    (View) param.thisObject);
//            View view = (View) param.thisObject;
//            Log.i("LZH","w: "+view.getWidth()+" h: "+view.getHeight());
//        }
//        Log.i("LZH","activity dispatchTouchEvent: "+System.currentTimeMillis());
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
    }
    private int[] getViewLocation(View rootView,View targetView){
        class Node{
            public float x,y;
            public View view;
            public Node(float x,float y,View view){
                this.x = x;
                this.y = y;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        queue.add(new Node(decorView.getX(),decorView.getY(),decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
            if(targetView==temp.view){
                int location[] = new int[2];
                location[0] = (int) temp.x;
                location[1] = (int) temp.y;
                return location;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.x+child.getX(),temp.y+child.getY(),child));
                }
            }
        }
        return null;
    }
}
