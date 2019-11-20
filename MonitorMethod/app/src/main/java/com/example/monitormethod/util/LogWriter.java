package com.example.monitormethod.util;

import android.util.Log;

import com.example.monitormethod.receive.LocalActivityReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 单实例模式，确保所有方法调用Log顺序的写入文件中
 */
public class LogWriter {
    private static File file;
    private static BufferedWriter writer;
    private static volatile LogWriter logWriter;
    private static String fName;
    private static boolean token = false;//是否可以写入日志的标志
    private static long preTime;
    private static String targetPKName = "cn.cuco";
    public boolean TempIsSetText = false;
    public int num = 0;
    private static List<String> list ;
    //com.kingsoft com.ichi2.anki  com.tencent.qqmusic  com.ss.android.autoprice
    //com.ichi2.anki  com.yongche.android  com.douban.movie  com.jnzc.shipudaquan
    public LogWriter(String fileName){
        fName = fileName;
        file = new File(fName);
        list = new ArrayList<>();
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

    /**
     * 向文件中写入方法调用
     * @param log
     */
    public synchronized void writeLog(String log){
        if(!token){
            return;
        }
        list.add(log);
//        try {
////            Log.i("LZH","thread id: "+Thread.currentThread().getId());
//            writer.write(log+"\n");
////            writer.flush();
////            Log.i("LZH","write "+log);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
            writeLogList();
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void writeLogList(){
        try {
//            Log.i("LZH","thread id: "+Thread.currentThread().getId());
            for(String log:list){
                writer.write(log+"\n");
            }
//            writer.flush();
//            Log.i("LZH","write "+log);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
