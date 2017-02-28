package com.ltx.zc.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.ltx.zc.R;
import com.ltx.zc.ZCApplication;
import com.ltx.zc.activity.MainFragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends RelativeLayout {
	private ViewPager mViewPager;
	private LinearLayout mLinearLayout;
	private Context mContext;
	private ImageView[] mIndicator;
	private Handler mHandler = new Handler();
	final List<String> mList = new ArrayList<String>();
	private OnBannerItemClickListener mOnBannerItemClickListener;
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			int count = mViewPager.getCurrentItem() + 1;
			if(count == mItemCount){
				count = 0;
			}
			mViewPager.setCurrentItem(count);
			mHandler.postDelayed(mRunnable, 5000);
		}
	};
	private int mItemCount;
	private ImageLoader imageLoader;

	public interface OnBannerItemClickListener {
		void onClick(int position);
	}

	public BannerView(Context context) {
		this(context, null);
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		imageLoader = ZCApplication.getInstance().getImageLoader();
		init();
	}

	private void init() {
		View.inflate(mContext, R.layout.banner, this);
		// 取到布局中的控件
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mLinearLayout = (LinearLayout) findViewById(R.id.points);
		
		 //让banner的高度是屏幕的1/4
        ViewGroup.LayoutParams vParams = mViewPager.getLayoutParams();
        vParams.height = MainFragmentActivity.screenHeightPixels * 1 / 4;
        mViewPager.setLayoutParams(vParams);
	}

	/**
	 * 给banner中的viewpager设置数据
	 * 
	 * @param list
	 */
	public void setList(List<String> list) {
		if (mList.size() == 0) {
			mList.addAll(list);
			mItemCount = mList.size();
			initView();
		}
	}

	/**
	 * banner item的点击监听
	 * 
	 * @param onBannerItemClickListener
	 */
	public void setOnBannerItemClickListener(
			OnBannerItemClickListener onBannerItemClickListener) {
		mOnBannerItemClickListener = onBannerItemClickListener;
	}

	private void initView() {
		// 给viewpager设置adapter
		BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(mList,
				mContext);
		mViewPager.setAdapter(bannerPagerAdapter);
		// 初始化底部点指示器
		initIndicator(mList, mContext);
		mViewPager.setCurrentItem(500 * 0);

		// 给viewpager设置滑动监听
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageSelected(int position) {
						switchIndicator(position % mItemCount);
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			cancelRecycle();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			startRecycle();
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private void initIndicator(List<String> list, Context context) {
		mIndicator = new ImageView[mItemCount];
		for (int i = 0; i < mIndicator.length; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					12, 12);
			params.setMargins(6, 0, 6, 0);
			ImageView imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.point_selector);
			mIndicator[i] = imageView;
			if (i == 0) {
				mIndicator[i].setEnabled(true);
			} else {
				mIndicator[i].setEnabled(false);
			}
			mLinearLayout.addView(imageView, params);
		}
		if (mItemCount == 1) {
			mLinearLayout.setVisibility(View.GONE);
		} else {
			mLinearLayout.setVisibility(View.VISIBLE);
		}
	}

	private void switchIndicator(int selectItems) {
		for (int i = 0; i < mIndicator.length; i++) {
			if (i == selectItems) {
				mIndicator[i].setEnabled(true);
			} else {
				mIndicator[i].setEnabled(false);
			}
		}
	}

	private void startRecycle() {
		mHandler.postDelayed(mRunnable, 5000);
	}

	private void cancelRecycle() {
		mHandler.removeCallbacks(mRunnable);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == VISIBLE) {
			startRecycle();
		} else {
			cancelRecycle();
		}
	}

	private class BannerPagerAdapter extends PagerAdapter {
		private List<String> imagesUrl;
		private Context context;

		public BannerPagerAdapter(List<String> imagesUrl, Context context) {
			this.imagesUrl = imagesUrl;
			this.context = context;
		}

		@Override
		public int getCount() {
			return imagesUrl.size();// mItemCount == 1 ? 1 : Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			// 联网取图片，根据自己的情况修改
			imageLoader.get(imagesUrl.get(position), imageLoader
					.getImageListener(imageView, R.mipmap.banner_loading,
							R.mipmap.banner_loading));
			container.addView((View) imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnBannerItemClickListener != null) {
						mOnBannerItemClickListener.onClick(position);
					}
				}
			});
			return (View) imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}
