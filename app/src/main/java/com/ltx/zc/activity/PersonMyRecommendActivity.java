package com.ltx.zc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.ltx.zc.R;
import com.ltx.zc.adapter.PersonMyRecommendAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-01.
 */
public class PersonMyRecommendActivity extends Activity {

    private ListView list;
    private PersonMyRecommendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_myrecommend);
        initViews();
        init();
    }

    private void initViews() {
        list = (ListView) findViewById(R.id.person_myrecommend_list);
    }

    private void init() {
        adapter = new PersonMyRecommendAdapter(this);
        list.setAdapter(adapter);
        List<String> data = new ArrayList<>();
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        adapter.setData(data);
    }
}
