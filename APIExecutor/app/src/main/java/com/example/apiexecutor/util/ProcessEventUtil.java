package com.example.apiexecutor.util;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.core.Event;
import com.example.apiexecutor.core.MyParameter;

import java.util.ArrayList;
import java.util.List;

public class ProcessEventUtil {
    public static Event transformJSONToEvent(JSONObject jsonObject){
        String activityId = jsonObject.getString("ActivityID");
        String viewId = jsonObject.getString("viewId");
        String viewPath = jsonObject.getString("viewPath");
        String methodName = jsonObject.getString("methodName");
        String parameterValue = jsonObject.getString("parameterValue");
        Event event = new Event(activityId,viewId,viewPath,methodName);

        List<MyParameter> list = new ArrayList<>();
        list.add(new MyParameter("String",parameterValue));
        event.setParameters(list);

        List<String> invokeListStr = new ArrayList<>();
        JSONObject invokeJson = jsonObject.getJSONObject("invoke");
        int size = invokeJson.getInteger("invokeSize");
        for(int i=0;i<size;i++){
            invokeListStr.add(invokeJson.getString(""+i));
        }
        event.setInvokeList(invokeListStr);
        return event;
    }
}
