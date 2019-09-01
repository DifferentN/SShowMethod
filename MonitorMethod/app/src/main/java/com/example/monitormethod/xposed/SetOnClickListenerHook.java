package com.example.monitormethod.xposed;

import android.os.Environment;
import android.view.View;

import com.example.monitormethod.dynamicProxy.OnClickListenerProxy;
import com.example.monitormethod.util.LogWriter;

import de.robv.android.xposed.XC_MethodHook;

public class SetOnClickListenerHook extends XC_MethodHook {

    public SetOnClickListenerHook() {
        super();
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
        View view = (View) param.thisObject;
        String packageName = view.getContext().getPackageName();
        OnClickListenerProxy proxy = new OnClickListenerProxy((View.OnClickListener) param.args[0],packageName);
        param.args[0] = proxy;
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
    }
}
