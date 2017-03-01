package com.ltx.zc.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.ltx.zc.R;
import com.ltx.zc.ZCApplication;

public class ActivitiesListItemViewStyle_1 extends
		ActivitiesListItemStyleViewBase {

	private ImageLoader imageLoader;

	public ActivitiesListItemViewStyle_1() {
	}

	@Override
	public View getView(int position, final Context context, View convertView) {
		if (imageLoader == null) {
			imageLoader = ZCApplication.getInstance().getImageLoader();
		}
		convertView = LayoutInflater.from(context).inflate(
				R.layout.list_itemview_style_1, null);
		return convertView;
	}

}
