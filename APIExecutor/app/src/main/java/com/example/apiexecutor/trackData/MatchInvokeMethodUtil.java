package com.example.apiexecutor.trackData;

import java.util.ArrayList;
import java.util.List;

class MatchInvokeMethodUtil {
    public static int KMP(String s,String t)
    {
        int MaxSize = Math.max(s.length(),t.length());
        int next[],i=0,j=0;
        next = new int[MaxSize];
        Getnext(next,t);
        while(i<s.length()&&j<t.length())
        {
            if(j==-1 || s.charAt(i)==t.charAt(j))
            {
                i++;
                j++;
            }
            else j=next[j];               //j回退。。。
        }
        if(j>=t.length())
            return (i-t.length());         //匹配成功，返回子串的位置
        else
            return (-1);                  //没找到
    }
    public static void Getnext(int next[], String t)
    {
        int j=0,k=-1;
        next[0]=-1;
        while(j<t.length()-1)
        {
            if(k == -1 || t.charAt(j) == t.charAt(k))
            {
                j++;k++;
                if(t.charAt(j)==t.charAt(k))//当两个字符相同时，就跳过
                    next[j] = next[k];
                else
                    next[j] = k;
            }
            else k = next[k];
        }
    }

    public static boolean checkContains(String src,String target){
        int start = 0,pos = 0;
        String invoke = null;
        List<String> res = transform(target);
        for(;pos<res.size();){
            invoke = res.get(pos);
            if((start=src.indexOf(invoke,start))>=0){
                //确保匹配到的是个完整的方法 类名/方法名
                char endFlag = src.charAt(start+invoke.length());
                //匹配到了相应的方法调用
                if(endFlag==':'||endFlag==')'){
                    start++;
                }else {
                    start++;
                    continue;
                }
                //匹配到方法之后加1
                pos++;
            }else return false;
        }
        return true;
    }
    private static List<String> transform(String target){
        List<String> res = new ArrayList<>();
        int pos = 0;
        while(pos<target.length()&&(pos = target.indexOf("(",pos))>=0){
            pos++;
            int end = target.length(),temp = -1;
            if((temp=target.indexOf(":",pos))>=0){
                end = Math.min(end,temp);
            }
            if((temp = target.indexOf(")",pos))>=0){
                end = Math.min(end,temp);
            }
            String invoke = target.substring(pos,end);
            res.add(invoke);
            pos = end;
        }
        return res;
    }
}
