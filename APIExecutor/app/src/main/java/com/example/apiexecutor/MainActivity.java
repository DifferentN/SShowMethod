package com.example.apiexecutor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.ViewManager.FloatViewManager;
import com.example.apiexecutor.core.Coordinator;
import com.example.apiexecutor.core.CoordinatorReceiver;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Coordinator coordinator;
    private String saveTaskJSON = "";
    private CoordinatorReceiver coordinatorReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewByPath2("");
    }

    private void init(){
        coordinator = new Coordinator();
        coordinatorReceiver = new CoordinatorReceiver(coordinator);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CoordinatorReceiver.EXECUTE_METHOD);
        filter.addAction(CoordinatorReceiver.ON_RESUME);
        registerReceiver(coordinatorReceiver,filter);

        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
        floatViewManager.showSaveIntentViewBt();
    }
    public void save(View view){
        EditText key = findViewById(R.id.saveKey);
        EditText value = findViewById(R.id.saveValue);
        coordinatorReceiver.start=true;
        JSONObject json = new JSONObject();
        json.put("packageName","com.ichi2.anki");
        json.put("intent","#Intent;component=com.ichi2.anki/.NoteEditor;i.CALLER=3;end");
        JSONArray methods = new JSONArray();
        JSONObject method = new JSONObject();

        method.put("callerClassName","com.ichi2.anki.FieldEditText");
        method.put("callerHashCode",2109);
        method.put("methodName","setText");
        JSONArray pArray = new JSONArray();
        JSONObject pitem = new JSONObject();
        pitem.put("parameterClassName",String.class.getName());
        pitem.put("parameterHashCode",2106);
        pitem.put("parameterId",2131820681);
        pitem.put("parameterValue",key.getText().toString());
        pitem.put("parameterViewIndex", 36);
        pArray.add(pitem);
        method.put("methodParameter",pArray);
        methods.add(method);

        method = new JSONObject();
        method.put("callerClassName","com.ichi2.anki.FieldEditText");
        method.put("callerHashCode",2257);
        method.put("methodName","setText");
        pArray = new JSONArray();
        pitem = new JSONObject();
        pitem.put("parameterClassName",String.class.getName());
        pitem.put("parameterHashCode",2253);
        pitem.put("parameterId",2131820681);
        pitem.put("parameterValue",value.getText().toString());
        pitem.put("parameterViewIndex", 38);
        pArray.add(pitem);
        method.put("methodParameter",pArray);
        methods.add(method);

        method = new JSONObject();
        method.put("callerClassName","com.ichi2.anki.NoteEditor");
        method.put("callerHashCode",2255);
        method.put("methodName","onOptionsItemSelected");
        pArray = new JSONArray();
        pitem = new JSONObject();
        pitem.put("parameterClassName","android.view.MenuItem");
        pitem.put("parameterHashCode",2341);
        pitem.put("parameterId",2131820915);
        pitem.put("parameterValue","2341");
        pArray.add(pitem);
        method.put("methodParameter",pArray);
        methods.add(method);

//        method = new JSONObject();
//        method.put("callerClassName","com.ichi2.anki.NoteEditor");
//        method.put("callerHashCode",2255);
//        method.put("methodName","saveNote");
//        pArray = new JSONArray();
//        method.put("methodParameter",pArray);
//        methods.add(method);

        json.put("methods",methods);
        coordinator.setTaskJSON(json.toJSONString());
        Log.i("LZH","SaveApi: "+json.toJSONString());
        coordinator.startApp(coordinator.getPackageName(),this);
    }
    public void delete(View view){
        coordinatorReceiver.start=true;
        JSONObject json = new JSONObject();
        json.put("packageName","com.ichi2.anki");
        json.put("intent","#Intent;component=com.ichi2.anki/.Reviewer;end");
        JSONArray methods = new JSONArray();
        JSONObject method = new JSONObject();

        method.put("callerClassName","com.example.monitormethod.dynamicProxy.OnClickListenerProxy");
        method.put("callerHashCode",2739);
        method.put("methodName","onClick");

        JSONArray pArray = new JSONArray();
        JSONObject pitem = new JSONObject();
        pitem.put("parameterClassName","android.widget.LinearLayout");
        pitem.put("parameterHashCode",2740);
        pitem.put("parameterId",2131820805);
        pitem.put("parameterValue","2740");
        pArray.add(pitem);
        method.put("methodParameter",pArray);
        methods.add(method);

        json.put("methods",methods);
        coordinator.setTaskJSON(json.toJSONString());
        Log.i("LZH","DeleteApi: "+json.toJSONString());
        coordinator.startApp(coordinator.getPackageName(),this);
    }
    private void getViewByPath2(String path){
        Object windowManagerImpl = getSystemService(Context.WINDOW_SERVICE);
        Class windManagerImplClazz = windowManagerImpl.getClass();
        Object windowManagerGlobal = null;
        Class windManagaerGlobalClass = null;
        ArrayList<View> mViews = new ArrayList<>();
        try {Log.i("LZH","pass 0");
            Field field = windManagerImplClazz.getDeclaredField("mGlobal");
            Log.i("LZH","pass 1");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windManagaerGlobalClass = windowManagerGlobal.getClass();
            field = windManagaerGlobalClass.getDeclaredField("mViews");
            field.setAccessible(true);
            mViews = (ArrayList<View>) field.get(windowManagerGlobal);

        } catch (NoSuchFieldException e) {
            Log.i("LZH",e.getMessage());
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            Log.i("LZH",e.getMessage());
            e.printStackTrace();

        }
        Log.i("LZH","view size: "+mViews.size());
        if(mViews!=null){
            for(View view:mViews){

            }

        }
    }
}
