package com.example.monitormethod.xposed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.example.monitormethod.receive.RecordMethodLogReceiver;
import com.example.monitormethod.trackData.DataCollectioner;
import com.example.monitormethod.trackData.DataRecorder;
import com.example.monitormethod.trackData.SystemDataCollection;
import com.example.monitormethod.util.ContextUtil;
import com.example.monitormethod.util.LogWriter;
import com.example.monitormethod.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;


public class TrackMethod extends XC_MethodHook {

    Class pclazz[];
    private LogWriter logWriter;
    private DataRecorder dataRecorder;
    private String fileName = "APIFile/methodLog.txt";
    private JSONObject jsonObject;
    public TrackMethod(Class pclazz[],String packageName){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,packageName);
        dataRecorder = DataRecorder.getInstance();
        this.pclazz = pclazz;
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        if(true){
//            return;
//        }
        if(Thread.currentThread().getId()!=1){
            return;
        }
        if(param.method.getName().contains("getLayoutDirection")){
            if(param.method.getDeclaringClass().getName().contains("com.douban.frodo.baseproject.view.flowlayout.DouFlowLayout")){
                Log.i("LZH","com.douban.frodo.baseproject.view.flowlayout.DouFlowLayout/getLayoutDirection");
            }
        }
        if(param.method.getName().contains("onStop")){
            if(param.method.getDeclaringClass().getName().contains("com.douban.frodo.baseproject.activity.BaseActivity")){
                Log.i("LZH","com.douban.frodo.baseproject.activity.BaseActivity/onStop");
            }
        }
        if(param.method.getName().contains("search")){
            Log.i("LZH","find search method");
        }
        if(jsonObject==null){
            jsonObject = new JSONObject();
            writeCallerInfo(jsonObject,param);
            writeMethodName(jsonObject,param);
            writeMethodParameter(jsonObject,param);
            writeThreadId(jsonObject);
            writeViewFlag(jsonObject,param);
            writeViewInfo(jsonObject,param);
            writeActivityID(jsonObject,param);
        }

        if(logWriter!=null){
            logWriter.writeLog("before: "+jsonObject.toJSONString());
//            Log.i("LZH-Method","before: "+jsonObject.toJSONString());
//            sendMethodLog("before: "+jsonObject.toJSONString());
        }

//        Log.i("LZH",""+param.method.getDeclaringClass().getName()+"/"+param.method.getName());
//        Log.i("LZH-Method","before: "+jsonObject.toJSONString());

    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        if(true){
//            return;
//        }
//        checkPermission();
        if(Thread.currentThread().getId()!=1){
            return;
        }else{
//            Log.i("DIF","DIF_Thread");
        }
//        JSONObject jsonObject = new JSONObject();
//        writeCallerInfo(jsonObject,param);
//        writeMethodName(jsonObject,param);
//        writeMethodParameter(jsonObject,param);
//        writeResultInfo(jsonObject,param);
//        writeThreadId(jsonObject);
//        writeViewFlag(jsonObject,param);
//        writeViewInfo(jsonObject,param);
//        writeActivityID(jsonObject,param);
        writeResultInfo(jsonObject,param);
        if(logWriter!=null){
//            sendMethodLog("after: "+jsonObject.toJSONString());
            logWriter.writeLog("after: "+jsonObject.toJSONString());
//            Log.i("LZH-Method","after: "+jsonObject.toJSONString());
        }

    }
    private void sendMethodLog(String log){
        Context context = ContextUtil.getContext();
        if(context!=null){
            Intent intent = new Intent();
            intent.setAction(RecordMethodLogReceiver.WRITE_LOG);
            intent.putExtra(RecordMethodLogReceiver.METHOD_LOG,log);
            context.sendBroadcast(intent);
        }
    }
    /**
     * 向JSON中写入方法调用者的信息
     * @param json
     * @param param
     */
    private void writeCallerInfo(JSONObject json,MethodHookParam param){
        Object caller = param.thisObject;
        String callerName = param.method.getDeclaringClass().getName();
        //调用者的类名
        json.put("callerClassName",callerName);
        json.put("callerHashCode",-1);
//        if(caller!=null){
//            int hash = dataRecorder.getRefKey(caller);
//            if(hash<=0){
//                hash = dataRecorder.addRef(caller);
//            }
//            //调用者的hash值
//            json.put("callerHashCode",hash);
//        }else {
//            json.put("callerHashCode",null);
//        }
    }

    /**
     * 向JSON中写入方法的名称
     * @param json
     * @param param
     */
    private void writeMethodName(JSONObject json,MethodHookParam param){
        json.put("methodName",param.method.getName());
    }

    /**
     * 向JSON写入方法的参数
     * @param json
     * @param param
     */
    private void writeMethodParameter(JSONObject json,MethodHookParam param){
        Object p = null;
        JSONObject itemJson;
        JSONArray jsonArray = new JSONArray();
        //参数的个数
        for(int i=0;i<pclazz.length;i++){
            itemJson = new JSONObject();
            //某个参数的类名
//            if(param.args[i]!=null){
//                itemJson.put("parameterClassName",param.args[i].getClass().getName());
//            }else{
//                itemJson.put("parameterClassName",pclazz[i].getName());
//            }
            itemJson.put("parameterClassName",pclazz[i].getName());
            itemJson.put("parameterHashCode",null);
            itemJson.put("parameterValue",null);

//            p = param.args[i];
//            if(p!=null){
//                int hash = dataRecorder.getRefKey(p);
//                if(hash<=0){
//                    hash = dataRecorder.addRef(p);
//                }
//                //参数的hashcode
//                itemJson.put("parameterHashCode",hash);
//                //如果参数是View或MenuItem，获取他们的ID
//                if(p instanceof View){
//                    itemJson.put("parameterId",((View)p).getId());
//                }else if(p instanceof MenuItem){
//                    itemJson.put("parameterId",((MenuItem)p).getItemId());
//                }
//                //如果参数是基本类型，则写入他们的值，是其他类型写入他们的HashCode
//                if(ParserConfig.getGlobalInstance().isPrimitive(p.getClass())){
//                    itemJson.put("parameterValue",p.toString());
//                }else{
//                    itemJson.put("parameterValue",hash);
//                }
//            }else{
//                itemJson.put("parameterHashCode",null);
//                itemJson.put("parameterValue",null);
//            }
            //添加一个参数的JSON
            jsonArray.add(itemJson);
        }
        json.put("methodParameter",jsonArray);
    }

    /**
     * 将方法的返回结果写入JSON
     * @param json
     * @param param
     */
    private void writeResultInfo(JSONObject json,MethodHookParam param){
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("resultClassName",null);
        resultJSON.put("resultValue",null);
        resultJSON.put("resultHashCode",null);

//        if(param==null){
//            return;
//        }
//        Object result = null;
//        try {
//            result = param.getResultOrThrowable();
//        }catch (Throwable throwable) {
//            Log.i("LZH","error ResultOrThrowable:"+throwable.getMessage()+"-"+param.method.getName());
//            throwable.printStackTrace();
//        }
//        int hash = -1;
//        if(result!=null){
//            hash = dataRecorder.getRefKey(result);
//            if(hash<=0){
//                hash = dataRecorder.addRef(result);
//            }
//            resultJSON.put("resultClassName",result.getClass().getName());
////            Log.i("LZH",result.getClass().getName()+" "+param.method.getName());
//
//            resultJSON.put("resultHashCode",hash);
//            if(ParserConfig.getGlobalInstance().isPrimitive(result.getClass())){
//                resultJSON.put("resultValue",result.toString());
//            }else{
//                resultJSON.put("resultValue",hash);
//            }
//        }else{
//            resultJSON.put("resultClassName",null);
//            resultJSON.put("resultValue",null);
//            resultJSON.put("resultHashCode",null);
//        }

        json.put("methodResult",resultJSON);
    }

    /**
     * 将方法调用的线程ID写入JSON
     * @param jsonObject
     */
    private void writeThreadId(JSONObject jsonObject){
        long id = Thread.currentThread().getId();
        jsonObject.put("threadId",id);
    }

    /**
     * 向JSON写入一个ViewFlag，用来表示参数的调用者是不是View类型的
     * @param jsonObject
     * @param param
     */
    private void writeViewFlag(JSONObject jsonObject, MethodHookParam param) {
        Object caller = param.thisObject;
        if(caller==null||!(caller instanceof View)){
            jsonObject.put("ViewFlag",false);
        }else{
            jsonObject.put("ViewFlag",true);
        }
    }
    /**
     * 将响应点击的view的ID和路径写入JSON
     * * @param json
     * @param param
     */
    private void writeViewInfo(JSONObject json,MethodHookParam param){
        String methodName = param.method.getName();
        if(!methodName.contains("dispatchTouchEvent")){
            return;
        }
        Object o = param.thisObject;
        View view = null;
        if(o instanceof View){
            view = (View) o;
        }else{
            return ;
        }
        JSONObject viewInfo = new JSONObject();
        viewInfo.put("viewId",view.getId());
        viewInfo.put("viewPath", ViewUtil.getViewPath(view));
        json.put("viewInfo",viewInfo);
    }
    private void writeActivityID(JSONObject json,MethodHookParam param){
        Object o = param.thisObject;
        View view = null;
        if(o instanceof View){
            view = (View) o;
        }else{
            return;
        }
        json.put("ActivityID",ViewUtil.getActivityNameByView(view));
    }

}
