package com.ltx.zc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ltx.zc.R;
import com.ltx.zc.net.BaseResponse;
import com.ltx.zc.net.NetCallBack;
import com.ltx.zc.net.request.LoginReq;
import com.ltx.zc.net.response.LoginResponse;
import com.ltx.zc.utils.ToastTool;
import com.umeng.analytics.MobclickAgent;


public class LoginActivity extends Activity implements NetCallBack, View.OnClickListener {

    private TextView register, forgetpwd, suggest;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        register = (TextView) findViewById(R.id.login_register);
        forgetpwd = (TextView) findViewById(R.id.login_forgetpwd);
        suggest = (TextView) findViewById(R.id.login_suggest);
        login = (Button) findViewById(R.id.login);

        register.setOnClickListener(this);
        forgetpwd.setOnClickListener(this);
        suggest.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        MobclickAgent.onProfileSignIn("0000");
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainFragmentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof LoginResponse) {
            LoginResponse lr = (LoginResponse) baseRes;
            if (lr.isSuccess()) {

            }
            ToastTool.showShortBigToast(LoginActivity.this, lr.getMessage());
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {

    }

    private void requestLogin(String user, String pwd) {
        LoginReq req = new LoginReq();
        req.setNetCallback(this);
        req.setUsername(user);
        req.setPassword(pwd);
        req.addRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_forgetpwd:
                Intent forgetpwd = new Intent();
                forgetpwd.setClass(this, ForgetPwdActivity.class);
                startActivity(forgetpwd);
                break;
            case R.id.login_suggest:
                Intent suggest = new Intent();
                suggest.setClass(this, SuggestActivity.class);
                startActivity(suggest);
                break;

            case R.id.login_register:
                Intent register = new Intent();
                register.setClass(this, RegisterActivity.class);
                startActivity(register);
                break;

            case R.id.login:
                attemptLogin();
                break;
        }
    }
}

