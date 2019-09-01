package com.example.monitormethod.xposed;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class TestGetTextHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
//        Editable editable = new SpannableStringBuilder("99");
//        param.setResult(editable);
        Log.i("LZH","before getText: ");
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
//        Editable editable = new SpannableStringBuilder("99");
//        param.setResult(editable);
        Log.i("LZH","after getText: "+param.getResult());
    }
}
