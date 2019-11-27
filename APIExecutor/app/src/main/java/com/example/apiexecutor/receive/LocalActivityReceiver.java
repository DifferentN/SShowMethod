package com.example.apiexecutor.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor.core.CoordinatorReceiver;
import com.example.apiexecutor.core.Event;
import com.example.apiexecutor.core.MethodExecutor;
import com.example.apiexecutor.core.UserAction;
import com.example.apiexecutor.trackData.MethodTrackPool;
import com.example.apiexecutor.util.ViewUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String INPUT_EVENT = "INPUT_EVENT";


    public static final String GenerateIntentData = "GenerateIntentData";
    public static final String GenerateDeepLink = "GenerateDeepLink";

    public static final String TARGET_INTENT = "targetIntent";

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_CLICK = "ON_CLICK";
    public static final String CREATE_DESK = "CREATE_DESK";
    public static final String FIND_SPECIFY = "FIND_SPECIFY";
    public static final String CLICK_DELETE = "CLICK_DELETE";

    public static final String EXECUTE_EVENT = "EXECUTE_EVENT";
    public static final String DRAW_OVER = "DRAW_OVER";
    public static final String METHOD_NAME = "METHOD_NAME";

    public static final String USER_ACTION = "USER_ACTION";

    public static final String obtainActivityText = "obtainActivityText";

    private String showActivityName = "";
    private String selfActivityName = "";
    private String selfPackageName;
    private String selfAppName;
    private String curPackageName = "";
    private String curAppName;
    private String selfpackageName;

    private int clickTime = 0;
    public long curTime,preTime;
    private boolean isSetText = false;

    private MethodExecutor methodExecutor;
    private MethodTrackPool methodTrackPool;
    private UserAction prepareUserAction;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();
//        selfAppName = AppUtil.getAppName(selfActivity);
        selfPackageName = activity.getPackageName();
        methodExecutor = new MethodExecutor();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case CoordinatorReceiver.ON_RESUME:
                showActivityName = intent.getStringExtra(CoordinatorReceiver.RESUME_ACTIVITY);
                break;
            case LocalActivityReceiver.openTargetActivityByIntent:
                Intent tarIntent = intent.getParcelableExtra(LocalActivityReceiver.TARGET_INTENT);
//                startActivityFrom = intent.getStringExtra(LocalActivityReceiver.fromActivityStart);
//                startActivityFromApp = intent.getStringExtra(LocalActivityReceiver.fromAppStart);
//                Log.i("LZH","self: "+selfAppName+"start: "+startActivityFromApp);
                if(showActivityName.compareTo(selfActivityName)!=0){
                    break;
                }
                selfActivity.startActivity(tarIntent);
                break;
            case LocalActivityReceiver.EXECUTE_EVENT:
                //2131298054
                UserAction userAction = intent.getParcelableExtra(USER_ACTION);
                prepareUserAction = userAction;
//                Log.i("LZH",selfActivityName+"\n"+showActivityName+"\n"+userAction.getActivityName());
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(userAction.getActivityName())){
                    break;
                }
                Log.i("LZH",userAction.getActionName());
                if(executeUserAction(userAction)){
                    prepareUserAction = null;
                }
                break;
            case LocalActivityReceiver.INPUT_TEXT:
                //amodule.activity.main.MainHomePageNew
                //com.douban.movie.activity.MainActivity
                //com.tencent.qqmusic.activity.AppStarterActivity
                //com.imooc.component.imoocmain.index.MCMainActivity
                //cn.cuco.model.version3.home.HomeVersion3Activity
                //com.zhangshangjianzhi.newapp.activity.tab.MainTabActivity
                if(selfActivityName.equals("com.naman14.timberx.ui.activities.MainActivity")){
                    methodTrackPool = MethodTrackPool.getInstance();
                    methodTrackPool.clearRunTimeRecord();
                    methodTrackPool.LaunchUserAction();
                    Log.i("LZH","start action");
                    isSetText = true;
                }
                break;
            case LocalActivityReceiver.DRAW_OVER:
                if(prepareUserAction!=null){
                    if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(prepareUserAction.getActivityName())){
                        break;
                    }
                    if(executeUserAction(prepareUserAction)){
                        prepareUserAction = null;
                    }
                }else if(selfActivity.getPackageName().contains("cn.cuco")){
                    MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
                    if(!methodTrackPool.isCurActionFinish()){
                        tryLaunchUserAction();
                    }
                }
        }
    }
    private boolean executeUserAction(UserAction userAction){
        boolean executionOver = false;
        Log.i("LZH","imitate user action "+userAction.getActionName());
        if(userAction.getActionName().equals(Event.SETTEXT)){
            TextView textView = null;
            textView = (TextView) getViewByPath(userAction.getViewPath());
            if(textView==null){
                textView = selfActivity.findViewById(userAction.getViewId());
            }
            if(textView==null){
                Log.i("LZH","textView is null:setText");
                return executionOver;
            }
            Log.i("LZH","setText");
            textView.setText(userAction.getText());
            executionOver = true;
        }else if(userAction.getActionName().equals(Event.DISPATCH)){
            View view = getViewByPath2(userAction.getViewPath());
//            getViewByPath2(userAction.getViewPath());
//            Log.i("LZH","viewPath: "+userAction.getViewPath());
            if(view==null&&userAction.getViewId()>0){
                Log.i("LZH","can't get view by viewPath");
                view = selfActivity.findViewById(userAction.getViewId());
            }

            if(view==null||view.getWidth()==0||view.getHeight()==0){
                Log.i("LZH","view is null:dispatchTouchEvent");
                return executionOver;
            }
            Log.i("LZH","findById: "+view.getId()+"w: "+view.getWidth()+"h: "+view.getHeight());
            Log.i("LZH","click view");
            imitateClick(view);
            executionOver = true;
        }
        if(executionOver){
            //通知methodTrackPool 当前发过来的event已经完成
            MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
            methodTrackPool.finishCurAction();
            tryLaunchUserAction();
        }
        return executionOver;

    }
    private View getViewByPath2(String path){
        Object windowManagerImpl = selfActivity.getSystemService(Context.WINDOW_SERVICE);
        Class windManagerImplClazz = windowManagerImpl.getClass();
        Object windowManagerGlobal = null;
        Class windManagaerGlobalClass = null;
        ArrayList<View> mViews = null;
        try {
            Field field = windManagerImplClazz.getDeclaredField("mGlobal");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windManagaerGlobalClass = windowManagerGlobal.getClass();
            field = windManagaerGlobalClass.getDeclaredField("mViews");
            field.setAccessible(true);
            mViews = (ArrayList<View>) field.get(windowManagerGlobal);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.i("LZH","view size: "+mViews.size());
        View targetView = null;
        if(mViews!=null){
            for(View view:mViews){
                targetView = getViewByPath(view,path);
                if(targetView!=null){
                    Log.i("LZH","find view");
                    return targetView;
                }
            }
        }
        return null;
    }
    private View getViewByPath(String viewPath){
        class Node{
            public String path;
            public View view;
            public Node(String path,View view){
                this.path = path;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = selfActivity.getWindow().getDecorView().getRootView();
        String path = decorView.getClass().getName();
        queue.add(new Node(path,decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
//            Log.i("LZH","path: "+temp.path);
            if(temp.path.equals(viewPath)){
                if(temp.view instanceof TextView){
                    Log.i("LZH","text: "+((TextView) temp.view).getText());
                }
                return temp.view;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.path+"/"+child.getClass()+":"+i,child));
                }
            }
        }
        return null;
    }
    private View getViewByPath(View rootView,String viewPath){
        class Node{
            public String path;
            public View view;
            public Node(String path,View view){
                this.path = path;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        String path = decorView.getClass().getName();
        queue.add(new Node(path,decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
            Log.i("LZH","path: "+temp.path);
            if(temp.path.equals(viewPath)){
                if(temp.view instanceof TextView){
                    Log.i("LZH","text: "+((TextView) temp.view).getText());
                }
                return temp.view;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.path+"/"+child.getClass()+":"+i,child));
                }
            }
        }
        return null;
    }
    private void imitateClick(View view){
        int clickPos[] = new int[2];
        view.getLocationInWindow(clickPos);
        clickPos[0]+=view.getWidth()/2;
        clickPos[1]+=view.getHeight()/2;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = clickPos[0];
        int y = clickPos[1];
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        selfActivity.dispatchTouchEvent(motionEvent);
        view.getRootView().dispatchTouchEvent(motionEvent);
//        view.dispatchTouchEvent(motionEvent);
        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
//        view.dispatchTouchEvent(motionEvent);
//        selfActivity.dispatchTouchEvent(motionEvent);
        view.getRootView().dispatchTouchEvent(motionEvent);
    }
    private void tryLaunchUserAction(){
        MethodTrackPool methodTrackPool = MethodTrackPool.getInstance();
        methodTrackPool.LaunchUserAction();
    }


    @Override
    public String getContent() {
        return content;
    }

}
