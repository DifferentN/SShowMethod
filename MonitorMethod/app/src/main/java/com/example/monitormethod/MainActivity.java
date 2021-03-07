package com.example.monitormethod;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitormethod.ViewManager.FloatViewManager;
import com.example.monitormethod.receive.RecordMethodLogReceiver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private Button bt;
    private TextView textView;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };


//        bt.dispatchTouchEvent()

    }
    public void click(View view){
        textView.setText("Click!!");
        Log.i("LZH"," clicked ");
    }
    public void click2(View view){
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = 72;
        int y = 184;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        if(motionEvent!=null){
            Log.i("LZH","success create MotionEvent");
        }
        getWindow().getDecorView().dispatchTouchEvent(motionEvent);
//        bt.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        bt.dispatchTouchEvent(motionEvent);
        getWindow().getDecorView().dispatchTouchEvent(motionEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void init(){
        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
        floatViewManager.showSaveIntentViewBt();

        RecordMethodLogReceiver recordMethodLogReceiver = new RecordMethodLogReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecordMethodLogReceiver.WRITE_LOG);
        intentFilter.addAction(RecordMethodLogReceiver.RECORD_SWITCH);
        registerReceiver(recordMethodLogReceiver,intentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
