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
public class SuggestActivity extends Activity implements View.OnClickListener {

    private TextView hint;
    private Button confirm;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_suggest);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        hint = (TextView) findViewById(R.id.suggest_edit_hint);
        editText = (EditText) findViewById(R.id.suggest_edit);
        confirm = (Button) findViewById(R.id.suggest_confirm);

        confirm.setOnClickListener(this);
        editText.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {

        private CharSequence beforeEditString;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeEditString = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == null) {
                return;
            }
            int length = s.toString().length();
            hint.setText(length + "/120字");
            if (length > 10) {
                hint.setTextColor(getResources().getColor(R.color.textred));
            }else{
                hint.setTextColor(getResources().getColor(R.color.textgrey));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggest_confirm:

                break;
        }
    }
}
