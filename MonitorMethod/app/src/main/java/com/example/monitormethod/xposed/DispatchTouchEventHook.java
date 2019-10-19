package com.example.monitormethod.xposed;

import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.monitormethod.trackData.DataCollectioner;
import com.example.monitormethod.trackData.DataRecorder;
import com.example.monitormethod.trackData.TouchedView;
import com.example.monitormethod.util.LogWriter;
import com.example.monitormethod.util.ViewUtil;

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
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject jsonObject = null;
        View view = (View) param.thisObject;
        if(motionEvent!=null){
            jsonObject = writeInfo(view,motionEvent);
            writeThreadId(jsonObject);
            writeViewInfo(jsonObject,view);
            writeViewFlag(jsonObject,view);
            writeActivityID(jsonObject,view);
            Log.i("LZH-Method","before: "+jsonObject.toJSONString());
            logWriter.writeLog("before: "+jsonObject.toJSONString());
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject json = null,resultJSON = null;
        View view = (View) param.thisObject;
        //记录刚刚点击的view
        sendDispatchView(view);
        if(motionEvent!=null){
            json = writeInfo(view,motionEvent);
            resultJSON = writeResult(param);
            json.put("methodResult",resultJSON);
            writeThreadId(json);
            writeViewInfo(json,view);
            writeViewFlag(json,view);
            writeActivityID(json,view);
            Log.i("LZH-Method","after: "+json.toJSONString());
            logWriter.writeLog("after: "+json.toJSONString());
        }

    }

    /**
     * 记录刚刚点击的View
     * @param view
     */
    private void sendDispatchView(View view){
        TouchedView.setView(view);
    }
    /**
     * 将点击信息转化为一个JSON
     * @param view
     * @param motionEvent
     * @return
     */
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

    /**
     * 将MotionEvent转化为一个JSON
     * @param motionEvent
     * @return
     */
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

    /**
     * 将方法的返回值写入JSON（true/false）
     * @param param
     * @return
     */
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
    private void writeThreadId(JSONObject jsonObject){
        long id = Thread.currentThread().getId();
        jsonObject.put("threadId",id);
    }

    /**
     * 将响应点击的view的ID和路径写入JSON
     * * @param json
     * @param view
     */
    private void writeViewInfo(JSONObject json,View view){
        JSONObject viewInfo = new JSONObject();
        viewInfo.put("viewId",view.getId());
        viewInfo.put("viewPath", ViewUtil.getViewPath(view));
        json.put("viewInfo",viewInfo);
    }
    private void writeViewFlag(JSONObject jsonObject, View view) {
        if(view==null){
            jsonObject.put("ViewFlag",false);
        }else{
            jsonObject.put("ViewFlag",true);
        }
    }
    private void writeActivityID(JSONObject json,View view){
        json.put("ActivityID",ViewUtil.getActivityNameByView(view));
    }
}
