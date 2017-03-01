package com.ltx.zc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ltx.zc.R;
import com.ltx.zc.adapter.EnrollAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentEnroll extends Fragment {


    private ListView enroll_list;
    private EnrollAdapter enrollAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enroll, container, false);
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

    private void initViews() {
        enroll_list = (ListView) getView().findViewById(R.id.enroll_list);
        enrollAdapter = new EnrollAdapter(getActivity());
        enroll_list.setAdapter(enrollAdapter);
    }

    private void init() {
        List<String> data = new ArrayList<>();
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        enrollAdapter.setData(data);
    }
}
