package com.dianping.btmovie.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by oceanzhang on 16/1/13.
 */
public class SharedPreferences {
    static ConcurrentHashMap<String,SharedPreferences> instances = new ConcurrentHashMap<String, SharedPreferences>();
    JSONObject object;
    private String name;
    private String filePath;
    public SharedPreferences(String name) {
        this.name = name;
        this.filePath =SharedPreferences.class.getClassLoader().getResource("").getPath();
        try {
            String data = IOUtils.readFile(filePath+name);
            this.object = JSON.parseObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(object == null) {
            this.object = new JSONObject();
        }
    }

    public static SharedPreferences getInstance(String name){
        SharedPreferences s = instances.get(name);
        if(s == null){
            s = new SharedPreferences(name);
            instances.put(name,s);
        }
        return s;
    }

    public String getString(String name,String defaultVaule){
        String value = object.getString(name);
        return value == null ? defaultVaule : value;
    }

    public SharedPreferences putString(String name,String value){
        this.object.put(name,value);
        return this;
    }
    public SharedPreferences remove(String name){
        object.remove(name);
        return this;
    }
    public void commit(){
        try {
            IOUtils.writeFile(filePath+name,object.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
