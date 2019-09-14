package com.example.apiexecutor.trackData;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.apiexecutor.receive.LocalActivityReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class MethodTrackPool {
    private Context context;
    private static volatile MethodTrackPool methodTrackPool;
    private ArrayList<String> sequence;
    private List<String> runTimeRecord;
    private List<MyMethod> subCall;
    private boolean isAvailable = false;
    public MethodTrackPool(){
        sequence = new ArrayList<String>();
        subCall = new ArrayList<>();
        runTimeRecord = new ArrayList<>();
//        runTimeRecord = Collections.synchronizedList(runTimeRecord);
        readSequence("doubanLogDetail.txt");
//        readSequence("shipudaquanLogDetail.txt");
    }

    public static MethodTrackPool getInstance(){
        if(methodTrackPool==null){
            synchronized (MethodTrackPool.class){
                if(methodTrackPool==null){
                    methodTrackPool = new MethodTrackPool();
                }
            }
        }
        return methodTrackPool;
    }
    private void readSequence(String fileName){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        File file = new File(filePath);
        try {
            if(!file.exists()){
                Log.i("LZH","序列文件不存在");
            }
            FileReader fileReader  = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while( (line = reader.readLine())!=null ){
                sequence.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("LZH","sequence size "+sequence.size());
    }
    public synchronized void sendMessage(String message){
        if(!isAvailable()){
//            Log.i("LZH","unavailable");
            return;
        }
//        Log.i("LZH",message);
        addSubCall(message);
//        String body = null;
//        if(message.startsWith("before: ")){
//            body = message.substring("before: ".length());
//            runTimeRecord.add(body);
//        }else if(message.startsWith("after: ")){
//            body = message.substring("after: ".length());
//            String last = runTimeRecord.get(runTimeRecord.size()-1);
//            if(last.equals(body)){
//                last = runTimeRecord.remove(runTimeRecord.size()-1);
////                Log.i("LZH-SRC",last);
//                if(runTimeRecord.size()==0){
//                    removeSequenceItem(last);
//                }
//            }else{
//                Log.i("LZH","runTimeRecord error");
//            }
//        }else{
//            Log.i("LZH","error: "+message);
//        }
    }
    private void removeSequenceItem(String last){
        if(skipSequence(last,2)>0){
            Log.i("LZH",last);
        }else{
            Log.i("LZH","未匹配,要求："+sequence.get(0)+" 现有："+last);
//            Log.i("LZH","未匹配,"+" 现有："+last);
        }
        int deleteNum = -1;
        if( !sequence.isEmpty()&&(deleteNum=skipSequence(last,2))>0 ){
            deleteSameSequence(deleteNum);
            checkNotification();
        }
    }


    /**
     * 不只检查Sequence中的第一个序列，检查前checkNum个
     * @param last
     * @param checkNum checkNum=1相当于检查第一个序列
     * @return
     */
    private int skipSequence(String last,int checkNum){
        int num = 0;
        boolean same = false;
        while(num<sequence.size()&&checkNum>0){
            if( last.equals(sequence.get(num)) ){
                same = true;
                num++;
                break;
            }
            num++;
            checkNum--;
        }
        if(!same){
            num = -1;
        }
        return num;
    }
    private void deleteSameSequence(int num){
        while(num>0&&!sequence.isEmpty()){
            sequence.remove(0);
            num--;
        }
    }

    private void checkNotification() {
        if(!sequence.isEmpty()&&sequence.get(0).contains("dispatchTouchEvent")){
            Log.i("LZH","dispatchTouchEvent");
            sequence.remove(0);
            sendNotification();
        }
    }
    private boolean isAvailable(){
        if(context!=null&&isAvailable){
            return true;
        }
        return false;
    }

    private void sendNotification(){
        if(context==null){
            Log.i("LZH","context is null can't send notification");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.AFTER_METHOD);
        context.sendBroadcast(intent);
    }

    public void setContext(Context context){
        this.context = context;
    }
    public void clearRunTimeRecord(){
//        runTimeRecord.clear();
        subCall.clear();
        isAvailable = true;
    }
    public void addSubCall(String message){
        String body = null;
        if(message.startsWith("before: ")){
            body = message.substring("before: ".length());
            subCall.add(new MyMethod(body));
        }else if(message.startsWith("after: ")){
            body = message.substring("after: ".length());
            MyMethod last = subCall.get(subCall.size()-1);
            MyMethod parent = null;
            if(last.name.equals(body)){
                last = subCall.remove(subCall.size()-1);
                if(!subCall.isEmpty()){
                    parent = subCall.get(subCall.size()-1);
                    parent.add(last.getDetail());
                }else {
//                    Log.i("LZH-SRC",last.getDetail());
                    removeSequenceItem(last.getDetail());
//                    skipSequence(last.getHash());
                }
            }else{
                Log.i("LZH","runTimeRecord error");
            }
        }
    }
    private static class MyMethod{
        public String name;
        private List<String> childs;
        public MyMethod(String name){
            this.name = name;
            childs = new ArrayList<>();
        }
        public void add(String child){
            childs.add(child);
        }
        public String getHash(){
            return name+":"+childs.hashCode();
        }
        public String getDetail(){
            for(int i=0;i<childs.size();i++){
                name+=":"+childs.get(i);
            }
            return name;
        }
    }
}
