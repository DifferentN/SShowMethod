package com.example.monitormethod.trackData;

public class MethodDataCollection {
    private static volatile MethodDataCollection methodDataCollection;
    public static MethodDataCollection getInstance(){
        if(methodDataCollection==null){
            synchronized (MethodDataCollection.class){
                if(methodDataCollection==null){
                    methodDataCollection = new MethodDataCollection();
                }
            }
        }
        return methodDataCollection;
    }
}
