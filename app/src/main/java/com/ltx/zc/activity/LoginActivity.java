package com.ltx.zc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ltx.zc.R;
import com.ltx.zc.net.BaseResponse;
import com.ltx.zc.net.NetCallBack;
import com.ltx.zc.net.request.LoginReq;
import com.ltx.zc.net.response.LoginResponse;
import com.ltx.zc.utils.ToastTool;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements NetCallBack {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private String READ_CONTACTS = "android.permission.READ_CONTACTS";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainFragmentActivity.class);
        startActivity(intent);
        finish();
    }





    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if(baseRes instanceof LoginResponse){
            LoginResponse lr = (LoginResponse) baseRes;
            if(lr.isSuccess()){

            }
            ToastTool.showShortBigToast(LoginActivity.this,lr.getMessage());
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {

    }



    private void requestLogin(String user,String pwd){
        LoginReq req = new LoginReq();
        req.setNetCallback(this);
        req.setUsername(user);
        req.setPassword(pwd);
        req.addRequest();
    }

}

