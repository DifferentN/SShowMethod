package com.example.apiexecutor.core;


import java.util.List;

public class Event {
    public static String SETTEXT = "setText",DISPATCH = "dispatchTouchEvent";
    private String activityId,componentId,path;
    private String methodName;
    private List<MyParameter> parameters;
    private List<String> invokeList;
    public int invokePoint;
    public boolean isFinish = false;
    public Event(String activityId,String componentId,String path,String methodName){
        this.activityId = activityId;
        this.componentId = componentId;
        this.path = path;
        this.methodName = methodName;
    }

    public void setParameters(List<MyParameter> parameters) {
        this.parameters = parameters;
    }

    public void setInvokeList(List<String> invokeList) {
        this.invokeList = invokeList;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getPath() {
        return path;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MyParameter> getParameters() {
        return parameters;
    }

    public List<String> getInvokeList() {
        return invokeList;
    }
}
