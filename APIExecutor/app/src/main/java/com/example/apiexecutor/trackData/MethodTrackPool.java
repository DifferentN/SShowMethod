package com.example.apiexecutor.trackData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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
import java.util.HashMap;
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
    private int progress = 0;//表示没有已经执行的event
    private boolean executeActionState = false; //false 表示当前操作未执行
    private boolean isAvailable = false;
    private int LOG_SIZE = 0;
    private boolean started = false;
    //开启一个线程处理方法调用信息
    private HandlerThread handlerThread;
    private Handler handler;
    public MethodTrackPool(){
        sequence = new ArrayList<String>();
        subCall = new ArrayList<>();
        runTimeRecord = new ArrayList<>();
//        runTimeRecord = Collections.synchronizedList(runTimeRecord);
//        readSequence("doubanLogDetail.txt");
//        readSequence("shipudaquanLogDetail.txt");
        readSequence("ankiLogDetail.txt");
        handlerThread = new HandlerThread("HandlerCallMessageThread");
        handlerThread.start();
        handler = new InvokeHandler(handlerThread.getLooper(),this);
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
    public static MethodTrackPool getMethodTrackPool(){
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
        int index = 0;
        Event event = null;
        for(int i=0;i<jsonArray.size();i++){
            index = jsonArray.getJSONObject(i).getInteger("seqId");
            event = ProcessEventUtil.transformJSONToEvent(jsonArray.getJSONObject(i));
            events.add(index,event);
        }
    }
    public synchronized void sendMessage(String invokeMessage){
        if(!isAvailable()){
            return;
        }
        //将方法调用信息放到子线程处理
        Message message = handler.obtainMessage();
        message.obj = invokeMessage;
        handler.sendMessage(message);
//        addSubCall(invokeMessage);
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
    private void removeSequenceItem(String last){
        List<String> invokeStrs = curEvent.getInvokeList();
//        Log.i("LZH","curMethod: "+last);
        String invoke = null;
        boolean match = false;
        if(last.length()>=600){
            last = last.substring(0,600);
        }
        runTimeRecord.add(last);
        if(curEvent.invokePoint<invokeStrs.size()){
            invoke = invokeStrs.get(curEvent.invokePoint);
            if(invoke.length()>=600){
                invoke = invoke.substring(0,600);
            }
            invoke = invoke.substring(0,invoke.length()-1);//不能用equals
            for(int i=0;i<runTimeRecord.size();i++){
                // checkEqual(runTimeRecord.get(i),invoke)
                if(runTimeRecord.get(i).contains(invoke)){
                    curEvent.invokePoint++;
                    match = true;
//                    Log.i("LZH","match method");
                    break;
                }
            }
            if(!match&&curEvent.invokePoint<invokeStrs.size()){
//                Log.i("LZH", "curMethod" + last + " \n record: " + curEvent.invokePoint + " " + invoke);
            }
        }
        if(runTimeRecord.size()>LOG_SIZE){
            runTimeRecord.remove(0);
        }
        if(invokeStrs.size()>0){
            checkNotification(curEvent);
        }

    }

    private void checkNotification(Event event) {
        if(event==null||event.invokePoint>=event.getInvokeList().size()){
            if(!events.isEmpty()){
                curEvent = events.remove(0);
                Log.i("LZH","invoke 0");
                sendNotification(curEvent);
            }
        }
    }

    /**
     * 当前的操作已经执行完成，将executeActionState设置为true
     */
    public void setActionFinish(String flag){
        String name = curEvent.getPath()+"/"+curEvent.getMethodName();
        //表示progress个event已经完成
        progress++;
        if(flag.equals(name)){
            executeActionState = true;
        }
    }
    public UserAction getCurUserAction(){
        //如果当前event的action没有执行，则返回userAction
        if(!executeActionState){
            return getUserActionByEvent(curEvent);
        }
        return null;
    }
    public boolean isAvailable(){
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
        Log.i("LZH","sendNotification");
        final Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.EXECUTE_EVENT);
        UserAction userAction = getUserActionByEvent(event);
        intent.putExtra(LocalActivityReceiver.USER_ACTION,userAction);
        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.sendBroadcast(intent);
            }
        },600);
//        context.sendBroadcast(intent);
        executeActionState = false;
        Log.i("LZH","sendActionIntent");
    }

    /**
     * 尝试重新发送curEvent
     */
    public void retrySendNotification(){
        if(curEvent!=null&&!executeActionState){
            sendNotification(curEvent);
        }
    }
    private UserAction getUserActionByEvent(Event event){
        UserAction userAction = new UserAction(event.getMethodName(),
                event.getPath(),
                Integer.valueOf(event.getComponentId()),
                event.getActivityId());

        if(event.getMethodName().equals(Event.SETTEXT)){
            userAction.setText(event.getParameters().get(0).value);
        }
        return userAction;
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
            if(!events.isEmpty()){
                curEvent = events.remove(0);
                sendNotification(curEvent);
            }else{
                Log.i("LZH","event has done");
            }
        }
    }
    public boolean isAPIFinished(){
        if(events!=null&&events.isEmpty()){
            return true;
        }
        return false;
    }
    private boolean checkEqual(String curMethod,String invoke){
        int start = 0,end = 0;
        while(start>=0){
            start = curMethod.indexOf("(",start);
            end = curMethod.indexOf(":",start);
            if(end<0){
                end = curMethod.indexOf(")",start);
            }
            if(start<0){
                break;
            }
            if(!invoke.contains(curMethod.substring(start,end))){
                return false;
            }
            start = end+1;
        }
        return true;
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
    private static class InvokeHandler extends Handler {
        private MethodTrackPool methodTrackPool;
        public InvokeHandler(Looper looper,MethodTrackPool methodTrackPool) {
            super(looper);
            this.methodTrackPool = methodTrackPool;
        }

        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
//            Log.i("LZH","handler: "+message);
            methodTrackPool.addSubCall(message);
        }
    }
}
