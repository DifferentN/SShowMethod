package com.example.monitormethod.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monitormethod.trackData.SystemDataCollection;
import com.example.monitormethod.util.LogWriter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用来播放Intent，输入，点击事件。
 * 抽取，传递页面信息
 */
public class LocalActivityReceiver extends BroadcastReceiver implements CallBackToServer{
    private Activity selfActivity;
    public static final String intent = "intent";
    public static final String currentActivity = "currentActivity";
    public static final String openTargetActivityByIntent = "openTargetActivityByIntent";
    public static final String openTargetActivityByDeepLink = "openTargetActivityByDeepLink";
    public static final String INPUT_TEXT = "INPUT_TEXT";
    public static final String TEXT_KEY = "TEXT_KEY";
    public static final String INPUT_EVENT = "INPUT_EVENT";
    public static final String EVENTS = "EVENTS";
    public static final String DEEP_LINK_KEY = "DEEP_LINK_KEY";
    public static final String DEEP_LINK = "DEEP_LINK";

    public static final String GenerateIntentData = "GenerateIntentData";
    public static final String GenerateDeepLink = "GenerateDeepLink";

    public static final String fromAppStart = "fromAppStart";
    public static final String fromActivityStart ="fromActivityStart";
    public static final String fromActivityPlay = "fromActivityPlay";
    public static final String tarPackageName = "tarPackageName";
    public static final String TARGET_INTENT = "targetIntent";

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_CLICK = "ON_CLICK";
    public static final String CREATE_DESK = "CREATE_DESK";
    public static final String FIND_SPECIFY = "FIND_SPECIFY";
    public static final String CLICK_DELETE = "CLICK_DELETE";

    public static final String obtainActivityText = "obtainActivityText";

    private String showActivityName = "";
    private String selfActivityName = "";
    private String selfPackageName;
    private String selfAppName;
    private String curPackageName = "";
    private String curAppName;
    private String textKey ;
    private byte[] eventBytes;
    private String startActivityFrom;
    private String startActivityFromApp;
    private String selfpackageName;

    private int eventTime = 0;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();
//        selfAppName = AppUtil.getAppName(selfActivity);
        selfPackageName = activity.getPackageName();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case LocalActivityReceiver.currentActivity:
                Bundle bundle = intent.getBundleExtra("currentActivity");
                showActivityName = (String) bundle.get("showActivity");
                Log.i("LZH","showActivity: "+showActivityName);
                curPackageName = (String)bundle.get("curPackage");
//                curAppName = (String) bundle.get("curApp");
                break;
            case LocalActivityReceiver.openTargetActivityByIntent:
                Intent tarIntent = intent.getParcelableExtra(LocalActivityReceiver.TARGET_INTENT);
                startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityStart);
                startActivityFromApp = intent.getStringExtra(LocalActivityReceiver.fromAppStart);
                Log.i("LZH","self: "+selfAppName+"start: "+startActivityFromApp);

                if(selfAppName.compareTo(startActivityFromApp)!=0||showActivityName.compareTo(selfActivityName)!=0){
                    break;
                }
                Log.i("LZH","从"+selfActivityName+"打开"+startActivityFrom);

                selfActivity.startActivity(tarIntent);
                break;
            case LocalActivityReceiver.INPUT_TEXT:
                textKey = intent.getStringExtra(LocalActivityReceiver.TEXT_KEY);
                startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityPlay);
                if(startActivityFrom.compareTo(selfActivityName)!=0){
                    break;
                }
                Log.i("LZH"," to InputText");
                inputText(textKey);
                break;
            case LocalActivityReceiver.INPUT_EVENT:
                Log.i("LZH","intput_event: "+eventTime++);
                eventBytes = intent.getByteArrayExtra(LocalActivityReceiver.EVENTS);
                //在指定的页面播放点击事件
                startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityPlay);
                if(startActivityFrom.compareTo(selfActivityName)!=0){
                    break;
                }
                playMotionEvent(eventBytes);
                break;
            case LocalActivityReceiver.GenerateIntentData:
                if(selfActivityName.equals(showActivityName)){

                    analyseJSON();

                }
                break;
            case LocalActivityReceiver.GenerateDeepLink:
                if(selfActivityName.equals(showActivityName)) {
                    Log.i("ycx", "receive GenerateDeepLink command.");
                    String randomKey = intent.getStringExtra(DEEP_LINK_KEY);
                }
                break;
            case LocalActivityReceiver.openTargetActivityByDeepLink:
                Log.i("ycx", "receive openTargetActivityByDeepLink command.");
                String deepLink = intent.getStringExtra(LocalActivityReceiver.DEEP_LINK);
                String tarPackageName = intent.getStringExtra(LocalActivityReceiver.tarPackageName);
                if(tarPackageName.equals(selfPackageName)){

                }
                //startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityStart);


                if(selfPackageName.compareTo(tarPackageName)!=0||showActivityName.compareTo(selfActivityName)!=0){
                    //Log.i("ycx", "break???");
                    break;
                }
                //Log.i("ycx","从"+selfActivityName+"打开"+startActivityFrom);
                break;
            case LocalActivityReceiver.obtainActivityText:
                if(selfActivityName.equals(showActivityName)) {
                    obtainActivityText(selfActivity.getWindow().getDecorView());
                    Log.i("LZH","get"+selfActivityName+" Activity Text");
                }
                break;
            case LocalActivityReceiver.WRITE_LOG:
//                Log.i("LZH",selfPackageName);
//                DeckPicker com.douban.movie com.ichi2.anki com.jnzc.shipudaquan
                // com.ltz.dict
                if(selfPackageName.contains("com.douban.movie")){
                    LogWriter.turnWriteAble();
                }
                if(selfActivityName.contains("NewSearchActivity")){
                    final TextView textView = selfActivity.findViewById(2131298054);
                    textView.setText("哪吒");
                    String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/methodLog.txt";
                    LogWriter.getInstance(fileName,"com.douban.movie").TempIsSetText = true;
                }
                break;
            case LocalActivityReceiver.ON_CLICK:
//                DeckPicker
                if(selfActivityName.contains("NewSearchActivity")){
//                    View view = selfActivity.findViewById(2131820548);
//                    imitateClick(view);
//                    view = selfActivity.findViewById(2131820723);
//                    imitateClick(view);
//                    View view = selfActivity.findViewById(2131820805);
//                    imitateClick(view);
//                    View view = selfActivity.findViewById(2131820915);
//                    imitateClick(view);
//                    imitateDispatchTouchEvent(view,36,32);
                    click(selfActivity);
                }
                break;
            case LocalActivityReceiver.CREATE_DESK:
                if(selfActivityName.contains("DeckPicker")){
//                    imitateCreate_Desk_clickDialog(selfActivity);
                    imitateCreate_Desk(selfActivity);
                }
                break;
            case LocalActivityReceiver.FIND_SPECIFY:
                if(selfActivityName.contains("DeckPicker")){
                    List<View> views = getRootViews(selfActivity);
                    if(views==null){
                        Log.i("LZH","views is null");
                    }else{
                        for(View view:views){
                            View root = view.getRootView();
                            Log.i("LZH","top view: "+root.getClass().getName());
                            findSpecifyView(root);
//                            break;
                        }
                    }
                }
                break;
            case LocalActivityReceiver.CLICK_DELETE:
                if(selfActivityName.contains("Reviewer")){
                    clickDelete(selfActivity);
                }
                break;
        }
    }
    private void click(Activity activity){
        Log.i("LZH","doclick");
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = 535;
        int y = 149;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        activity.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        activity.dispatchTouchEvent(motionEvent);
    }
    private void clickDelete(Activity activity){
        Log.i("LZH","click delete"+activity.getClass().getName());
        try {
            Method method = activity.getClass().getDeclaredMethod("onOptionsItemSelected",MenuItem.class);
            method.setAccessible(true);
            Menu menu = (Menu) SystemDataCollection.getInstance().getReference(Menu.class.getName());
            MenuItem menuItem = menu.findItem(2131820894);
            method.invoke(activity,menuItem);
            Log.i("LZH","after delete dialog");

            List<View> views = getRootViews(activity);
            View dialog = null;
            for(View child:views){
                dialog = child.findViewById(2131820744);
                if(dialog!=null){
                    break;
                }
            }
            if(dialog!=null){
                imitateDispatchTouchEvent(dialog,46,39);
//                imitateClick(dialog);
            }else {
                Log.i("LZH","error getClickView is null");
            }
        } catch (Exception e){

        }
    }
    private List<View> getRootViews(Activity activity){
        ArrayList<View> views = null;
        ClassLoader loader;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        try {
            Class clazz = activity.getClassLoader().loadClass("android.view.WindowManagerImpl");
            Field field = clazz.getDeclaredField("mGlobal");
            field.setAccessible(true);
            Object global = field.get(windowManager);

            Class globalClazz = global.getClass();
            field = globalClazz.getDeclaredField("mViews");
            field.setAccessible(true);
            views = (ArrayList<View>) field.get(global);
        } catch (ClassNotFoundException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        }
        return views;
    }
    private void findSpecifyView(View view){
        Log.i("LZH","start find");
        List<View> list = new ArrayList<>();
        list.add(view);
        ViewGroup viewGroup = null;
        View temp = null,child = null;
        int len = 0;
        while (!list.isEmpty()){
            temp = list.remove(0);
            if(temp instanceof ViewGroup){
                viewGroup = (ViewGroup) temp;
                len = viewGroup.getChildCount();
                for(int i=0;i<len;i++){
                    temp = viewGroup.getChildAt(i);
                    Log.i("LZH","find View1: "+temp.getClass().getName());
                    if(temp instanceof ViewGroup){
                        list.add(temp);
                    }
                }
            }
        }
        Log.i("LZH","end find");
    }
    private void imitateCreate_Desk_clickDialog(Activity activity){
        Object dialog = SystemDataCollection.getInstance().getReference("com.afollestad.materialdialogs.MaterialDialog");
        Class clazz = dialog.getClass();
        try {
            Method m = clazz.getDeclaredMethod("onClick",View.class);
            View view = activity.findViewById(2131820744);
            m.invoke(dialog,view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private void imitateCreate_Desk(Activity activity){
        ClassLoader classLoader = activity.getClassLoader();
        Method method = null;
        try {
            Class ankiActivityClass = classLoader.loadClass("com.ichi2.anki.AnkiActivity");
            method = ankiActivityClass.getDeclaredMethod("getCol");
            method.setAccessible(true);
            Object next = method.invoke(activity);

            Class collectionClass = next.getClass();
            method = collectionClass.getDeclaredMethod("getDecks");
            method.setAccessible(true);
            Object next2 = method.invoke(next);

            Class deskClass = next2.getClass();
            method = deskClass.getDeclaredMethod("id",String.class,boolean.class);
            method.setAccessible(true);
            method.invoke(next2,"123",true);

            Log.i("LZH","before get : access$000");
            method = activity.getClass().getDeclaredMethod("access$000",activity.getClass());
            Log.i("LZH","before execute : access$000");
            method.setAccessible(true);
            method.invoke(activity,activity);
            Log.i("LZH","after: access$000");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.i("LZH","error: "+e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private void imitateDispatchTouchEvent(View view,int x,int y){
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
    }
    private void imitateClick(View view){
        try {
            Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Object mListenerInfo = field.get(view);
            Field clickField = mListenerInfo.getClass().getDeclaredField("mOnClickListener");
            clickField.setAccessible(true);
            Object mOnClickListener = clickField.get(mListenerInfo);
            Method method = mOnClickListener.getClass().getDeclaredMethod("onClick",View.class);
            method.invoke(mOnClickListener,view);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void inputText(String textKey) {
        View view = selfActivity.getWindow().getDecorView();
        EditText editText = findEditText(view);
        if(editText==null){
            Log.i("LZH","未找到EditText");
        }
        Log.i("LZH","输入text: "+textKey);
        editText.setText(textKey);
    }
    private EditText findEditText(View view){
        ArrayList<View> list = new ArrayList<>();
        list.add(view);
        View cur;
        ViewGroup viewGroup;
        while (!list.isEmpty()){
            cur = list.remove(0);
            if(cur instanceof ViewGroup){
                viewGroup = (ViewGroup) cur;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    list.add(viewGroup.getChildAt(i));
                }
            }else if(cur instanceof EditText){
                return (EditText) cur;
            }
        }
        return null;
    }


    private void playMotionEvent(byte[] bytes) {
        //延时1s，回放点击事件，保证view已经被刷新出来
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MotionEvent[] motionEvents = tranformtoMotionEvent(bytes);
        MotionEvent curEvent;
        for(int i=0;i<motionEvents.length;i++){
            curEvent = MotionEvent.obtain(motionEvents[i]);
            selfActivity.dispatchTouchEvent(curEvent);
//            targetActivity.dispatchTouchEvent(motionEvents[i]);
            Log.i("LZH","x: "+curEvent.getRawX()+" y: "+curEvent.getRawY());
        }
    }
    private MotionEvent[] tranformtoMotionEvent(byte[] bytes){
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes,0,bytes.length);
        parcel.setDataPosition(0);
        int size = parcel.readInt();
        MotionEvent[] motionEvents = new MotionEvent[size];
        parcel.readTypedArray(motionEvents,MotionEvent.CREATOR);
        return motionEvents;
    }
    private void analyseJSON(){

    }
    private void obtainActivityText(View decorView){

    }
    @Override
    public String getContent() {
        return content;
    }

}
