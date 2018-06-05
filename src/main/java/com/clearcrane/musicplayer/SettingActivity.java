package com.clearcrane.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clearcrane.musicplayer.common.Constant;
import com.clearcrane.musicplayer.common.utils.SPUtils;

public class SettingActivity extends Activity {
    private Button mBtnSave;
    private EditText mEtHome;
    private SPUtils mSpUtils = SPUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(this::saveSettings);
        mEtHome = findViewById(R.id.etHome);
    }

    private void saveSettings(View view) {
        String homeUrl = mEtHome.getText().toString();
        mSpUtils.save(Constant.SP_KEY_HOME_URL, homeUrl);
    }

}
