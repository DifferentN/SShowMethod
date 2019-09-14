package com.example.monitormethod.xposed;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IASXposedModule implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.i("LZH","Loaded app: "+lpparam.packageName);

        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new ActivityOnCreateHook(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new ActivityOnResumeHook());
        XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventActivityHook());

        //广播告知当前页面是否已经完成绘制
//        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onDraw",Canvas.class,new EditTextonDrawHook());

        String className = null;
        ClassLoader classLoader = lpparam.classLoader;
        //查看某个页面的方法调用
//        className = "com.douban.movie.activity.MainActivity";
//        className = "com.pwp.activity.CalendarActivity";
//        hook_methods(className,classLoader);

        String classNames = "com.ichi2._class.txt";
        classNames = "anki.txt";
        if(lpparam.packageName.contains("com.ichi2.anki")){
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.ichi2.anki"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.ichi2.anki"));
            //下面这个报错
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClickInternal",new TrackMethod(new Class[0],"com.ichi2.anki"));
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.ichi2.anki"));
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());

//            hook_methods("android.view.View",lpparam.classLoader,"com.ichi2.anki");
            hookAPPMethod(classNames,classLoader,"com.ichi2.anki");

            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        classNames = "tianxiameishi.txt";
        classNames = "douban.txt";
        if(lpparam.packageName.contains("com.douban.movie")){
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.widget.EditText",lpparam.classLoader,"getText",new TestGetTextHook());
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
            hookAPPMethod(classNames,classLoader,"com.douban.movie");

//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
//            XposedHelpers.findAndHookMethod("android.widget.EditText", lpparam.classLoader,"setText",CharSequence.class,new TrackMethod(new Class[]{CharSequence.class}));
        }
        classNames = "shipudaquan.txt";
        if(lpparam.packageName.contains("com.jnzc.shipudaquan")){
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.jnzc.shipudaquan"));
//            XposedHelpers.findAndHookMethod("android.widget.EditText",lpparam.classLoader,"getText",new TestGetTextHook());
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.jnzc.shipudaquan"));
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
            hookAPPMethod(classNames,classLoader,"com.jnzc.shipudaquan");

//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
//            XposedHelpers.findAndHookMethod("android.widget.EditText", lpparam.classLoader,"setText",CharSequence.class,new TrackMethod(new Class[]{CharSequence.class}));
        }

//        classNames = "kingsoft.txt";
//        if(lpparam.packageName.contains("com.kingsoft")){
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.kingsoft"));
////            XposedHelpers.findAndHookMethod("android.widget.EditText",lpparam.classLoader,"getText",new TestGetTextHook());
////            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
////            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.jnzc.shipudaquan"));
////            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
//            hookAPPMethod(classNames,classLoader,"com.kingsoft");
//
////            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
////            XposedHelpers.findAndHookMethod("android.widget.EditText", lpparam.classLoader,"setText",CharSequence.class,new TrackMethod(new Class[]{CharSequence.class}));
//        }
        classNames = "dict.txt";
        if(lpparam.packageName.contains("com.ltz.dict")){
            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.ltz.dict"));
//            XposedHelpers.findAndHookMethod("android.widget.EditText",lpparam.classLoader,"getText",new TestGetTextHook());
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.jnzc.shipudaquan"));
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
            hookAPPMethod(classNames,classLoader,"com.ltz.dict");

//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
//            XposedHelpers.findAndHookMethod("android.widget.EditText", lpparam.classLoader,"setText",CharSequence.class,new TrackMethod(new Class[]{CharSequence.class}));
        }
    }
    private void hook_methods(String className,ClassLoader loader,String packageName) {

        try {
//            Class<?> clazz = Class.forName(className); //反射
            Class<?> clazz = loader.loadClass(className);
            if(clazz.isInterface()||clazz.isEnum()||clazz.isAnnotation()||clazz.isArray()||clazz.isAnonymousClass()||clazz.isLocalClass()||clazz.isMemberClass()){
                return;
            }
            Method methods[] = clazz.getDeclaredMethods();
            for (Method method : methods) {
                int methodId = method.getModifiers();

                if(Modifier.isAbstract(methodId)||Modifier.isInterface(methodId)||Modifier.isNative(methodId)){
                    continue;
                }
//                Log.i("LZH", "get " + method.getName());

            //如果 （通过反射找到的方法名和准备hook的方法名相同 && 方法判定如果整数参数包含abstract修饰符，则返回true，否则返回false &&
            // 方法判断如果给定参数包含public修饰符，则返回true，否则返回false )
            //Modifier.isPublic(method.getModifiers())

                if (true) {
//                    Log.i("LZH",className+" method: "+method.getName()+"1");
                    XposedBridge.hookMethod(method, new TrackMethod(method.getParameterTypes(),packageName));
//                    Log.i("LZH",className+" method: "+method.getName()+"2");
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            Log.i("LZH",className+" error: "+e.getMessage());
        }
    }
    private void hookAPPMethod(String filename,ClassLoader classLoader,String packageName){
        List<String> names = getClassName(filename);
        int sum = names.size();
        int num = 0;
        String last = "";
        for(String line :names){
//            if(line.contains("$")){
//                continue;
//            }
            if(line.contains("android.widget")){
                Log.i("LZH","contain: "+line);
            }
            if(line.startsWith("android.support")||line.startsWith("dalvik")||line.startsWith("java")||line.startsWith("timber")||line.startsWith("androidx")){
                continue;
            }
//            if(!line.startsWith(packageName)){
//                continue;
//            }
//            if(line.contains("com.douban.rexxar.view")||line.contains("com.douban.richeditview")||line.contains("com.douban.videouploader")||line.contains("com.douban.zeno")){
//                continue;
//            }
            hook_methods(line,classLoader,packageName);
            num++;
            if(num>=7000){//7000
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
