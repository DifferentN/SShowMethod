package com.example.monitormethod.xposed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;



import com.example.monitormethod.receive.LocalActivityReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.TreeMap;

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
        injectReceiver(context, activity);

        Log.i("LZH","after create "+componentName.getClassName());
    }




    private void injectReceiver(Context context, Activity activity) {
        //注册一个广播接收器，可以用来接收指令，这里是用来回去指定view的xpath路径的

        LocalActivityReceiver receiver = new LocalActivityReceiver(activity);
        IntentFilter filter = new IntentFilter();

        filter.addAction(LocalActivityReceiver.intent);

        filter.addAction(LocalActivityReceiver.currentActivity);

        filter.addAction(LocalActivityReceiver.obtainActivityText);
        filter.addAction(LocalActivityReceiver.WRITE_LOG);
        filter.addAction(LocalActivityReceiver.ON_RESUME);

        Object o = XposedHelpers.getAdditionalInstanceField(activity,"iasReceiver");
        if(o!=null){
            return;
        }

        XposedHelpers.setAdditionalInstanceField(activity, "iasReceiver", receiver);
        activity.registerReceiver(receiver,filter);

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/douban.txt";
//        if(activity.getPackageName().contains("com.douban.movie")){
//            writeAnkiClassName("com.douban",activity,fileName);
//            Log.i("LZH","write className finish ");
//        }
        //        com.jnzc.shipudaquan
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/shipudaquan.txt";
//        if(activity.getPackageName().contains("com.jnzc.shipudaquan")){
//            writeAnkiClassName("com.jnzc.shipudaquan",activity,fileName);
//            Log.i("LZH","write className finish ");
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tianxiameishi.txt";
//        if(activity.getPackageName().contains("com.jingdian.tianxiameishi.android")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/anki.txt";
//        if(activity.getPackageName().contains("com.ichi2.anki")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/qqmusic.txt";
//        if(activity.getPackageName().contains("com.tencent.qqmusic")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/jiachangcai.txt";
//        if(activity.getPackageName().contains("cn.ecook.jiachangcai")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/yst.txt";
//        if(activity.getPackageName().contains("yst.apk")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/yr.txt";
//        if(activity.getPackageName().contains("com.yr.qmzs")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/boohee.txt";
//        if(activity.getPackageName().contains("com.boohee.food")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/dragon.txt";
//        if(activity.getPackageName().contains("com.dragon.read")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tingshu.txt";
//        if(activity.getPackageName().contains("bubei.tingshu")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/article.txt";
//        if(activity.getPackageName().contains("com.ss.android.article.lite")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/news.txt";
//        if(activity.getPackageName().contains("com.tencent.news")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sogou.txt";
//        if(activity.getPackageName().contains("com.sogou.map.android.maps")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pris.txt";
//        if(activity.getPackageName().contains("com.netease.pris")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/xiachufang.txt";
//        if(activity.getPackageName().contains("com.xiachufang.lazycook")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/qqmusic.txt";
//        if(activity.getPackageName().contains("com.tencent.qqmusic")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/cloudmusic.txt";
//        if(activity.getPackageName().contains("com.netease.cloudmusic")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/dailyyoga.txt";
//        if(activity.getPackageName().contains("com.dailyyoga.cn")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/musicplayer.txt";
//        if(activity.getPackageName().contains("com.example.musicplayer")){
//            writeAnkiClassName(activity.getPackageName(),activity,fileName);
//        }
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/antennapod.txt";
//        if(activity.getPackageName().contains("de.danoeh.antennapod.debug")){
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
