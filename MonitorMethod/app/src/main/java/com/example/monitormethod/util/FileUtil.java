package com.example.monitormethod.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class FileUtil {
    /**
     * 将页面内容写到文件中
     * @param fileName
     * @param pageContent
     */
    public static void writePageContent(final String fileName,final HashMap<String,String>pageContent){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = transformToJSON(pageContent);
                writeJSONObject(fileName,jsonObject);
            }
        });
        thread.start();
    }
    public static JSONObject transformToJSON(HashMap<String, String> pageContent){
        JSONObject pageJson = new JSONObject();
        for(String key:pageContent.keySet()){
            pageJson.put(key,pageContent.get(key));
        }
        return pageJson;
    }
    public static JSONObject readJSONObject(File file){
        JSONObject res = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder strBuilder = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null){
                strBuilder.append(line);
            }
            res = JSONObject.parseObject(strBuilder.toString());
            fileInputStream.close();
            inputStreamReader.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void writeJSONObject(String path, JSONObject jsonObject){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            String content = jsonObject.toJSONString();
            writer.write(content);

            fileOutputStream.flush();
            outputStreamWriter.flush();
            writer.flush();
            fileOutputStream.close();
            outputStreamWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
