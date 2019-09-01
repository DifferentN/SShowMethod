package com.example.apiexecutor.test;

import android.util.Log;

import com.example.apiexecutor.receive.LocalActivityReceiver;

public class MyRunnable implements Runnable {
    public LocalActivityReceiver localActivityReceiver;
    public long tempTime;
    private int eventTime;
    public MyRunnable(LocalActivityReceiver localActivityReceiver,long time,int eventTIme){
        this.localActivityReceiver = localActivityReceiver;
        tempTime  = time;
        this.eventTime = eventTIme;
    }
    @Override
    public void run() {
        Log.i("LZH","2time: "+tempTime+" "+localActivityReceiver.curTime);
        if(tempTime==localActivityReceiver.curTime){
            Log.i("LZH","execute click");
            localActivityReceiver.doClick(eventTime);
        }
    }
}
