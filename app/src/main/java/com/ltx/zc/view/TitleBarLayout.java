package com.ltx.zc.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ltx.zc.R;


public class TitleBarLayout extends RelativeLayout {

	private TitleBarListener listener = null;
	private TextView title;
	private ImageButton leftButton;
	private TextView rightButton;

	public interface TitleBarListener {
		public void leftClick();

		public void rightClick();
	}

	public void setTitleBarListener(TitleBarListener listener) {
		this.listener = listener;
	}
	
	public void setRightBtnShow(String rightText){
		rightButton.setText(rightText);
		rightButton.setVisibility(View.VISIBLE);
	}

	public TitleBarLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TitleBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitleBarLayout(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		title = (TextView) findViewById(R.id.title_name);
		leftButton = (ImageButton) findViewById(R.id.title_left_img);
		rightButton = (TextView) findViewById(R.id.title_right_img);
		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);
		rightButton.setVisibility(View.GONE);
		super.onFinishInflate();
	}

	public void setTitle(String name) {
		if(TextUtils.isEmpty(name)){
			name = "";
		}
		title.setText(name);
	}

    OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.title_left_img:
				if(listener != null){
					listener.leftClick();
				}
				break;

			case R.id.title_right_img:
				if(listener != null){
					listener.rightClick();
				}
				break;

			}
		}
	};
}
