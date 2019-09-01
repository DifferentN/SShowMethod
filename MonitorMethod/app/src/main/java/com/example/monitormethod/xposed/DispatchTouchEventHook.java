package com.example.monitormethod.xposed;

import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.monitormethod.trackData.DataCollectioner;
import com.example.monitormethod.trackData.DataRecorder;
import com.example.monitormethod.util.LogWriter;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventHook extends XC_MethodHook {
    private LogWriter logWriter;
    private DataCollectioner dataCollectioner;
    private DataRecorder dataRecorder;
    private String fileName = "methodLog.txt";
    public DispatchTouchEventHook(String packageName){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,packageName);
        dataCollectioner = DataCollectioner.getInstance();
        dataRecorder = DataRecorder.getInstance();
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        Log.i("LZH","view id:"+((View)param.thisObject).getId());
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject jsonObject = null;
        if(motionEvent!=null){
            jsonObject = writeInfo((View) param.thisObject,motionEvent);
            Log.i("LZH-Method","before: "+jsonObject.toJSONString());
            logWriter.writeLog("before: "+jsonObject.toJSONString());
        }
//        Log.i("LZH","click: x:"+motionEvent.getX()+" y: "+motionEvent.getY());
//        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject json = null,resultJSON = null;
        if(motionEvent!=null){
            json = writeInfo((View) param.thisObject,motionEvent);
            resultJSON = writeResult(param);
            json.put("methodResult",resultJSON);
            Log.i("LZH-Method","after: "+json.toJSONString());
            logWriter.writeLog("after: "+json.toJSONString());
        }

    }
    private JSONObject writeInfo(View view,MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        json.put("callerClassName",view.getClass().getName());
        int hash = dataRecorder.getRefKey(view);
        if(hash<=0){
            hash = dataRecorder.addRef(view);
        }
        json.put("callerHashCode",hash);

        json.put("methodName","dispatchTouchEvent");
        JSONObject itemJSON = new JSONObject();
        int eventHash = dataRecorder.getRefKey(motionEvent);
        if(eventHash<=0){
            eventHash = dataRecorder.addRef(motionEvent);
        }
        itemJSON.put("parameterClassName",motionEvent.getClass().getName());
        itemJSON.put("parameterHashCode",eventHash);
        itemJSON.put("parameterValue",writeMotionEvent(motionEvent));
        itemJSON.put("parameterId",view.getId());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(itemJSON);
        json.put("methodParameter",jsonArray);

        long threadId = Thread.currentThread().getId();
        json.put("threadId",threadId);

        return json;
    }
    private JSONObject writeMotionEvent(MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        json.put("action",motionEvent.getAction());
        json.put("downTime",motionEvent.getDownTime());
        json.put("EventTime",motionEvent.getEventTime());
        json.put("x",(int)motionEvent.getX());
        json.put("y",(int)motionEvent.getY());
        json.put("metaState",motionEvent.getMetaState());
        return json;
    }
    private JSONObject writeResult(MethodHookParam param){
        boolean res = (boolean) param.getResult();
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("resultClassName",boolean.class);
        int hash = dataRecorder.getRefKey(res);
        if(hash<=0){
            hash = dataRecorder.addRef(res);
        }
        resultJSON.put("resultHashCode",hash);
        resultJSON.put("resultValue",res);
        return resultJSON;
    }
}
