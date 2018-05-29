package com.clearcrane.musicplayer.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jjy on 2018/5/26.
 */

public class JsonUtil {
    private static final String TAG = "JsonUtil";
    private static final Gson GSON = new GsonBuilder().create();

    public static <T> T json2Obj(String json, Class<T> clazz) {
        Log.v(TAG, "json2Obj: " + json);
        return GSON.fromJson(json, clazz);
    }

    public static <T> List<T> json2List(String json) {
        Log.v(TAG, "json2List: " + json);
        return GSON.fromJson(json, new TypeToken<T>() {
        }.getType());
    }

    public static String obj2Json(Object o) {
        String ret = GSON.toJson(o);
        Log.v(TAG, "obj2Json: " + ret);
        return ret;
    }

    private static JSONObject sObj = new JSONObject();

    public static JSONObject getJsonObject(String json) {
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject setJson(String json) {
        try {
            sObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sObj;
    }

    public static boolean hasKey(String key) {
        return sObj.has(key);
    }

    public static JSONObject put(String key, Object val) {
        try {
            sObj.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sObj;
    }

    public static String toJson() {
        return sObj.toString();
    }
}
