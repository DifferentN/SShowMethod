package com.example.monitormethod.trackData;

import android.util.Log;

import com.example.monitormethod.util.LogWriter;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;

/**
 * 记录对象的Hashcode(不使用对象的hashcode())
 */
public class DataRecorder {
    private static DataRecorder dataRecorder;
    private Hashtable<Integer, WeakReference<Object>> hashtable;
    private Vector<SaveNode> objList;
    private int num = 0;
    public DataRecorder(){
        hashtable = new Hashtable<>();
        objList = new Vector<>();
    }
    public static DataRecorder getInstance(){
        if(dataRecorder==null){
            dataRecorder = new DataRecorder();
        }
        return dataRecorder;
    }

    /**
     * 添加一个对象引用，并为它分配一个自定义的“hashCode”
     * @param o
     * @return
     */
    public int addRef(Object o){
        synchronized (DataRecorder.class){
            num++;
//            hashtable.put(num,new WeakReference<Object>(o));
            objList.add(new SaveNode(new WeakReference<Object>(o),num));
        }
        return num;
    }

    /**
     * 获取对象的“hashcode”
     * @param o
     * @return
     */
    public int getRefKey(Object o){
        boolean flag = false;
        int key = -1;
        synchronized (DataRecorder.class){
//            Set<Integer> keys = hashtable.keySet();
//            Iterator<Integer> iterator =  keys.iterator();
//            while (iterator.hasNext()){
//                key = iterator.next();
//                if(hashtable.get(key).get()==o){
//                    flag = true;
//                    break;
//                }
//            }
            int i = 0,size = objList.size();
            i = size-1;
            while(i>=0){
                SaveNode saveNode = objList.get(i);
                if(saveNode.weakReference.get()==o){
                    flag = true;
                    break;
                }else if(saveNode.weakReference.get()==null){
                    objList.remove(saveNode);
                }
                i--;
            }
        }
        if(flag){
            return key;
        }else{
            return -1;
        }
    }
    private static class SaveNode{
        public WeakReference<Object> weakReference;
        public int num;
        public SaveNode(WeakReference<Object> weakReference,int num){
            this.weakReference = weakReference;
            this.num = num;
        }
    }

}
