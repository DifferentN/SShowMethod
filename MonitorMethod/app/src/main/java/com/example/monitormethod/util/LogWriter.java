package com.example.monitormethod.util;

import android.util.Log;

import com.example.monitormethod.receive.LocalActivityReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private static File file;
    private static BufferedWriter writer;
    private static volatile LogWriter logWriter;
    private static String fName;
    private static boolean token = false;
    private static long preTime;
    private static String targetPKName = "com.douban.movie";//com.ichi2.anki
    public LogWriter(String fileName){
        fName = fileName;
        file = new File(fName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LogWriter getInstance(String fileName,String packageName){
        if(!packageName.equals(targetPKName)){
            return null;
        }
        if(logWriter==null){
            synchronized (LogWriter.class){
                if(logWriter==null){
                    logWriter = new LogWriter(fileName);
                }
            }
        }
        return logWriter;
    }

    public void writeLog(String log){
        if(!token){
            return;
        }
        try {
            writer.write(log+"\n");
            writer.flush();
            Log.i("LZH","write "+log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void turnWriteAble(){
        long curTime = System.currentTimeMillis();
        if(curTime-preTime<=300){
            preTime = curTime;
            return;
        }
        preTime = curTime;

        token = !token;
        Log.i("LZH",token+"");
        if(!token){
            if (writer==null){
                Log.i("LZH","writer is null");
                return;
            }
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
