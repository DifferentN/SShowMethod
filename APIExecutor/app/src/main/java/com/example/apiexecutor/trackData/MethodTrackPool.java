package com.example.apiexecutor.trackData;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.core.Event;
import com.example.apiexecutor.core.UserAction;
import com.example.apiexecutor.receive.LocalActivityReceiver;
import com.example.apiexecutor.util.ProcessEventUtil;

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
    private List<Event> events;
    private Event curEvent;
    private boolean isAvailable = false;
    public MethodTrackPool(){
        sequence = new ArrayList<String>();
        subCall = new ArrayList<>();
        runTimeRecord = new ArrayList<>();
//        runTimeRecord = Collections.synchronizedList(runTimeRecord);
//        readSequence("doubanLogDetail.txt");
//        readSequence("shipudaquanLogDetail.txt");
        readSequence("ankiLogDetail.txt");
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
        StringBuffer buf = new StringBuffer();
        try {
            if(!file.exists()){
                Log.i("LZH","序列文件不存在");
            }
            FileReader fileReader  = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while( (line = reader.readLine())!=null ){
                buf.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = JSONArray.parseArray(buf.toString());
        events = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            events.add(ProcessEventUtil.transformJSONToEvent(jsonArray.getJSONObject(i)));
        }
    }
    public synchronized void sendMessage(String message){
        if(!isAvailable()){
            return;
        }
        addSubCall(message);
    }
    private void removeSequenceItem(String last){
        List<String> invokeStrs = curEvent.getInvokeList();
//        if(curEvent.invokePoint<invokeStrs.size()){
//            Log.i("LZH","method: "+last+" "+invokeStrs.get(curEvent.invokePoint));
//        }
        if(curEvent.invokePoint<invokeStrs.size()&&
                check(invokeStrs.get(curEvent.invokePoint),last)){
            curEvent.invokePoint++;
            Log.i("LZH","match method");
        }else{
            Log.i("LZH","curMethod"+last+" \n record: "+invokeStrs.get(curEvent.invokePoint));
        }
        checkNotification(curEvent);
    }


    /**
     * 不只检查Sequence中的第一个序列，检查前checkNum个
     * @param last 当前要比较的调用字符串
     * @param checkNum checkNum=1相当于检查第一个序列
     * @return
     */
    private int skipSequence(String last,int checkNum){
        int num = 0;
        boolean same = false;
        while(num<sequence.size()&&checkNum>0){//||check(sequence.get(num),last)
            if( last.equals(sequence.get(num)) ){//||check(last,sequence.get(num))
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

    /**
     *
     * @param cur
     * @param record
     * @return
     */
    private boolean check(String cur,String record){
        int curLen = cur.length(),recLen = record.length();
        String s1 = null,s2 = null;
        int i=0,j=0;
        for(;i<curLen&&j<recLen;){
            s1 = getFirstFun(cur,i);
            s2 = getFirstFun(record,j);
            if(s1==null&&s2!=null){
                return false;
            }
            if(s2==null){
                return true;
            }
            i=cur.indexOf(s1,i)+s1.length();
            if(s1.equals(s2)){
                j=record.indexOf(s2,j)+s2.length();
            }

        }
        Log.i("LZH","出错："+i+" "+curLen+" "+j+" "+recLen);
        return false;
    }

    /**
     * 获取src中从pos开始的第一个方法名称
    * @param src
     * @param pos
     * @return
     */
    private String getFirstFun(String src,int pos){
        int start = src.indexOf("(",pos);
        if(start<0){
            return null;
        }
        int end = src.indexOf(")",start);
        int temp = src.indexOf(":",start);
        if(temp>0){
            end = Math.min(end,temp);
        }
        String res = src.substring(start+1,end);
        return res;
    }

    private void checkNotification(Event event) {
        if(event==null||event.invokePoint>=event.getInvokeList().size()){
            if(!events.isEmpty()){
                curEvent = events.remove(0);
                sendNotification(curEvent);
            }
        }
    }
    private boolean isAvailable(){
        if(context!=null&&isAvailable){
            return true;
        }
        return false;
    }

    private void sendNotification(Event event){
        if(context==null){
            Log.i("LZH","context is null can't send notification");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.EXECUTE_EVENT);
        UserAction userAction = new UserAction(event.getMethodName(),
                event.getPath(),
                Integer.valueOf(event.getComponentId()),
                event.getActivityId());

        if(event.getMethodName().equals(Event.SETTEXT)){
            userAction.setText(event.getParameters().get(0).value);
        }
        intent.putExtra(LocalActivityReceiver.USER_ACTION,userAction);
        context.sendBroadcast(intent);
    }
    public void setContext(Context context){
        this.context = context;
    }
    public void clearRunTimeRecord(){
        subCall.clear();
        isAvailable = true;
    }
    public void LaunchUserAction(){
        if(!isAvailable()){
            return;
        }
        if(curEvent==null||curEvent.invokePoint>=curEvent.getInvokeList().size()){
            curEvent = events.remove(0);
        }
        sendNotification(curEvent);
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
                    removeSequenceItem(last.getDetail());
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
        public String getDetail(){
            for(int i=0;i<childs.size();i++){
                name+=":"+childs.get(i);
            }
            return "("+name+")";
        }
    }
}
