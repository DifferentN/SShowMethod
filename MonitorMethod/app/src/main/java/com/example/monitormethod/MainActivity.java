package com.example.monitormethod;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.example.monitormethod.ViewManager.FloatViewManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Button bt;
    private TextView textView;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.testBT);
        textView = findViewById(R.id.textTop);
        init();


        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = 100;
        int y = 100;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        if(motionEvent!=null){
            Log.i("LZH","success create MotionEvent");
        }
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
//        Intent intent = new Intent(this,Main2Activity.class);
//        IntentData intentData = new IntentData();
//        intent.putExtra("1",intentData);
//        Log.i("LZH","intentDataHashCode: "+intentData.hashCode());
//        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void init(){
        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
        floatViewManager.showSaveIntentViewBt();
//        floatViewManager.showGetActivityTextBt();
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("LZH","click bt");
//            }
//        });

//        try {
//            Field field = View.class.getDeclaredField("mListenerInfo");
//            field.setAccessible(true);
//            Object o = field.get(bt);
//            if(o==null){
//                Log.i("LZH","error: can't get mListenerInfo");
//            }else{
//                Log.i("LZH"," get mListenerInfo");
//            }
//        } catch (NoSuchFieldException e) {
//            Log.i("LZH","error1: "+e.getLocalizedMessage());
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            Log.i("LZH","error2: "+e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//       AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("");
//       builder.create().show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
