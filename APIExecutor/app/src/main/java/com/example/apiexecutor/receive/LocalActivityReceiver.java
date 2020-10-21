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
import com.example.apiexecutor.serve.MyAPIExecuteAdapter;
import com.example.apiexecutor.trackData.ActivityNameRecord;
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
    public static final String START_ACTION = "START_ACTION";
    public static final String INPUT_EVENT = "INPUT_EVENT";
    public static final String SEND_ACTIVITY_NAME = "SEND_ACTIVITY_NAME";
    public static final String FLAG_ACTIVITY_NAME = "FLAG_ACTIVITY_NAME";

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

    public static final String START_ACTIVITY_NAME = "startActivityName";

    public static final String CAPTURE_PAGE_CONTENT = "CAPTURE_PAGE_CONTENT";

    public static final String CHECK_USER_ACTION_STATE = "CHECK_USER_ACTION_STATE";

    private String showActivityName = "";
    private String selfActivityName = "";
    private String selfPackageName;
    private String selfAppName;
    private String curPackageName = "";
    private String curAppName;
    private String launchActivityName = "";
    //当前APP是否开始执行API(为了简便，先设置为static)
    private static boolean isStartAction = false;

    private MethodExecutor methodExecutor;
    private MethodTrackPool methodTrackPool;
    private UserAction prepareUserAction;
    //执行次数，userAction执行一次就加1
    private static int executeTime;
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
                Log.i("LZH","show Activity: "+showActivityName+" time: "+System.currentTimeMillis());
                if(showActivityName.equals(selfActivityName)&&isStartAction){
                    checkExecuteAction();
                }
                break;
            case LocalActivityReceiver.openTargetActivityByIntent:
                Intent tarIntent = intent.getParcelableExtra(LocalActivityReceiver.TARGET_INTENT);
                if(showActivityName.compareTo(selfActivityName)!=0){
                    break;
                }
                selfActivity.startActivity(tarIntent);
                break;
            case LocalActivityReceiver.EXECUTE_EVENT:
                if(!isStartAction){
                    return;
                }
                UserAction userAction = intent.getParcelableExtra(USER_ACTION);
                prepareUserAction = userAction;
                Log.i("LZH","showActivityName: "+showActivityName+"self: "+selfActivityName+" target: "+userAction.getActivityName());
                if(showActivityName.compareTo(selfActivityName)!=0||!selfActivityName.contains(userAction.getActivityName())){
                    break;
                }
                Log.i("LZH","execute event:"+userAction.getActionName());
                if(executeUserAction(userAction)){
                    prepareUserAction = null;
                    Log.i("LZH","execute finish: "+System.currentTimeMillis());
                }

                break;
            case LocalActivityReceiver.START_ACTION:
                //amodule.activity.main.MainHomePageNew
                //com.douban.movie.activity.MainActivity
                //com.tencent.qqmusic.activity.AppStarterActivity
                //com.imooc.component.imoocmain.index.MCMainActivity
                //cn.cuco.model.version3.home.HomeVersion3Activity
                //com.zhangshangjianzhi.newapp.activity.tab.MainTabActivity
                //com.cqrenyi.huanyubrowser.activity.MainActivity
                //com.yr.cdread.activity.MainActivity
                //com.netease.pris.activity.MainGridActivity
                //cn.thepaper.paper.ui.main.MainActivity
                //com.infzm.ireader.activity.HomeActivity
                //com.duxiaoman.umoney.home.MainActivity
                //com.boohee.food.HomeActivity
                //com.boohee.one.app.home.ui.activity.main.MainActivity
                String startActivityName = intent.getStringExtra(START_ACTIVITY_NAME);
                Log.i("LZH","self: "+selfActivityName+" target: "+startActivityName);
                if(selfActivityName.equals(startActivityName)){
                    methodTrackPool = MethodTrackPool.getInstance();
                    methodTrackPool.clearRunTimeRecord();
                    methodTrackPool.LaunchUserAction();
                    isStartAction = true;
                    Log.i("LZH","start action");
                }
                break;
            case LocalActivityReceiver.CAPTURE_PAGE_CONTENT:
                if(selfActivityName.equals(showActivityName)){
                    //获取用户屏幕内容
                    ArrayList<String> contents = ViewUtil.capturePageContent(selfActivity);
                    //将屏幕内容发送到MyServe
                    Intent pageResponse = new Intent();
                    pageResponse.setAction(MyAPIExecuteAdapter.API_RESPONSE);
                    pageResponse.putExtra(MyAPIExecuteAdapter.PAGE_CONTENT,contents);
                    pageResponse.putExtra(MyAPIExecuteAdapter.RESULT_STATE,MyAPIExecuteAdapter.RESULT_STATE_SUCCESS);
                    selfActivity.sendBroadcast(pageResponse);
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
                }
                break;
            case SEND_ACTIVITY_NAME:
                launchActivityName = intent.getStringExtra(FLAG_ACTIVITY_NAME);
                ActivityNameRecord.launchActivityName = launchActivityName;
                ActivityNameRecord.launchEnable = true;
                break;
        }
    }

    /**
     * 发送一个延时通知，去检查userAction是否已经执行
     *
     * @return
     */
    private void checkHasExecuteUserAction(){
        ExecuteStateRunnable executeStateRunnable = new ExecuteStateRunnable(this,executeTime);
        selfActivity.getWindow().getDecorView().postDelayed(executeStateRunnable,
                9*1000);
    }
    private void notifyUserActionFinish(UserAction userAction){
        methodTrackPool = MethodTrackPool.getMethodTrackPool();
        if(methodTrackPool==null){
            return;
        }
        String actionFlag = userAction.getViewPath()+"/"+userAction.getActionName();
        methodTrackPool.setActionFinish(actionFlag);
    }
    private boolean executeUserAction(UserAction userAction){
        boolean executionOver = false;
        //userAction已经执行1次
        executeTime++;
        checkHasExecuteUserAction();

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
            View view = getViewByPath(selfActivity.getWindow().getDecorView().getRootView(),
                    userAction.getViewPath());
            if(view==null){
                //可能会存在多个相同路径的view
                view = getViewByPath2(userAction.getViewPath());
            }
            if(view!=null){
                Log.i("LZH","view Width: "+view.getWidth()+" height: "+view.getHeight());
            }
            if(view==null&&userAction.getViewId()>0){
                Log.i("LZH","can't get view by viewPath; "+userAction.getViewPath());
            }
            if(view==null||view.getWidth()==0||view.getHeight()==0||!ViewUtil.isVisible(view)){
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
            notifyUserActionFinish(userAction);
            tryLaunchUserAction();
            if(checkAPIFinished()){
                //API 中的用户事件已经执行完成，发送一个延时广播，去捕获用户页面中的内容
                selfActivity.getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setAction(LocalActivityReceiver.CAPTURE_PAGE_CONTENT);
                        selfActivity.sendBroadcast(intent);
                    }
                },3000);
            }

        }
        return executionOver;
    }

    private boolean checkAPIFinished() {
        methodTrackPool = MethodTrackPool.getMethodTrackPool();
        if(methodTrackPool==null){
            return false;
        }
        if(methodTrackPool.isAPIFinished()){
            return true;
        }
        return false;
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
        View targetView = null;
        if(mViews!=null){
            for(View view:mViews){
                targetView = getViewByPath(view,path);
                if(targetView!=null){
                    Log.i("LZH","find view");
                    break;
                }
            }
            return targetView;
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
    private int[] getViewLocation(View rootView,View targetView){
        class Node{
            public float x,y;
            public View view;
            public Node(float x,float y,View view){
                this.x = x;
                this.y = y;
                this.view = view;
            }
        }
        List<Node> queue = new ArrayList<>();
        View decorView = rootView;
        queue.add(new Node(decorView.getX(),decorView.getY(),decorView));
        Node temp = null;
        ViewGroup viewGroup;
        View child = null;
        while(!queue.isEmpty()){
            temp = queue.remove(0);
            if(targetView==temp.view){
                int location[] = new int[2];
                location[0] = (int) temp.x;
                location[1] = (int) temp.y;
                return location;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                for(int i=0;i<viewGroup.getChildCount();i++){
                    child = viewGroup.getChildAt(i);
                    queue.add(new Node(temp.x+child.getX(),temp.y+child.getY(),child));
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
        methodTrackPool = MethodTrackPool.getMethodTrackPool();
        if(methodTrackPool!=null){
            methodTrackPool.LaunchUserAction();
        }
    }
    private void checkExecuteAction(){
        methodTrackPool = MethodTrackPool.getInstance();
        if(methodTrackPool.getCurUserAction()!=null&&
                methodTrackPool.isAvailable()){
            methodTrackPool.retrySendNotification();
        }
    }
    @Override
    public String getContent() {
        return content;
    }

    private static class ExecuteStateRunnable implements Runnable{
        //临时保存的执行次数，用来检查某个userAction是否执行
        private int preExecuteTime;
        private LocalActivityReceiver localActivityReceiver;
        public ExecuteStateRunnable(LocalActivityReceiver local,int executeTime){
            localActivityReceiver = local;
            preExecuteTime = executeTime;
        }
        @Override
        public void run() {
            //如果API已经执行完成，则返回
            if(localActivityReceiver.checkAPIFinished()){
                return;
            }
            Log.i("LZH","preExecuteTime: "+preExecuteTime+" "+localActivityReceiver.executeTime);
            if(preExecuteTime==localActivityReceiver.executeTime){

//                MethodTrackPool methodTrackPool = MethodTrackPool.getMethodTrackPool();
//                if(!methodTrackPool.isStart()){
//                    //表示第一个操作没有执行，要重新执行,否则则说明API执行中断
//                    methodTrackPool.LaunchUserAction();
//
//                    return;
//                }
                Intent pageResponse = new Intent();
                pageResponse.setAction(MyAPIExecuteAdapter.API_RESPONSE);
                pageResponse.putExtra(MyAPIExecuteAdapter.RESULT_STATE,MyAPIExecuteAdapter.RESULT_STATE_ERROR);
                localActivityReceiver.selfActivity.sendBroadcast(pageResponse);
            }
        }
    }
}
