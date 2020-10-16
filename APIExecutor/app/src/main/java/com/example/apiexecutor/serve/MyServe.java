package com.example.apiexecutor.serve;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import fi.iki.elonen.NanoHTTPD;

public class MyServe extends NanoHTTPD {
    public static final String POST_DATA = "postData";
    private String result = "";
    private WeakReference<MyAPIExecuteAdapter> executeAdapterWeakReference;
    public MyServe(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        StringBuilder stringBuilder = new StringBuilder();
        if(session.getMethod()==Method.POST){
            HashMap<String,String> postData = new HashMap<>();
            try {
                session.parseBody(postData);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            if(postData.get(POST_DATA)!=null){
                //获取执行API需要的参数（标签和值）
                String data = postData.get(POST_DATA);
                HashMap<String,String> params = obtainTagValue(data);
                String url = session.getUri();
                String apiName = url.substring(1);
                Log.i("LZH",data);
                //此过程会阻塞当前线程，直到得到页面内容
                executeAdapterWeakReference.get().executeAPI(apiName,params);
                Log.i("LZH","get Result");
            }
        }
        Log.i("LZH","send data");
        stringBuilder.append(result);
        String content = stringBuilder.toString();
        byte[] byteData = content.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
        Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK,"text/json",byteArrayInputStream,byteData.length);
        return response;
    }

    /**
     * 从data中解析出Tag和Value
     * @param data 形如Tag1:Value1/Tag2:value2/.../Tagn:Valuen
     * @return
     */
    private HashMap<String,String> obtainTagValue(String data){
        HashMap<String,String> res = new HashMap<>();
        String tvGroup[] = data.split("/");
        for(String item:tvGroup){
            String tagValue[] = item.split(":");
            res.put(tagValue[0],tagValue[1]);
        }
        return res;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public void setExecuteAdapter(MyAPIExecuteAdapter adapter){
        executeAdapterWeakReference = new WeakReference<>(adapter);
        adapter.bindServe(this);
    }
}
