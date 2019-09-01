package com.example.apiexecutor.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CoordinatorReceiver extends BroadcastReceiver {
    public static final String ON_RESUME = "ON_RESUME";
    public static final String RESUME_ACTIVITY = "RESUME_ACTIVITY";
    public static final String EXECUTE_METHOD = "EXECUTE_METHOD";
    public static final String METHODS = "METHODS";

    private Coordinator coordinator;
    public  boolean isIntent = false,isMethod = false,start = false;
    private String showActivityName;
    public CoordinatorReceiver(Coordinator coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        final Context context1 = context;
        switch (action){
            case ON_RESUME:
                showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
                Log.i("LZH","showActivityName: "+showActivityName);
                if(!start){
                    break;
                }
                if(!isIntent){
                    if(showActivityName.startsWith(coordinator.getPackageName())){
                        coordinator.sendIntentToOpenActivity(context);
                        isIntent = true;
                    }
                }else if(!isMethod){
                    if(!showActivityName.contains("Reviewer")){
                        break;
                    }
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String methods = coordinator.getMethods();
                            Intent intent1 =new Intent();
                            intent1.setAction(EXECUTE_METHOD);
                            intent1.putExtra(METHODS,methods);
                            context1.sendBroadcast(intent1);
                            isMethod = true;
                        }
                    });
                    isMethod = true;
                    thread.start();
                }
                break;

        }
    }
}
