package com.example.monitormethod.trackData;

import java.util.WeakHashMap;

public class SystemDataCollection {
    private static WeakHashMap<String, Object> weakHashMap;
    private static SystemDataCollection systemDataCollection;
    public SystemDataCollection(){
        weakHashMap = new WeakHashMap<>();
    }
    public static SystemDataCollection getInstance(){
        if(systemDataCollection ==null){
            systemDataCollection = new SystemDataCollection();
        }
        return systemDataCollection;
    }
    public void addReference(String className,Object ref){
        weakHashMap.put(className,ref);
    }
    public Object getReference(String className){
        return weakHashMap.get(className);
    }
}
