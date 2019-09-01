package com.example.monitormethod.trackData;

import android.util.Log;
import android.view.Menu;

import de.robv.android.xposed.XC_MethodHook;

public class DataCollectioner {
    private static volatile DataCollectioner dataCollectioner;
    private SystemDataCollection systemDataCollection;
    private MethodDataCollection methodDataCollection;
    public static DataCollectioner getInstance(){
        if(dataCollectioner==null){
            synchronized (DataCollectioner.class){
                if(dataCollectioner==null){
                    dataCollectioner = new DataCollectioner();
                }
            }
        }
        return dataCollectioner;
    }
    public DataCollectioner(){
        systemDataCollection = SystemDataCollection.getInstance();
        methodDataCollection = MethodDataCollection.getInstance();
    }
    public void collectMethodHookParam(XC_MethodHook.MethodHookParam param){
        if(param.method.getName().contains("onCreateOptionsMenu")){
            if(param.args[0] instanceof Menu){
                Menu menu = (Menu) param.args[0];
                SystemDataCollection.getInstance().addReference(Menu.class.getName(),menu);
                Log.i("LZH","get Menu: "+menu.toString());
            }
        }


    }

}
