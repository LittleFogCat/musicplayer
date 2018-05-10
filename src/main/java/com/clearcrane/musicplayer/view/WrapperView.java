package com.clearcrane.musicplayer.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by jjy on 2018/5/9.
 */

public class WrapperView extends View implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final String TAG = "WrapperView";

    private int[] mLoc = new int[2];
    private long mLastChange;
    private Handler mHandler = new Handler();
    private Runnable dismiss = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: " + System.currentTimeMillis() + "," + mLastChange);
            if (System.currentTimeMillis() - mLastChange >= 2500) {
                setVisibility(INVISIBLE);
            } else {
                mHandler.postDelayed(this, System.currentTimeMillis() - mLastChange + 3000);
            }
        }
    };

    public WrapperView(Context context) {
        super(context);
    }

    public WrapperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        newFocus.getLocationInWindow(mLoc);

        View parent = (View) getParent();
        int[] parentLoc = new int[2];
        parent.getLocationInWindow(parentLoc);

        setX(mLoc[0] - parentLoc[0]);
        setY(mLoc[1] - parentLoc[1]);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = newFocus.getWidth();
        params.height = newFocus.getHeight();

        setLayoutParams(params);
    }
}
