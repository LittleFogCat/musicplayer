package com.clearcrane.musicplayer.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jjy on 2018/6/5.
 * <p>
 * SharedPreference的工具类，使用先调用init()
 */

@SuppressLint("ApplySharedPref")
public class SPUtils {
    private static SPUtils sInstance;
    private SharedPreferences mPreferences;

    private SPUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static SPUtils getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("must call init(context) first");
        }
        return sInstance;
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new SPUtils(context);
        }
    }

    public void save(String key, String val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public void save(String key, int val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public void save(String key, float val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(key, val);
        editor.commit();
    }

    public void save(String key, long val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, val);
        editor.commit();
    }

    public void save(String key, boolean val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public String get(String key, String val) {
        return mPreferences.getString(key, val);
    }

    public int get(String key, int val) {
        return mPreferences.getInt(key, val);
    }

    public float get(String key, float val) {
        return mPreferences.getFloat(key, val);
    }

    public long get(String key, long val) {
        return mPreferences.getLong(key, val);
    }

    public boolean get(String key, boolean val) {
        return mPreferences.getBoolean(key, val);
    }

}
