package com.example.apiexecutor.core;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.apiexecutor.trackData.ObjectPool;
import com.example.apiexecutor.util.ClassUtil;

import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class MethodExecutor {
    private ObjectPool objectPool;
    private boolean canExecute = false;
    public MethodExecutor(){
        objectPool = ObjectPool.getInstance();
    }
    public void executeMethod(JSONObject methodJson, Activity activity){
        canExecute = true;
        ClassLoader classLoader = activity.getClassLoader();
        String callerClassName = methodJson.getString("callerClassName");
        int callerHashCode = methodJson.getIntValue("callerHashCode");
        Class callerClazz = null;
        try {
            callerClazz = classLoader.loadClass(callerClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String methodName = methodJson.getString("methodName");

        JSONArray parameters = methodJson.getJSONArray("methodParameter");
        Object[] params = new Object[parameters.size()];
        Class[] paramClazzs = new Class[parameters.size()];
        int paramId[] = new int[parameters.size()];
        String parameterClassName = "";
        int parameterHashCode = -1;
        Object parameterValue = null;
        Class paramClazz = null;
        int parameterId = -1;
        int parameterViewIndex = -1;
        for(int i=0;i<parameters.size();i++){
            JSONObject itemJson = parameters.getJSONObject(i);
            parameterClassName = itemJson.getString("parameterClassName");
            parameterHashCode = itemJson.getIntValue("parameterHashCode");
            parameterValue = itemJson.get("parameterValue");
            try {
                paramClazz = classLoader.loadClass(parameterClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(paramClazz==null){
                canExecute = false;
                Log.i("LZH","error: ClassNotFoundException");
                break;
            }
            parameterValue =getParam(paramClazz,parameterValue,parameterHashCode,activity);
            if(itemJson.get("parameterId")!=null){
                parameterId = itemJson.getIntValue("parameterId");
            }
            if(itemJson.get("parameterViewIndex")!=null){
                parameterViewIndex = itemJson.getIntValue("parameterViewIndex");
            }
            params[i] = parameterValue;
            paramClazzs[i] = paramClazz;
            paramId[i] = parameterId;
        }
        if(!canExecute){
            Log.i("LZH","can't execute");
            return;
        }

        if(methodName.equals("onClick")){
            View  view = (View) objectPool.getObject(paramClazz,parameterId,activity);
            executeOnClick(view);
        }else if(methodName.equals("onOptionsItemSelected")){
            MenuItem menuItem = (MenuItem) objectPool.getObject(paramClazz,parameterId,activity);
            executeOnSelectMenuItem(menuItem,activity);
        }else if(methodName.equals("setText")){
            List<View> views = objectPool.getViewObject(callerClazz,parameterId,parameterViewIndex,activity);
            executeSetText(views,parameterValue.toString());
        }else {
            Object caller = objectPool.getObject(callerClazz,callerHashCode,activity);
            executeNormalMethod(caller,methodName,paramClazzs,params);
        }
    }

    private Object getParam(Class paramClazz,Object paramValue,int paramHashCode,Activity activity){
        if(paramClazz==null){
            return null;
        }
        if(ClassUtil.isBoolean(paramClazz)){
            return Boolean.valueOf(paramValue.toString());
        }else if(ClassUtil.isInt(paramClazz)){
            return Integer.valueOf(paramValue.toString());
        }else if(ClassUtil.isString(paramClazz)){
            return paramValue.toString();
        }
        //not belong to primitive
        Object p = objectPool.getObject(paramClazz,paramHashCode,activity);
        return p;
    }
    private void executeOnClick(View view){
        try {
            Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Object mListenerInfo = field.get(view);
            Field clickField = mListenerInfo.getClass().getDeclaredField("mOnClickListener");
            clickField.setAccessible(true);
            Object mOnClickListener = clickField.get(mListenerInfo);
            Method method = mOnClickListener.getClass().getDeclaredMethod("onClick",View.class);
            method.invoke(mOnClickListener,view);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private void executeOnSelectMenuItem(MenuItem menuItem,Activity activity){
        Method method = null;
        try {
            method = activity.getClass().getDeclaredMethod("onOptionsItemSelected", MenuItem.class);
            method.setAccessible(true);
            method.invoke(activity,menuItem);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.i("LZH","invoke error "+e.getMessage());
            e.printStackTrace();
        }
    }
    private void executeSetText(List<View> textViews,String text){
        TextView textView;
        for(int i=0;i<textViews.size();i++){
            textView = (TextView) textViews.get(i);
            textView.setText(text);
        }

    }
    private Object executeNormalMethod(Object caller,String methodName,Class[] pClazzs,Object[] parmas){
        if(caller==null){
            Log.i("LZH","caller is null when call: "+methodName);
            return null;
        }
        Object res = null;
        Class clazz = caller.getClass();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName,pClazzs);
            method.setAccessible(true);
            if(method!=null){
                res = method.invoke(caller,parmas);
            }
        } catch (NoSuchMethodException e) {
            Log.i("LZH","caller invoke error "+e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.i("LZH","caller invoke error "+e.getMessage());
            e.printStackTrace();
        }
        return res;

    }
}
