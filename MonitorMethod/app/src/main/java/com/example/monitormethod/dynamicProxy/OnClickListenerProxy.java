package com.example.monitormethod.dynamicProxy;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.monitormethod.trackData.DataRecorder;
import com.example.monitormethod.trackData.TrackOnClickListener;
import com.example.monitormethod.util.LogWriter;

public class OnClickListenerProxy implements View.OnClickListener {
    private View.OnClickListener listener;
    private int time;
    private LogWriter logWriter;
    private DataRecorder dataRecorder;
    private String fileName = "APIFile/methodLog.txt";
    public OnClickListenerProxy(View.OnClickListener listener,String packageName){
        this.listener = listener;
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,packageName);
        dataRecorder = DataRecorder.getInstance();
    }
    @Override
    public void onClick(View v) {
//        Log.i("LZH","call : onclick "+time);
        time++;
        String info = this.getClass().getName()+"("+View.class.getName()+")";
        info = writeInfo(v).toJSONString();
        if(logWriter!=null){
            logWriter.writeLog("before: "+info);
        }
        listener.onClick(v);
        if(logWriter!=null){
            logWriter.writeLog("after: "+info);
        }
        TrackOnClickListener trackOnClickListener = TrackOnClickListener.getInstance();
        TrackOnClickListener.ViewNode viewNode = trackOnClickListener.getViewNodeAssociate(this);
        if(viewNode!=null){
            Log.i("LZH","onclick: "+viewNode.view.getClass().getName()+";id: "+viewNode.id+"view:"+v.getId());
        }else{
            Log.i("LZH","viewNode is null");
        }
    }
    private JSONObject writeInfo(View view){
        JSONObject json = new JSONObject();
        json.put("callerClassName",this.getClass().getName());
        int hash = dataRecorder.getRefKey(this);
        if(hash<=0){
            hash = dataRecorder.addRef(this);
        }
        json.put("callerHashCode",hash);

        json.put("methodName","onClick");
        JSONObject itemJSON = new JSONObject();
        int viewHash = dataRecorder.getRefKey(view);
        if(viewHash<=0){
            viewHash = dataRecorder.addRef(view);
        }
        itemJSON.put("parameterClassName",view.getClass().getName());
        itemJSON.put("parameterHashCode",viewHash);
        itemJSON.put("parameterValue",viewHash);
        itemJSON.put("parameterId",view.getId());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(itemJSON);

        json.put("methodParameter",jsonArray);

        JSONObject resultJSON = new JSONObject();
        resultJSON.put("resultClassName",null);
        resultJSON.put("resultHashCode",-1);
        resultJSON.put("resultValue",null);
        json.put("methodResult",resultJSON);

        long threadId = Thread.currentThread().getId();
        json.put("threadId",threadId);

        return json;
    }

}
