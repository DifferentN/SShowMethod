package com.example.apiexecutor.xposed;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;

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
//        XposedHelpers.findAndHookMethod("android.app.Activity",lpparam.classLoader,"dispatchTouchEvent",MotionEvent.class,new DispatchTouchEventActivityHook());
        XposedHelpers.findAndHookMethod("android.view.ViewRootImpl", lpparam.classLoader, "performTraversals", new ViewRootImplPerformTraversalsHook());
        //广播告知当前页面是否已经完成绘制
//        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onDraw",Canvas.class,new EditTextonDrawHook());
//        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent", MotionEvent.class,new DispatchTouchEventActivityHook());
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "finish", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i("LZH","finish: "+param.thisObject.getClass().getName());
            }
        });
        XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent", MotionEvent.class,new DispatchTouchEventActivityHook());
        String className = null;
        ClassLoader classLoader = lpparam.classLoader;
        //查看某个页面的方法调用
//        className = "com.douban.movie.activity.MainActivity";
//        className = "com.pwp.activity.CalendarActivity";
//        hook_methods(className,classLoader);

        String classNames = "com.ichi2._class.txt";
        classNames = "anki.txt";
        if(lpparam.packageName.contains("com.ichi2.anki")){
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new ActivityOnDraw());
//            hook_methods("android.view.View",lpparam.classLoader,"com.ichi2.anki");
            ArrayList<String> list = new ArrayList<>();
            hookAPPMethod(classNames,classLoader,"com.ichi2.anki",list);
            Log.i("LZH","hook anki");
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        classNames = "douban.txt";
        if(lpparam.packageName.contains("com.douban.movie")){
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"dispatchTouchEvent", MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.widget.EditText",lpparam.classLoader,"getText",new TestGetTextHook());
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"onTouchEvent",MotionEvent.class,new DispatchTouchEventHook("com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"performClick",new TrackMethod(new Class[0],"com.douban.movie"));
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new HookOnDraw());
            ArrayList<String> filter = new ArrayList<>();
            filter.add("douban");
            hookAPPMethod(classNames,classLoader,"com.douban.movie",filter);

//            XposedHelpers.findAndHookMethod("android.view.View",lpparam.classLoader,"setOnClickListener", View.OnClickListener.class,new SetOnClickListenerHook());
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
//            XposedHelpers.findAndHookMethod("android.widget.EditText", lpparam.classLoader,"setText",CharSequence.class,new TrackMethod(new Class[]{CharSequence.class}));
        }
        classNames = "shipudaquan.txt";
        if(lpparam.packageName.contains("com.jnzc.shipudaquan")){
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "onDraw",Canvas.class, new ActivityOnDraw());
//            hook_methods("android.view.View",lpparam.classLoader,"com.ichi2.anki");
            List<String> filter = new ArrayList<>();
            filter.add("shipudaquan");
            filter.add("amodule");
            hookAPPMethod(classNames,classLoader,"com.jnzc.shipudaquan",filter);
//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        classNames = "qqmusic.txt";
        if(lpparam.packageName.contains("com.tencent.qqmusic")){

            List<String> filter = new ArrayList<>();
            filter.add("qqmusic");
//            filter.add("android");
            //设置监听的应用方法
            hookAPPMethod(classNames,classLoader,"com.tencent.qqmusic",filter);

//            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader,"findViewById",int.class,new FindViewByIdHook());
        }
        classNames = "mooc.txt";
        if(lpparam.packageName.contains("cn.com.open.mooc")){
            List<String> filter = new ArrayList<>();
            filter.add("mooc");
            hookAPPMethod(classNames,classLoader,"cn.com.open.mooc",filter);
        }
//        if (lpparam.packageName.equals("cn.cuco")) {
//            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
//                    "attachBaseContext", Context.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Log.i("LZH","hook classLoader");
//                            Context context = (Context) param.args[0];
//                            ClassLoader classLoader =context.getClassLoader();
//                            List<String> filter = new ArrayList<>();
//                            filter.add("cuco");
//                            //设置监听的应用方法
//                            hookAPPMethod("cuco.txt",classLoader,"cn.cuco",filter);
//
//                        }
//                    });
//        }
        if (lpparam.packageName.equals("com.zhangshangjianzhi.newapp")) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.i("LZH","hook classLoader");
                            Context context = (Context) param.args[0];
                            ClassLoader classLoader =context.getClassLoader();

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
                            List<String> filter = new ArrayList<>();
                            filter.add("eusoft");
                            //设置监听的应用方法
                            hookAPPMethod("eudic.txt",classLoader,"com.eusoft.eudic",filter);

                        }
                    });
        }
        classNames = "naman14.txt";
        if(lpparam.packageName.contains("com.naman14.timberx")){
            List<String> filter = new ArrayList<>();
            filter.add("naman14");
            hookAPPMethod(classNames,classLoader,"com.naman14.timberx",filter);
        }
        classNames = "yst.txt";
        if(lpparam.packageName.contains("yst.apk")){
            List<String> filter = new ArrayList<>();
            filter.add("yst");
            hookAPPMethod(classNames,classLoader,"yst.apk",filter);
        }
        classNames = "huanyubrowser.txt";
        if(lpparam.packageName.contains("com.cqrenyi.huanyubrowser")){
            List<String> filter = new ArrayList<>();
            filter.add("huanyubrowser");
            hookAPPMethod(classNames,classLoader,"com.cqrenyi.huanyubrowser",filter); }

        classNames = "yr.txt";
        if(lpparam.packageName.contains("com.yr.qmzs")){
            List<String> filter = new ArrayList<>();
            filter.add("yr");
            hookAPPMethod(classNames,classLoader,"com.yr.qmzs",filter); }

        classNames = "jrtd.txt";
        if(lpparam.packageName.contains("com.jrtd.mfxszq")){
            List<String> filter = new ArrayList<>();
            filter.add("dzbook");
            filter.add("dz");
            hookAPPMethod(classNames,classLoader,"com.jrtd.mfxszq",filter); }
        //bubei.tingshu
        classNames = "pris.txt";
        if(lpparam.packageName.contains("com.netease.pris")){
            List<String> filter = new ArrayList<>();
            filter.add("pris");
            hookAPPMethod(classNames,classLoader,"com.netease.pris",filter); }
        classNames = "wondertek.txt";
        if(lpparam.packageName.contains("com.wondertek.paper")){
            List<String> filter = new ArrayList<>();
            filter.add("wondertek");
            filter.add("paper");
            hookAPPMethod(classNames,classLoader,"com.wondertek.paper",filter); }
        classNames = "infzm.txt";
        if(lpparam.packageName.contains("com.infzm.ireader")){
            List<String> filter = new ArrayList<>();
            filter.add("infzm");
            filter.add("ireader");
            hookAPPMethod(classNames,classLoader,"com.infzm.ireader",filter); }
        classNames = "ifeng.txt";
        if(lpparam.packageName.contains("com.ifeng.news2")){
            List<String> filter = new ArrayList<>();
            filter.add("ifeng");
            hookAPPMethod(classNames,classLoader,"com.ifeng.news2",filter); }
        classNames = "duxiaoman.txt";
        if(lpparam.packageName.contains("com.duxiaoman.umoney")){
            List<String> filter = new ArrayList<>();
            filter.add("duxiaoman");
            hookAPPMethod(classNames,classLoader,"com.duxiaoman.umoney",filter); }
        classNames = "boohee.txt";
        if(lpparam.packageName.contains("com.boohee.food")){
            List<String> filter = new ArrayList<>();
            filter.add("boohee");
            hookAPPMethod(classNames,classLoader,"com.boohee.food",filter); }
        classNames = "booheeone.txt";
        if(lpparam.packageName.contains("com.boohee.one")){
            List<String> filter = new ArrayList<>();
            filter.add("boohee");
            hookAPPMethod(classNames,classLoader,"ccom.boohee.one",filter); }
        classNames = "dailyyoga.txt";
        if(lpparam.packageName.contains("com.dailyyoga.cn")){
            List<String> filter = new ArrayList<>();
            filter.add("dailyyoga");
            hookAPPMethod(classNames,classLoader,"com.dailyyoga.cn",filter); }

        classNames = "smartisan.txt";
        if(lpparam.packageName.contains("com.smartisan.notes")){
            List<String> filter = new ArrayList<>();
            filter.add("smartisan");
            hookAPPMethod(classNames,classLoader,"com.smartisan.notes",filter); }

        classNames = "dragon.txt";
        if(lpparam.packageName.contains("com.dragon.read")){
            List<String> filter = new ArrayList<>();
            filter.add("dragon");
            hookAPPMethod(classNames,classLoader,"com.dragon.read",filter);
        }
        classNames = "tingshu.txt";
        if(lpparam.packageName.contains("bubei.tingshu")){
            List<String> filter = new ArrayList<>();
            filter.add("tingshu");
            hookAPPMethod(classNames,classLoader,"bubei.tingshu",filter);
        }
        classNames = "article.txt";
        if(lpparam.packageName.contains("com.ss.android.article.lite")){
            List<String> filter = new ArrayList<>();
            filter.add("article");
            hookAPPMethod(classNames,classLoader,"com.ss.android.article.lite",filter);
        }
        classNames = "news.txt";
        if(lpparam.packageName.contains("com.tencent.news")){
            List<String> filter = new ArrayList<>();
            filter.add("news");
            hookAPPMethod(classNames,classLoader,"com.tencent.news",filter);
        }
        classNames = "sogou.txt";
        if(lpparam.packageName.contains("com.sogou.map.android.maps")){
            List<String> filter = new ArrayList<>();
            filter.add("sogou");
            hookAPPMethod(classNames,classLoader,"com.sogou.map.android.maps",filter);
        }
        classNames = "xiachufang.txt";
        if(lpparam.packageName.contains("com.xiachufang.lazycook")){
            List<String> filter = new ArrayList<>();
            filter.add("xiachufang");
            hookAPPMethod(classNames,classLoader,"com.xiachufang.lazycook",filter);
        }
        classNames = "xiangha.txt";
        if(lpparam.packageName.contains("com.xiangha")){
            List<String> filter = new ArrayList<>();
            filter.add("xiangha");
            hookAPPMethod(classNames,classLoader,"com.xiangha",filter);
        }
    }
    private void hook_methods(String className,ClassLoader loader,String packageName) {

        try {
            Class<?> clazz = loader.loadClass(className);
            if(clazz.isInterface()||clazz.isEnum()||clazz.isAnnotation()||clazz.isArray()||clazz.isAnonymousClass()||clazz.isLocalClass()||clazz.isMemberClass()){
                return;
            }
            if(clazz == null){
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
//            Log.i("LZH",className+" error: "+e.getMessage());
        }
    }
    private void hookAPPMethod(String filename,ClassLoader classLoader,String packageName,List<String> filters){
        List<String> names = getClassName(filename);
        int sum = names.size();
        int num = 0;
        String last = "";
        for(String line :names){
//            if(line.contains("android.widget")){
//                Log.i("LZH","contain: "+line);
//            }
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
                continue;
            }
            if(line.contains("smartisanos.widget.AppLockView$7")||
                    line.contains("smartisanos.widget.AppLockView$8")||
                    line.contains("smartisanos.widget.pinned.SmartisanStackClient$1")){
                //针对锤子便签纠错
                continue;
            }

            hook_methods(line,classLoader,packageName);
            num++;
            //可以监听的方法有限，对于有些应用，它的方法不能全部监听
            if(num>=5000){//7000
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
            Log.i("LZH","类名文件不存在: "+fileName);
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
