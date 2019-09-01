package com.example.apiexecutor.trackData;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.apiexecutor.util.ViewUtil;

import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class ObjectPool {
    private static ObjectPool objectPool;
    private ArrayList<ObjectNode> methodList,globalList;
    private int time = 0;
    public static ObjectPool getInstance(){
        if(objectPool==null){
            objectPool = new ObjectPool();
        }
        return  objectPool;
    }
    public ObjectPool(){
        methodList = new ArrayList<>();
        globalList = new ArrayList<>();
    }
    public void addMethodObject(Object o,int id){
        ObjectNode objectNode = new ObjectNode(id,o);
        methodList.add(objectNode);
    }
    public void addGlobalHookParam(XC_MethodHook.MethodHookParam param){
        if(addGlobalMenu(param)){
            return;
        }
    }

    public Object getObject(Class clazz, int id, Activity activity){
        Object res = getMethodObject(id);
        if(res!=null){
            return res;
        }
        time++;
        if(MenuItem.class.isAssignableFrom(clazz)){
            res = getMenuItem(id,activity);
        }else if(View.class.isAssignableFrom(clazz)){
            res = getView(clazz,id,-1, activity);
            if( ((List<View>)res).size()==1 ){
                return ((List<View>)res).get(0);
            }
        }else if(Activity.class.isAssignableFrom(clazz)){
            res = activity;
        }
        return res;
    }
    public List<View> getViewObject(Class clazz, int id, int myIndex,Activity activity){
        List<View> views = getView(clazz,id,myIndex, activity);
        return views;
    }
    private Object getMethodObject(int id){
        for(ObjectNode objectNode:methodList){
            if(objectNode.Id==id){
                return objectNode.getObject();
            }
        }
        return null;
    }
    private List<View> getView(Class viewClazz,int id,int myIndex, Activity activity){
        ArrayList<View> res=  new ArrayList<>();
        ArrayList<View> list = new ArrayList<>();
        View temp = activity.getWindow().getDecorView();
        list.add(temp);
        ViewGroup vg;
        View v;
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.getId()==id&&viewClazz.isAssignableFrom(temp.getClass())){
                if(myIndex>0){
                    if(ViewUtil.getViewIndex(temp)==myIndex){
                        res.add(temp);
                    }
                }else{
                    res.add(temp);
                }

            }
            if(temp instanceof  ViewGroup){
                vg = (ViewGroup) temp;
                int size = vg.getChildCount();
                for(int i=0;i<size;i++){
                    temp = vg.getChildAt(i);
                    list.add(temp);
                }
            }
        }
        return res;
    }
    private MenuItem getMenuItem(int id,Activity activity){
        Menu menu = getMenu(activity.getComponentName().getClassName()+"/"+Menu.class.getName());
        if(menu==null){
            return null;
        }
        MenuItem menuItem = menu.findItem(id);
        return menuItem;
    }


    private boolean addGlobalMenu(XC_MethodHook.MethodHookParam param){
        if(param.method.getName().contains("onCreateOptionsMenu")){
            if(param.args[0] instanceof Menu){
                Menu menu = (Menu) param.args[0];
                ObjectNode objectNode = new ObjectNode(param.thisObject.getClass().getName()+"/"+Menu.class.getName(),menu);
                globalList.add(objectNode);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param nameKey activity.class.name+/+Menu.class.name
     * @return
     */
    private Menu getMenu(String nameKey){

        for(ObjectNode objectNode:globalList){
            if(objectNode.nameKey.equals(nameKey)){
                return (Menu) objectNode.getObject();
            }
        }
        return null;
    }
    private static class ObjectNode{
        public int Id;
        public String nameKey;
        public WeakReference weakReference;
        public ObjectNode(String nameKey, Object o){
            this.nameKey = nameKey;
            weakReference = new WeakReference(o);
        }
        public ObjectNode(int id,Object o){
            this.Id = id;
            weakReference = new WeakReference(o);
        }
        public Object getObject(){
            return weakReference.get();
        }
    }
}
