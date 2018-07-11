package com.clearcrane.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.clearcrane.musicplayer.R;
import com.clearcrane.musicplayer.common.Constant;
import com.clearcrane.musicplayer.common.ToastUtil;
import com.clearcrane.musicplayer.common.utils.SPUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.clearcrane.musicplayer.common.ToastUtil.LENGTH_SHORT;

public class SettingActivity extends Activity {
    private Button mBtnSave;
    private EditText mEtHome;
    private EditText mEtCacheThreads;
    private Switch mSwLocal;
    private SPUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSpUtils = SPUtils.getInstance();

        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(this::saveSettings);

        mEtHome = findViewById(R.id.etHome);
        mEtHome.setText(mSpUtils.get(Constant.SP_KEY_HOME_URL, Constant.DEFAULT_WS_ADDR));

        mEtCacheThreads = findViewById(R.id.etCacheThreads);
        mEtCacheThreads.setText(String.valueOf(mSpUtils.get(Constant.SP_KEY_CACHE_THREADS, Constant.DEFAULT_CACHE_THREADS)));

        mSwLocal = findViewById(R.id.swLocal);
        mSwLocal.setChecked(mSpUtils.get("is_local", false));
        mSwLocal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSpUtils.save("is_local", isChecked);
            ToastUtil.show(this, "已保存", LENGTH_SHORT);
        });
    }

    private void saveSettings(View view) {
        String homeUrl = mEtHome.getText().toString();
        mSpUtils.save(Constant.SP_KEY_HOME_URL, homeUrl);

        int cts = Integer.valueOf(mEtCacheThreads.getText().toString());
        mSpUtils.save(Constant.SP_KEY_CACHE_THREADS, cts);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getApplicationContext().startActivity(intent);
            }
        }, 2000);

        System.exit(0);
    }

}
