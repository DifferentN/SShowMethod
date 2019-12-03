package com.example.apiexecutor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
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
        String packageName = "yst.apk";
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    public void delete(View view){

    }

}
