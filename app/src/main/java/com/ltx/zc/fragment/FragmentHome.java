package com.ltx.zc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ltx.zc.R;
import com.ltx.zc.view.BannerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

	private BannerView bannerView;
	private LinearLayout bannerlayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		init();
	}

	private void initView() {
		// 初始化
		bannerlayout = (LinearLayout) getView().findViewById(R.id.bannerlayout);
		bannerView = new BannerView(getActivity());
		bannerlayout.addView(bannerView);
	}

	private void init() {
		String img0 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488276809165&di=55813226b4a12d8cfb3a870712762d47&imgtype=0&src=http%3A%2F%2Ftupian.enterdesk.com%2F2012%2F0423%2F74%2F4.jpg";
		List<String> img = new ArrayList<String>();
		img.add(img0);
		img.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488276809163&di=bf1475a7efb1f8640484253ab7daa7e8&imgtype=0&src=http%3A%2F%2Fphoto.iyaxin.com%2Fattachement%2Fjpg%2Fsite2%2F20121112%2Fd4bed9e6027f120a9c1b39.jpg");
		bannerView.setList(img);
	}

}
