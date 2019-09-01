package com.example.monitormethod.xposed;

import android.app.Activity;
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
import com.example.monitormethod.trackData.DataCollectioner;
import com.example.monitormethod.trackData.DataRecorder;
import com.example.monitormethod.trackData.SystemDataCollection;
import com.example.monitormethod.util.LogWriter;

import de.robv.android.xposed.XC_MethodHook;


public class TrackMethod extends XC_MethodHook {
    Class pclazz[];
    private Activity activity;
    private LogWriter logWriter;
    private DataCollectioner dataCollectioner;
    private DataRecorder dataRecorder;
    private String fileName = "methodLog.txt";
    public TrackMethod(Class pclazz[],String packageName){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,packageName);
        dataCollectioner = DataCollectioner.getInstance();
        dataRecorder = DataRecorder.getInstance();
        this.pclazz = pclazz;
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
        dataCollectioner.collectMethodHookParam(param);

        int menuId = -1;
        JSONObject jsonObject = new JSONObject();
        writeCallerInfo(jsonObject,param);
        writeMethodName(jsonObject,param);
        writeMethodParameter(jsonObject,param);
        writeThreadId(jsonObject);
        if(logWriter!=null){
            logWriter.writeLog("before: "+jsonObject.toJSONString());
            Log.i("LZH-Method","before: "+jsonObject.toJSONString());
        }
    }


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
//        checkPermission();
        JSONObject jsonObject = new JSONObject();
        writeCallerInfo(jsonObject,param);
        writeMethodName(jsonObject,param);
        writeMethodParameter(jsonObject,param);
        writeResultInfo(jsonObject,param);
        writeThreadId(jsonObject);
        if(logWriter!=null){
            logWriter.writeLog("after: "+jsonObject.toJSONString());
            Log.i("LZH-Method","after: "+jsonObject.toJSONString());
        }
    }
    private void writeCallerInfo(JSONObject json,MethodHookParam param){
        Object caller = param.thisObject;
        String callerName = param.method.getDeclaringClass().getName();
        json.put("callerClassName",callerName);
        if(caller!=null){
            int hash = dataRecorder.getRefKey(caller);
            if(hash<=0){
                hash = dataRecorder.addRef(caller);
            }
            json.put("callerHashCode",hash);
        }else {
            json.put("callerHashCode",null);
        }
    }
    private void writeMethodName(JSONObject json,MethodHookParam param){
        json.put("methodName",param.method.getName());
    }
    private void writeMethodParameter(JSONObject json,MethodHookParam param){
        Object p = null;
        JSONObject itemJson;
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<pclazz.length;i++){
            itemJson = new JSONObject();
            if(param.args[i]!=null){
                itemJson.put("parameterClassName",param.args[i].getClass().getName());
            }else{
                itemJson.put("parameterClassName",pclazz[i].getName());
            }

            p = param.args[i];
            if(p!=null){
                int hash = dataRecorder.getRefKey(p);
                if(hash<=0){
                    hash = dataRecorder.addRef(p);
                }
                itemJson.put("parameterHashCode",hash);
                if(p instanceof View){
                    itemJson.put("parameterId",((View)p).getId());
                }else if(p instanceof MenuItem){
                    itemJson.put("parameterId",((MenuItem)p).getItemId());
                }

                if(ParserConfig.getGlobalInstance().isPrimitive(p.getClass())){
                    itemJson.put("parameterValue",p.toString());
                }else{
                    itemJson.put("parameterValue",hash);
                }

            }else{
                itemJson.put("parameterHashCode",null);
                itemJson.put("parameterValue",null);
            }
            jsonArray.add(itemJson);
        }
        json.put("methodParameter",jsonArray);
    }
    private void writeResultInfo(JSONObject json,MethodHookParam param){
        JSONObject resultJSON = new JSONObject();
        if(param==null){
            return;
        }
        Object result = null;
        try {
            result = param.getResultOrThrowable();
        }catch (Throwable throwable) {
            Log.i("LZH","error ResultOrThrowable:"+throwable.getMessage());
            throwable.printStackTrace();
        }
        int hash = -1;
        if(result!=null){
            hash = dataRecorder.getRefKey(result);
            if(hash<=0){
                hash = dataRecorder.addRef(result);
            }
            resultJSON.put("resultClassName",result.getClass().getName());
            resultJSON.put("resultHashCode",hash);
            if(ParserConfig.getGlobalInstance().isPrimitive(result.getClass())){
                resultJSON.put("resultValue",result.toString());
            }else{
                resultJSON.put("resultValue",hash);
            }
        }else{
            resultJSON.put("resultClassName",null);
            resultJSON.put("resultValue",null);
            resultJSON.put("resultHashCode",null);
        }

        json.put("methodResult",resultJSON);

    }
    private void writeThreadId(JSONObject jsonObject){
        long id = Thread.currentThread().getId();
        jsonObject.put("threadId",id);
    }
}
