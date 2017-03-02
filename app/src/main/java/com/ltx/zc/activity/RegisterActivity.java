package com.ltx.zc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ltx.zc.R;


/**
 * Created by Administrator on 2017-03-02.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText telephone;
    private EditText smsCode;
    private EditText newPwd;
    private EditText newPwdTwice;
    private Button obtainSmsCode;
    private EditText invivationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        telephone = (EditText) findViewById(R.id.register_telephone_number);
        smsCode = (EditText) findViewById(R.id.register_sms_verifycode);
        newPwd = (EditText) findViewById(R.id.register_new_pwd);
        newPwdTwice = (EditText) findViewById(R.id.register_new_pwd_twice);
        invivationCode = (EditText) findViewById(R.id.register_invitation_code);
        obtainSmsCode = (Button) findViewById(R.id.register_obtain_verifycode);
        findViewById(R.id.register_confirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_confirm:

                break;
        }
    }
}
