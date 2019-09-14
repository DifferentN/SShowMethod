package com.example.monitormethod.xposed;

import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.monitormethod.trackData.MyTextWatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class HookOnDraw extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
        View view = (View) param.thisObject;
        if(view!=null&&view instanceof TextView){
            ((TextView) view).addTextChangedListener(new MyTextWatcher(view));
        }
    }
    private boolean checkAdd(TextView view){
        Class clazz = view.getClass();
        try {
            Field field = clazz.getDeclaredField("mListeners");
            field.setAccessible(true);
            ArrayList<TextWatcher> list = (ArrayList<TextWatcher>) field.get(view);
            Object listener;
            if(list==null){
                return false;
            }
            for(int i=0;i<list.size();i++){
                listener = list.get(i);
                if(listener instanceof MyTextWatcher){
//                    Log.i("LZH","有TextWatcher");
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Log.i("LZH","无TextWatcher");
        return false;
    }
}
