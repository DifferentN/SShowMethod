package com.example.monitormethod.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * 获取view的路径
     * @param view
     * @return
     */
    public static String getViewPath(View view){
        if(view==null){
            return "";
        }
        View decorView = view.getRootView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view==view){
                return temp.path;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                   child = viewGroup.getChildAt(i);
                   childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                   list.add(childNode);
                }
            }
        }
        return "";
    }
    /**
     * 获取activity页面中的内容
     * 返回结果中的每一个Item格式： xpath:text
     * @param activity
     * @return
     */
    public static HashMap<String,String> capturePageContent(Activity activity){
        HashMap<String,String> hash = new HashMap<>();
        View decorView = activity.getWindow().getDecorView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view instanceof TextView){
                hash.put(temp.path,((TextView) temp.view).getText().toString());
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                    list.add(childNode);
                }
            }
        }
        return hash;
    }
    public static String getActivityNameByView(View view){
        Context context = null;
        String activityName = null;
        if(view!=null){
            context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    activityName = ((Activity)context).getComponentName().getClassName();
                    break;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        String curActivityName = ContextUtil.getActivityName();
        if(!activityName.equals(curActivityName)){
            activityName = curActivityName;
        }
        return activityName;
    }
    static class ViewNode{
        public View view;
        public String path;
        public ViewNode(View view,String path){
            this.view = view;
            this.path = path;
        }
    }

}
