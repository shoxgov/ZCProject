package com.ltx.zc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ltx.zc.R;
import com.ltx.zc.utils.ToastTool;


/**
 * @author hnbx
 * 
 */
public class MainFragmentActivity extends FragmentActivity {
	private Fragment[] mFragments;
	private RadioGroup bottomRg;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private RadioButton rb_home, rb_sort, rb_purchase, rb_my;
	/**
	 * 连续2次退出键才算退出
	 */
	private long quitTime = 0;
	public static int screenWidthPixels;
	public static int screenHeightPixels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// /////
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidthPixels = dm.widthPixels;
        screenHeightPixels = dm.heightPixels;
        // ///
//		CheckAndUpdateApk checkUpdate = CheckAndUpdateApk.getInstance();
//		checkUpdate.init(this);
//		checkUpdate.checkUpdate();
		mFragments = new Fragment[4];
		fragmentManager = getSupportFragmentManager();
		mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_home);
		mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_sort);
		mFragments[2] = fragmentManager
				.findFragmentById(R.id.fragement_purchase);
		mFragments[3] = fragmentManager.findFragmentById(R.id.fragement_my);
		fragmentTransaction = fragmentManager.beginTransaction()
				.hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2])
				.hide(mFragments[3]);
		fragmentTransaction.show(mFragments[0]).commit();
		setFragmentIndicator();
		initBottomRadio();
		rb_home.setChecked(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - quitTime > 1000) {
				quitTime = System.currentTimeMillis();
				ToastTool.showShortBigToast(this, "连续二次确认退出");
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		CheckAndUpdateApk.getInstance().stopDownload();
	}

	private void setFragmentIndicator() {

		bottomRg = (RadioGroup) findViewById(R.id.bottomRg);
		rb_home = (RadioButton) findViewById(R.id.rb_home);
		rb_sort = (RadioButton) findViewById(R.id.rb_sort);
		rb_purchase = (RadioButton) findViewById(R.id.rb_purchase);
		rb_my = (RadioButton) findViewById(R.id.rb_my);

		bottomRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				fragmentTransaction = fragmentManager.beginTransaction()
						.hide(mFragments[0]).hide(mFragments[1])
						.hide(mFragments[2]).hide(mFragments[3]);
				initBottomRadio();
				switch (checkedId) {
				case R.id.rb_home:
					rb_home.setChecked(true);
					fragmentTransaction.show(mFragments[0]).commit();
					break;

				case R.id.rb_sort:
					rb_sort.setChecked(true);
					fragmentTransaction.show(mFragments[1]).commit();
					break;

				case R.id.rb_purchase:
					rb_purchase.setChecked(true);
					fragmentTransaction.show(mFragments[2]).commit();
					break;

				case R.id.rb_my:
					rb_my.setChecked(true);
					fragmentTransaction.show(mFragments[3]).commit();
					break;

				default:
					break;
				}
			}
		});
	}

	private void initBottomRadio() {
		rb_home.setChecked(false);
		rb_sort.setChecked(false);
		rb_purchase.setChecked(false);
		rb_my.setChecked(false);
	}
}
