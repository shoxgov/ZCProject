package com.ltx.zc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2017-03-02.
 */
public class PersonSettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
