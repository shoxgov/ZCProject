package com.ltx.zc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.ltx.zc.R;

/**
 * Created by Administrator on 2017-03-01.
 */
public class PersonMyCommissionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_mycommission);
    }
}
