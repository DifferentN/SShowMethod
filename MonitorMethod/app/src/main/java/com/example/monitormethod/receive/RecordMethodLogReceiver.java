package com.example.monitormethod.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.monitormethod.util.LogWriter;

public class RecordMethodLogReceiver extends BroadcastReceiver {
    public final static String RECORD_SWITCH = "RECORD_SWITCH";
    public final static String METHOD_LOG = "METHOD_LOG";
    public final static String WRITE_LOG = "WRITE_METHOD_LOG";
    private LogWriter logWriter;
    private String fileName = "methodLog.txt";
    //com.dangdang.buy2  com.douban.movie com.jnzc.shipudaquan  yst.apk
    private static String targetPKName = "com.douban.movie";
    public RecordMethodLogReceiver(){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance(fileName,targetPKName);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String methodLog = "";
        switch (action){
            case WRITE_LOG:
                methodLog = intent.getStringExtra(METHOD_LOG);
                logWriter.writeLog(methodLog);
                break;
            case RECORD_SWITCH:
                Log.i("LZH","record order");
                LogWriter.turnWriteAble();
                break;
        }
    }
}
