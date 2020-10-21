package com.example.apiexecutor.xposed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.example.apiexecutor.core.CoordinatorReceiver;
import com.example.apiexecutor.receive.LocalActivityReceiver;
import com.example.apiexecutor.trackData.ActivityUtil;
import com.example.apiexecutor.trackData.MethodTrackPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by vector on 16/8/4.
 * 只用来配合工具，主要是用来查看页面结构和打印intent序列
 */
public class ActivityOnCreateHook extends XC_MethodHook {

    XC_LoadPackage.LoadPackageParam loadPackageParam;
    
    public ActivityOnCreateHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        final Context context = (Context) param.thisObject;

        Activity activity = (Activity) param.thisObject;
        ComponentName componentName = activity.getComponentName();

//        KLog.v("liuyi","=======onCreate========: " + activityName);


//        Log.i("LZH",activity.getComponentName().getPackageName());
//        print(activity.getComponentName().getClassName(),intent);


//        KLog.v(BuildConfig.GETVIEW, "#*#*#*#*#*#*# enable receiver in: " + activityName);
        injectReceiver(context, activity);
        Intent intent1 = new Intent();
        intent1.setAction("ON_CREATE");
        intent1.putExtra("LAUNCH_PACKAGE_NAME",activity.getComponentName().getPackageName());
        activity.sendBroadcast(intent1);
        Log.i("LZH","after create "+componentName.getClassName());
    }
    private void injectReceiver(Context context, Activity activity) {
        //注册一个广播接收器，可以用来接收指令，这里是用来回去指定view的xpath路径的



        ComponentName componentName = activity.getComponentName();
        Intent intent = activity.getIntent();
//        Log.i("LZH","packageName: "+componentName.getPackageName()+" intent "+getIntentInfo(intent));
//        Log.i("LZH","openActivity: "+componentName.getClassName());

        LocalActivityReceiver receiver = new LocalActivityReceiver(activity);
        IntentFilter filter = new IntentFilter();

        filter.addAction(LocalActivityReceiver.intent);

        filter.addAction(LocalActivityReceiver.currentActivity);
        filter.addAction(LocalActivityReceiver.openTargetActivityByIntent);

        filter.addAction(LocalActivityReceiver.START_ACTION);
        filter.addAction(LocalActivityReceiver.INPUT_EVENT);
        filter.addAction(LocalActivityReceiver.GenerateIntentData);
        filter.addAction(LocalActivityReceiver.GenerateDeepLink);
        filter.addAction(LocalActivityReceiver.openTargetActivityByDeepLink);
        filter.addAction(LocalActivityReceiver.obtainActivityText);
        filter.addAction(LocalActivityReceiver.WRITE_LOG);
        filter.addAction(LocalActivityReceiver.ON_CLICK);
        filter.addAction(LocalActivityReceiver.CREATE_DESK);
        filter.addAction(LocalActivityReceiver.FIND_SPECIFY);
        filter.addAction(LocalActivityReceiver.CLICK_DELETE);
        filter.addAction(CoordinatorReceiver.ON_RESUME);
        filter.addAction(CoordinatorReceiver.EXECUTE_METHOD);
        filter.addAction(LocalActivityReceiver.EXECUTE_EVENT);
        filter.addAction(LocalActivityReceiver.DRAW_OVER);
        filter.addAction(LocalActivityReceiver.SEND_ACTIVITY_NAME);
        filter.addAction(LocalActivityReceiver.CAPTURE_PAGE_CONTENT);

        Object o = XposedHelpers.getAdditionalInstanceField(activity,"iasReceiver");
        if(o!=null){
            return;
        }
        ActivityUtil.setActivity(activity);
        XposedHelpers.setAdditionalInstanceField(activity, "iasReceiver", receiver);
        activity.registerReceiver(receiver,filter);
        MethodTrackPool.getInstance().setContext(activity);
//        Log.i("LZH","register activity: "+componentName.getClassName());
//        showClassName(activity.getPackageName(),activity);
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/anki.txt";
//        if(fileName.contains("com.ichi2.anki")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }


    }
    private void showClassName(String pkName, Context context){
        List<String> names = getClassName(pkName,context);
        for(String name:names){
            Log.i("LZH_ClassName",name);
        }
    }
    public List<String > getClassName(String packageName, Context context){
        List<String > classNameList=new ArrayList<String >();
        try {

            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();
                classNameList.add(className);
//                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
//                    classNameList.add(className);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }
    private void writeAnkiClassName(String pkName, Context context,String fileName){
        List<String> names = getClassName(pkName,context);
        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //清空文件
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //添加类名
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for(String name:names){
//                char c = name.charAt(name.length()-1);
//                if(c<='9'&&c>='0'){
//                    continue;
//                }
//                name = name.replace("$",".");
                writer.write(name+"\n");
            }
            writer.flush();
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
