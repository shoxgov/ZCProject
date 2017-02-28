package com.ltx.zc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ltx.zc.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomActivity extends Activity implements ViewPager.OnPageChangeListener {

    private boolean isFirst;
    private LinearLayout mDotsLayout;
    private ImageView[] mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences pref = getSharedPreferences("first", Activity.MODE_PRIVATE);
        isFirst = pref.getBoolean("status", true);
        if (isFirst) {
            setContentView(R.layout.guideview);
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            mDotsLayout = (LinearLayout) findViewById(R.id.points);
            LayoutInflater inflater = LayoutInflater.from(this);
            ArrayList<View> views = new ArrayList<View>();
            // 初始化引导图片列表
            views.add(inflater.inflate(R.layout.guide_one, null));
            views.add(inflater.inflate(R.layout.guide_two, null));
            views.add(inflater.inflate(R.layout.guide_three, null));
            //初始化Dots
            initIndicator(views.size());
            // 初始化Adapter
            ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views);
            viewPager.setAdapter(vpAdapter);
            // 绑定回调
            viewPager.setOnPageChangeListener(this);
        } else {
            setContentView(R.layout.welcom);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            gotoMainActivity();
        }
        pref.edit().putBoolean("status",false).commit();
    }

    private void gotoMainActivity() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent();
                i.putExtra("updateLogin", true);
                i.setClass(WelcomActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }


    private void initIndicator(int mItemCount) {
        mIndicator = new ImageView[mItemCount];
        for (int i = 0; i < mIndicator.length; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    12, 12);
            params.setMargins(6, 0, 6, 0);
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.point_selector);
            mIndicator[i] = imageView;
            if (i == 0) {
                mIndicator[i].setEnabled(true);
            } else {
                mIndicator[i].setEnabled(false);
            }
            mDotsLayout.addView(imageView, params);
        }
        if (mItemCount == 1) {
            mDotsLayout.setVisibility(View.GONE);
        } else {
            mDotsLayout.setVisibility(View.VISIBLE);
        }
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
        if(arg0 == mIndicator.length - 1){
            gotoMainActivity();
        }
    }

    private void setCurrentDot(int selectItems) {
        for (int i = 0; i < mIndicator.length; i++) {
            if (i == selectItems) {
                mIndicator[i].setEnabled(true);
            } else {
                mIndicator[i].setEnabled(false);
            }
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        //界面列表
        private List<View> views;

        public ViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        //销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        //获得当前界面数
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        //初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }

        //判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

    }
}
