package com.example.monitormethod.xposed;

import androidx.appcompat.widget.SearchView;

import com.example.monitormethod.trackData.QueryTextListenerWrapper;

import de.robv.android.xposed.XC_MethodHook;

public class QueryTextSubmitHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
        SearchView searchView = (SearchView) param.thisObject;
        SearchView.OnQueryTextListener onQueryTextListener = (SearchView.OnQueryTextListener) param.args[0];
        if(onQueryTextListener instanceof QueryTextListenerWrapper){
            return;
        }
        QueryTextListenerWrapper wrapper = new QueryTextListenerWrapper(searchView,onQueryTextListener);
        searchView.setOnQueryTextListener(wrapper);
    }
}
