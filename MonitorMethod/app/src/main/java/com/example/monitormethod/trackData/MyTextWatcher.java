package com.example.monitormethod.trackData;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.monitormethod.util.LogWriter;
import com.example.monitormethod.util.ViewUtil;

public class MyTextWatcher implements TextWatcher {
    private DataRecorder dataRecorder;
    private String fileName = "methodLog.txt";
    private LogWriter logWriter;
    private View view;
    public MyTextWatcher(View view){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,view.getContext().getPackageName());
        this.view = view;
        dataRecorder = DataRecorder.getInstance();
    }
    private JSONObject writeInfo(View view,String text){
        JSONObject json = new JSONObject();
        json.put("callerClassName",this.getClass().getName());
        int hash = dataRecorder.getRefKey(this);
        if(hash<=0){
            hash = dataRecorder.addRef(this);
        }
        json.put("callerHashCode",hash);

        json.put("methodName","setText");

        JSONObject itemJSON = new JSONObject();
        int viewHash = dataRecorder.getRefKey(view);
        if(viewHash<=0){
            viewHash = dataRecorder.addRef(view);
        }
        itemJSON.put("parameterClassName",String.class.getName());
        itemJSON.put("parameterHashCode",viewHash);
        itemJSON.put("parameterValue",text);
        itemJSON.put("parameterId",view.getId());
        itemJSON.put("parameterViewIndex", ViewUtil.getViewIndex(view));
//        Log.i("LZH","parameterViewIndex: "+ViewUtil.getViewIndex(view));
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String info = writeInfo(view,s.toString()).toJSONString();
        if(logWriter!=null){
            logWriter.writeLog("before: "+info);
        }
        Log.i("LZH","Text: "+info);
        if(logWriter!=null){
            logWriter.writeLog("after: "+info);
        }

    }
}