package com.clearcrane.musicplayer.common;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    public static void show(Context context, String s, int length) {
        Toast.makeText(context, s, length).show();
    }
}
