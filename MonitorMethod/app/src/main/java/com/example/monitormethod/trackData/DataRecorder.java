package com.example.monitormethod.trackData;

import android.util.Log;

import com.example.monitormethod.util.LogWriter;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class DataRecorder {
    private static DataRecorder dataRecorder;
    private Hashtable<Integer, WeakReference<Object>> hashtable;
    private int num = 0;
    public DataRecorder(){
        hashtable = new Hashtable<>();
    }
    public static DataRecorder getInstance(){
        if(dataRecorder==null){
            dataRecorder = new DataRecorder();
        }
        return dataRecorder;
    }
    public int addRef(Object o){
        synchronized (DataRecorder.class){
            num++;
            hashtable.put(num,new WeakReference<Object>(o));
        }
        return num;
    }
    public int getRefKey(Object o){
        boolean flag = false;
        int key = -1;
        synchronized (DataRecorder.class){
            Set<Integer> keys = hashtable.keySet();
            Iterator<Integer> iterator =  keys.iterator();
            while (iterator.hasNext()){
                key = iterator.next();
                if(hashtable.get(key).get()==o){
                    flag = true;
                    break;
                }
            }
        }
        if(flag){
            return key;
        }else{
            return -1;
        }
    }

}
