package com.ltx.zc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ltx.zc.R;
import com.ltx.zc.activity.PersonMyCommissionActivity;
import com.ltx.zc.activity.PersonMyRecommendActivity;
import com.ltx.zc.activity.PersonSettingActivity;
import com.ltx.zc.adapter.PersonAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentPersonal extends Fragment {

    private ListView person_list;
    private PersonAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        person_list = (ListView) getView().findViewById(R.id.person_list);
    }

    private void init() {
        adapter = new PersonAdapter(getActivity());
        List<String> data = new ArrayList<String>();
        data.add("我的钱包");
        data.add("我的佣金");
        data.add("我的推荐");
        data.add("我的团队");
        data.add("我的收藏");
        data.add("我的圈子");
        data.add("设置");
        person_list.setAdapter(adapter);
        adapter.setData(data);
        person_list.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    break;
                case 1:
                    Intent commission = new Intent();
                    commission.setClass(getActivity(), PersonMyCommissionActivity.class);
                    getActivity().startActivity(commission);
                    break;
                case 2:
                    Intent recommend = new Intent();
                    recommend.setClass(getActivity(), PersonMyRecommendActivity.class);
                    getActivity().startActivity(recommend);
                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:
                    Intent setting = new Intent();
                    setting.setClass(getActivity(), PersonSettingActivity.class);
                    getActivity().startActivity(setting);
                    break;
            }
        }
    };
}
