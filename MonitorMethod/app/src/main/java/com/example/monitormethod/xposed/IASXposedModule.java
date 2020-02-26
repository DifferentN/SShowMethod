package com.example.monitormethod.xposed;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IASXposedModule implements IXposedHookLoadPackage{
    private HashMap<String,String> hashMap = new HashMap<>();
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.i("LZH","Loaded app: "+lpparam.packageName);
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new ActivityOnCreateHook(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new ActivityOnResumeHook());
//        XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventActivityHook());

        //广播告知当前页面是否已经完成绘制
//        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onDraw",Canvas.class,new EditTextonDrawHook());

        String packageName = "";
        String className = null;
        ClassLoader classLoader = lpparam.classLoader;
        //查看某个页面的方法调用
//        className = "com.douban.movie.activity.MainActivity";
//        className = "com.pwp.activity.CalendarActivity";
//        hook_methods(className,classLoader);

        String classNames = "com.ichi2._class.txt";
        classNames = "anki.txt";
        if(lpparam.packageName.contains("com.ichi2.anki")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.ichi2.anki"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.ichi2.anki"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("com.ichi2.anki"));
            //不设置过滤器
            List<String> list = new ArrayList<>();
//            hook_methods("android.view.View",lpparam.classLoader,"com.ichi2.anki");
            hookAPPMethod(classNames,classLoader,"com.ichi2.anki",list);

//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        //监听豆瓣电影的方法调用
        classNames = "douban.txt";
        if(lpparam.packageName.contains("com.douban.movie")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.douban.movie"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("com.douban.movie"));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},"com.douban.movie"));
            List<String> filter = new ArrayList<>();
            filter.add("douban");
//            filter.add("android");
            //设置监听的应用方法
            hookAPPMethod(classNames,classLoader,"com.douban.movie",filter);

//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        classNames = "qqmusic.txt";
        if(lpparam.packageName.contains("com.tencent.qqmusic")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.tencent.qqmusic"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.tencent.qqmusic"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("com.tencent.qqmusic"));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},"com.tencent.qqmusic"));
            List<String> filter = new ArrayList<>();
            filter.add("qqmusic");
//            filter.add("android");
            //设置监听的应用方法
            hookAPPMethod(classNames,classLoader,"com.tencent.qqmusic",filter);

//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }

        //监听食谱大全的方法调用
        classNames = "shipudaquan.txt";
        if(lpparam.packageName.contains("com.jnzc.shipudaquan")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.jnzc.shipudaquan"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.jnzc.shipudaquan"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("com.jnzc.shipudaquan"));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},"com.jnzc.shipudaquan"));
            List<String> filter = new ArrayList<>();
            filter.add("shipudaquan");
            filter.add("amodule");
            hookAPPMethod(classNames,classLoader,"com.jnzc.shipudaquan",filter);
        }
        classNames = "jiachangcai.txt";
        if(lpparam.packageName.contains("cn.ecook.jiachangcai")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"cn.ecook.jiachangcai"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("cn.ecook.jiachangcai"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("cn.ecook.jiachangcai"));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},"cn.ecook.jiachangcai"));
            List<String> filter = new ArrayList<>();
            filter.add("jiachangcai");
            hookAPPMethod(classNames,classLoader,"cn.ecook.jiachangcai",filter);
        }

        if (lpparam.packageName.equals("cn.cuco")) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.i("LZH","hook classLoader");
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader =context.getClassLoader();
                            XposedHelpers.findAndHookMethod("android.app.Activity",classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"cn.cuco"));
                            XposedHelpers.findAndHookMethod("android.view.View",classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("cn.cuco"));
                            XposedHelpers.findAndHookMethod("android.view.View", classLoader, "onDraw",Canvas.class, new HookOnDraw("cn.cuco"));
                            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", classLoader, "commitText",CharSequence.class, int.class,
                                    new TrackMethod(new Class[]{CharSequence.class, int.class},"cn.cuco"));
                            List<String> filter = new ArrayList<>();
                            filter.add("cuco");
                            //设置监听的应用方法
                            hookAPPMethod("cuco.txt",classLoader,"cn.cuco",filter);

                        }
                    });
        }
        if (lpparam.packageName.equals("com.zhangshangjianzhi.newapp")) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.i("LZH","hook classLoader");
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader =context.getClassLoader();
                            XposedHelpers.findAndHookMethod("android.app.Activity",classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.zhangshangjianzhi.newapp"));
                            XposedHelpers.findAndHookMethod("android.view.View",classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.zhangshangjianzhi.newapp"));
                            XposedHelpers.findAndHookMethod("android.view.View", classLoader, "onDraw",Canvas.class, new HookOnDraw("com.zhangshangjianzhi.newapp"));
                            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", classLoader, "commitText",CharSequence.class, int.class,
                                    new TrackMethod(new Class[]{CharSequence.class, int.class},"com.zhangshangjianzhi.newapp"));
                            List<String> filter = new ArrayList<>();
                            filter.add("zhangshangjianzhi");
                            //设置监听的应用方法
                            hookAPPMethod("zhangshangjianzhi.txt",classLoader,"com.zhangshangjianzhi.newapp",filter);

                        }
                    });
        }
        if (lpparam.packageName.equals("com.eusoft.eudic")) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.i("LZH","hook classLoader");
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader =context.getClassLoader();
                            XposedHelpers.findAndHookMethod("android.app.Activity",classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"com.eusoft.eudic"));
                            XposedHelpers.findAndHookMethod("android.view.View",classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.eusoft.eudic"));
                            XposedHelpers.findAndHookMethod("android.view.View", classLoader, "onDraw",Canvas.class, new HookOnDraw("com.eusoft.eudic"));
                            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", classLoader, "commitText",CharSequence.class, int.class,
                                    new TrackMethod(new Class[]{CharSequence.class, int.class},"com.eusoft.eudic"));
                            List<String> filter = new ArrayList<>();
                            filter.add("eusoft");
                            //设置监听的应用方法
                            hookAPPMethod("eudic.txt",classLoader,"com.eusoft.eudic",filter);

                        }
                    });
        }
        classNames = "yst.txt";
        if(lpparam.packageName.contains("yst.apk")){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},"yst.apk"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("yst.apk"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw("yst.apk"));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},"yst.apk"));
            List<String> filter = new ArrayList<>();
            filter.add("yst");
            hookAPPMethod(classNames,classLoader,"yst.apk",filter);
        }
        //监听环宇浏览器的方法调用
        classNames = "huanyubrowser.txt";
        packageName = "com.cqrenyi.huanyubrowser";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("huanyubrowser");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        //
        classNames = "yr.txt";
        packageName = "com.yr.qmzs";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("yr");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "jrtd.txt";
        packageName = "com.jrtd.mfxszq";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("dzbook");
            filter.add("dz");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "pris.txt";
        packageName = "com.netease.pris";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("pris");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "wondertek.txt";
        packageName = "com.wondertek.paper";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("wondertek");
            filter.add("paper");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "infzm.txt";
        packageName = "com.infzm.ireader";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("infzm");
            filter.add("ireader");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "ifeng.txt";
        packageName = "com.ifeng.news2";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("ifeng");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "duxiaoman.txt";
        packageName = "com.duxiaoman.umoney";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("duxiaoman");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "boohee.txt";
        packageName = "com.boohee.food";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("boohee");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "dailyyoga.txt";
        packageName = "com.dailyyoga.cn";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("dailyyoga");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "booheeone.txt";
        packageName = "com.boohee.one";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("boohee");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
        classNames = "starbucks.txt";
        packageName = "com.starbucks.cn";
        if(lpparam.packageName.contains(packageName)){
            XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new TrackMethod(new Class[]{MotionEvent.class},packageName));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook(packageName));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw(packageName));
            XposedHelpers.findAndHookMethod("android.view.inputmethod.BaseInputConnection", lpparam.classLoader, "commitText",CharSequence.class, int.class,
                    new TrackMethod(new Class[]{CharSequence.class, int.class},packageName));
            List<String> filter = new ArrayList<>();
            filter.add("starbucks");
            hookAPPMethod(classNames,classLoader,packageName,filter);
        }
    }
    private void hook_methods(String className,ClassLoader loader,String packageName) {
        try {
            Class<?> clazz = loader.loadClass(className);
//            Class<?> clazz = Class.forName(className);
            if(clazz.isInterface()||clazz.isEnum()||clazz.isAnnotation()||clazz.isArray()||clazz.isAnonymousClass()||clazz.isLocalClass()||clazz.isMemberClass()){
                return;
            }
            Method methods[] = clazz.getDeclaredMethods();
            for (Method method : methods) {
                int methodId = method.getModifiers();

                if(Modifier.isAbstract(methodId)||Modifier.isInterface(methodId)||Modifier.isNative(methodId)){
                    continue;
                }
            //如果 （通过反射找到的方法名和准备hook的方法名相同 && 方法判定如果整数参数包含abstract修饰符，则返回true，否则返回false &&
            // 方法判断如果给定参数包含public修饰符，则返回true，否则返回false )
            //Modifier.isPublic(method.getModifiers())

                if (true) {
                    XposedBridge.hookMethod(method, new TrackMethod(method.getParameterTypes(),packageName));
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            Log.i("LZH",className+" error: "+e.getMessage());
        }
    }
    private void hook_methods(Class clazz,String packageName){
        Log.i("LZH","prepate hook: "+clazz.getName());
        try {
            if(clazz.isInterface()||clazz.isEnum()||clazz.isAnnotation()||clazz.isArray()||clazz.isAnonymousClass()||clazz.isLocalClass()||clazz.isMemberClass()){
                return;
            }
            Method methods[] = clazz.getDeclaredMethods();
            for (Method method : methods) {
                int methodId = method.getModifiers();

                if(Modifier.isAbstract(methodId)||Modifier.isInterface(methodId)||Modifier.isNative(methodId)){
                    continue;
                }
                //如果 （通过反射找到的方法名和准备hook的方法名相同 && 方法判定如果整数参数包含abstract修饰符，则返回true，否则返回false &&
                // 方法判断如果给定参数包含public修饰符，则返回true，否则返回false )
                //Modifier.isPublic(method.getModifiers())

                if (true) {
                    XposedBridge.hookMethod(method, new TrackMethod(method.getParameterTypes(),packageName));
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            Log.i("LZH",clazz.getName()+" error: "+e.getMessage());
        }
    }
    private void hookAPPMethod(String filename,ClassLoader classLoader,String packageName,List<String> filters){
        List<String> names = getClassName(filename);
        int sum = names.size();
        int num = 0,skipTime = 0;
        String last = "";
        for(String line :names){
            if(line.contains("android.widget")){
                Log.i("LZH","contain: "+line);
            }
            if(line.startsWith("android.support")||line.startsWith("dalvik")||line.startsWith("java")
                    ||line.startsWith("timber")||line.startsWith("androidx")
                    ||line.startsWith("com.brsanthu")){
                continue;
            }
            //根据filter进行过滤,line中有filter中字符串的通过
            boolean isSkip = false;
            for (String item:filters){
                if(!line.contains(item)){
                    isSkip = true;
                }else{
                    isSkip = false;
                    break;
                }
            }
            if(isSkip){
                continue;
            }
            if(line.contains("FordManager$b")){
                //针对QQ音乐出错纠正
                continue;
            }
            if(line.contains("com.ss.android.ugc.aweme.R")||
                line.contains("com.ss.android.ugc.aweme_push_lib.R")){
                //针对抖音出错纠正
                continue;
            }
//            Log.i("LZH",line);
            hook_methods(line,classLoader,packageName);
            num++;
            //可以监听的方法有限，对于有些应用，它的方法不能全部监听
            if(num>=5000){//7000 5000
                break;
            }
            last = line;
        }
        Log.i("LZH","last: "+last);
    }
    private List<String> getClassName(String name){
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+name;
        File file = new File(fileName);
        List<String> list = new ArrayList<>();
        String line = null;
        if(!file.exists()){
            Log.i("LZH","类名文件不存在");
        }
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            while ((line=reader.readLine())!=null){
                list.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
