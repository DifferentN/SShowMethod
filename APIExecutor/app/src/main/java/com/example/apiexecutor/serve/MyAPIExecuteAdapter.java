package com.example.apiexecutor.serve;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.core.CoordinatorReceiver;
import com.example.apiexecutor.core.Event;
import com.example.apiexecutor.receive.LocalActivityReceiver;
import com.example.apiexecutor.util.MyFileUtil;
import com.example.apiexecutor.util.ProcessEventUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyAPIExecuteAdapter extends BroadcastReceiver {
    public static final String API_LINK = "APILINK";
    public static final String API_MODEL = "APIMODEL";
    public static final String API_OUTPUT = "APIOUTPUT";
    public static final String OUTPUT_VIEW_PATH ="OutputViewPath";
    public static final String OUTPUT_LABEL = "OutputLabel";
    public static final String API_RESPONSE = "API_RESPONSE";
    public static final String PAGE_CONTENT = "PAGE_CONTENT";
    public static final String RESULT_STATE = "RESULT_STATE";
    public static final int RESULT_STATE_SUCCESS = 1;
    public static final int RESULT_STATE_ERROR = 0;
    private static int flag;
    //表示要执行API
    private static final int START_EXECUTE_API = 1;
    private String startActivityName = "";
    //用来阻塞线程，直到得到页面信息(Output)
    private ReentrantLock lock;
    private Condition condition;
    private MyServe myServe;
    private Activity activity;
    private String curAPIName;
    public MyAPIExecuteAdapter(Activity activity){
        this.activity = activity;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case CoordinatorReceiver.ON_RESUME:
                String showActivityName = intent.getStringExtra(CoordinatorReceiver.RESUME_ACTIVITY);
                if(!startActivityName.equals(showActivityName)){
                    break;
                }
                //打开APP后，当显示的Activity是首页Activity（startActivity）时，发送执行API的命令
                if((flag&START_EXECUTE_API)==START_EXECUTE_API){
                    flag&=~START_EXECUTE_API;
                    Intent startAPIIntent = new Intent();
                    startAPIIntent.putExtra(LocalActivityReceiver.START_ACTIVITY_NAME,startActivityName);
                    startAPIIntent.setAction(LocalActivityReceiver.START_ACTION);
                    activity.sendBroadcast(startAPIIntent);
                    Log.i("LZH","start API");
                }
                break;
            case MyAPIExecuteAdapter.API_RESPONSE:
                int resultState = intent.getIntExtra(RESULT_STATE,RESULT_STATE_ERROR);
                HashMap<String,String> pageHash = new HashMap<String,String>();
                if(resultState==RESULT_STATE_SUCCESS){
                    ArrayList<String> pageContents = intent.getStringArrayListExtra(MyAPIExecuteAdapter.PAGE_CONTENT);

                    for(String item:pageContents){
//                        Log.i("LZH","item: "+item);
                        int lastIndex = item.lastIndexOf(":");
                        String path = item.substring(0,lastIndex);
                        String text = item.substring(lastIndex+1,item.length());
                        pageHash.put(path,text);
                    }
                }
                handleResponse(curAPIName,pageHash,resultState);
                break;
        }
    }
    public void executeAPI(String APIName, Map<String,String> params){
        curAPIName = APIName;
        //根据用户给定的参数，构建一个新的API执行文件
        File apiFile = obtainAPIFile(APIName);
        JSONArray apiModel = obtainAPIModel(apiFile);
        assignValueAndSaveAPIModel(apiModel,params);
        //发送执行API的命令
        startAPPAndExecuteAPI(apiModel);
        Log.i("LZH","locking");
        try{
            lock.lock();
            condition.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private void startAPPAndExecuteAPI(JSONArray apiModel) {
        flag |= START_EXECUTE_API;
        String appName = getAPPNameByAPIModel(apiModel);
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(appName);
        activity.startActivity(intent);
    }

    private String getAPPNameByAPIModel(JSONArray apiModel){
        startActivityName = getStartActivityNameByAPIModel(apiModel);
        JSONObject jsonObject = apiModel.getJSONObject(0);
        if(jsonObject.getString("packageName")==null){
            Log.i("LZH","not find packageName");
        }
        String appName = jsonObject.getString("packageName");
        return appName;
    }
    private String getStartActivityNameByAPIModel(JSONArray apiModel){
        String startActivityName = "";
        JSONObject userEvent = apiModel.getJSONObject(0);
        startActivityName = userEvent.getString("ActivityID");
        return startActivityName;
    }
    private void handleResponse(final String apiName,final HashMap<String,String> pageContent,int resultState){
//        AsyncTask asyncTask = new AsyncTask<String,String,JSONArray>() {
//            @Override
//            protected JSONArray doInBackground(String... path) {
//                String filePath = path[0];
//                Log.i("LZH",filePath);
//                JSONObject jsonObject = MyFileUtil.readJSONObject(filePath);
//                Log.i("LZH",jsonObject.toJSONString());
//                JSONArray userRequired = jsonObject.getJSONArray(API_OUTPUT);
//                Log.i("LZH",userRequired.toJSONString());
//                return userRequired;
//            }
//
//            @Override
//            protected void onPostExecute(JSONArray jsonArray) {
//                if(jsonArray==null){
//                    Log.i("LZH","output required is null");
//                }
//                pushOutputToUser(jsonArray,pageContent);
//            }
//        };
        String apiFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+apiName+".json";
        JSONObject jsonObject = MyFileUtil.readJSONObject(apiFilePath);
        JSONArray userRequired = jsonObject.getJSONArray(API_OUTPUT);
        pushOutputToUser(userRequired,pageContent,resultState);
//        asyncTask.execute(apiFilePath);
    }
    public void pushOutputToUser(JSONArray userRequired,HashMap<String,String> pageContent,int resultState){
        //筛选用户需要的输出
        JSONObject responseJson = new JSONObject();
        if(resultState==RESULT_STATE_ERROR){
            responseJson.put("status","error");
            responseJson.put("info","API执行出错");
        }
        if(resultState==RESULT_STATE_SUCCESS){
            if(userRequired!=null&&!pageContent.isEmpty()){
                Log.i("LZH","response to user");
                for(int i=0;i<userRequired.size();i++){
                    JSONObject json = userRequired.getJSONObject(i);
                    String viewPath = json.getString(OUTPUT_VIEW_PATH);
                    String label = json.getString(OUTPUT_LABEL);
                    String value = pageContent.get(viewPath);
                    responseJson.put(label,value);
                }
                responseJson.put("status","success");
            }else{
                //用户对页面没有要求，可以输出页面的全部内容
//            Set<String> keySet = pageContent.keySet();
//            for(String key:keySet){
//                responseJson.put(pageContent.get(key),"");
//            }
                responseJson.put("status","success");
            }
        }

        myServe.setResult(responseJson.toJSONString());
        Log.i("LZH","set result");
        try{
            lock.lock();
            condition.signal();
            Log.i("LZH","signal");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void bindServe(MyServe myServe){
        this.myServe = myServe;
    }

    /**
     * 获取API执行模板
     * @param file
     * @return
     */
    private JSONArray obtainAPIModel(File file){
        JSONObject apiJSONObject = MyFileUtil.readJSONObject(file);
        JSONArray apiModel = apiJSONObject.getJSONArray(API_MODEL);
        return apiModel;
    }

    /**
     *对API模板中的用户输入操作分配 输入值,并将API模板保存到本地文件（执行文件）
     * @param apiModel
     * @param params
     */
    private void assignValueAndSaveAPIModel(JSONArray apiModel,Map<String,String> params){
        JSONObject userEvent = null;
        int size = apiModel.size();
        for(int i=0;i<size;i++){
            userEvent = apiModel.getJSONObject(i);
            if(userEvent.getString("methodName").equals(Event.SETTEXT)){
                String tag = userEvent.getString("parameterType");
                String value = params.get(tag);
                userEvent.put("parameterValue",value);
            }
        }
        String savePath = Environment.getExternalStorageDirectory()+"/ankiLogDetail.txt";
        MyFileUtil.writeEventJSONArray(savePath,apiModel);
    }
    /**
     * 根据API名称获取保存API的文件
     * @param apiName
     * @return
     */
    private File obtainAPIFile(final String apiName){
        String apiFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+apiName+".json";
        //根据apiName获取对应的API文件
        File apiFile = new File(apiFilePath);
        if(!apiFile.exists()){
            Log.i("LZH","file not exist: "+apiFilePath);
        }
        return apiFile;
    }
}
