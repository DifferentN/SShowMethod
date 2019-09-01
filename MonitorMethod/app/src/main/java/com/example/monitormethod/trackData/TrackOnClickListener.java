package com.example.monitormethod.trackData;

import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackOnClickListener {
    public List<ViewNode> list;
    public HashMap<String,String> hash;
    private static TrackOnClickListener tracker;
    public static TrackOnClickListener getInstance(){
        if(tracker==null){
            tracker = new TrackOnClickListener();
        }
        return tracker;
    }
    public TrackOnClickListener(){
        list = new ArrayList<>();
        hash = new HashMap<>();
    }
    public void add(View view,long id){
        if(hash.get(""+id)!=null){
            return;
        }
        ViewNode viewNode = new ViewNode(view,id);
        list.add(viewNode);
        hash.put(id+"","1");
    }
    public ViewNode getViewNodeAssociate(View.OnClickListener listener){
        for(ViewNode node:list){
            if(checkViewNode(node,listener)){
                return node;
            }
        }
        return null;
    }
    public boolean checkViewNode(ViewNode viewNode, View.OnClickListener listener){
        Object view = viewNode.view;
        if(view==null){
            Log.i("LZH","view is null");
            return false;
        }
        for(Field field:View.class.getDeclaredFields()){
            if(field.getName().equals("mListenerInfo")){
                field.setAccessible(true);
                try {
                    Object listeners = field.get(view);
                    if(listeners==null){
                        return false;
                    }
                    if(checkListeners(listeners,listener)){
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    Log.i("LZH","error: "+e.getMessage());
                    e.printStackTrace();

                }
            }
        }
        return false;
    }
    private boolean checkListeners(Object lists, View.OnClickListener onClickListener){
        Class clazz = lists.getClass();
        for(Field field:clazz.getDeclaredFields()){
            if(field.getName().equals("mOnClickListener")){
                field.setAccessible(true);
                try {
                    Object l = field.get(lists);
                    if(l==onClickListener){
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return false;
    }
    public static class ViewNode{
        public View view;
        public long id;
        public ViewNode(View view,long id){
            this.view = view;
            this.id = id;
        }
    }

}
