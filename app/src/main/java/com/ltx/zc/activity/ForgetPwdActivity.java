package com.ltx.zc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ltx.zc.R;


/**
 * Created by Administrator on 2017-03-02.
 */
public class ForgetPwdActivity extends Activity implements View.OnClickListener {

    private EditText telephone;
    private EditText smsCode;
    private EditText newPwd;
    private EditText newPwdTwice;
    private Button obtainSmsCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgetpwd);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        telephone = (EditText) findViewById(R.id.forgetpwd_telephone_number);
        smsCode = (EditText) findViewById(R.id.forgetpwd_sms_verifycode);
        newPwd = (EditText) findViewById(R.id.forgetpwd_new_pwd);
        newPwdTwice = (EditText) findViewById(R.id.forgetpwd_new_pwd_twice);
        obtainSmsCode = (Button) findViewById(R.id.forgetpwd_obtain_verifycode);
        findViewById(R.id.forgetpwd_confirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetpwd_confirm:

                break;
        }
    }
}
