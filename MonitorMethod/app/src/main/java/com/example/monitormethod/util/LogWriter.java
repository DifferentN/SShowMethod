package com.example.monitormethod.util;

import android.util.Log;

import com.example.monitormethod.receive.LocalActivityReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private static String targetPKName = "com.example.musicplayer";
    public boolean TempIsSetText = false;
    public int num = 0;
    public static int fileNumber = 1;
    private static List<String> list ;
    //com.kingsoft com.ichi2.anki  com.tencent.qqmusic  com.ss.android.autoprice
    //com.ichi2.anki  com.yongche.android  com.douban.movie  com.jnzc.shipudaquan
    //com.dangdang.buy2 com.naman14.timberx  yst.apk com.cqrenyi.huanyubrowser
    //com.yr.qmzs com.jrtd.mfxszq com.netease.pris com.wondertek.paper
    //com.infzm.ireader com.ifeng.news2 com.duxiaoman.umoney
    //com.boohee.food com.boohee.one com.boohee.food com.smartisan.notes
    //com.dragon.read com.xiangha
    public LogWriter(String fileName){
        fName = fileName;
//        file = new File(fName);
        list = new LinkedList<>();
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
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
//        Log.i("LZH","token: "+token);
        if(!token){
            return;
        }
        list.add(log);
//        Log.i("LZH",log);
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
        if(token){
            //create new File for saving log
            int index = fName.indexOf(".");
            String newFileName = fName.substring(0,index)+fileNumber+fName.substring(index);
            fileNumber++;
            file = new File(newFileName);
            if(file.exists()){
                file.delete();

            }
            try {
                file.createNewFile();
                Log.i("LZH","new Create File");
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new BufferedWriter(fileWriter);

        }
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
            list.clear();
//            writer.flush();
//            Log.i("LZH","write "+log);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static class MyWriteFileRunnable implements Runnable{
        private List<String> contents;
        private BufferedWriter fileWriter;
        public MyWriteFileRunnable(List<String> contents,BufferedWriter fileWriter){
            this.contents = contents;
            this.fileWriter = fileWriter;
        }
        @Override
        public void run() {
            try {
                for(String log:contents){
                    writer.write(log);
                }
                writer.flush();
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            Log.i("LZH","log file write finish");
        }
    }
}
