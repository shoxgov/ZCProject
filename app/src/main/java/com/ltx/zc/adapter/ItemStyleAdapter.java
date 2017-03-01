package com.ltx.zc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ltx.zc.base.ActivitiesListItemStyleViewBase;

import java.util.List;

/**
 * 商品类别适配器
 * 
 * @author Cool
 * 
 */
public class ItemStyleAdapter extends BaseAdapter {

	private Context context;
	/**
	 * 栏目的展现样式 0：默认样式；
	 */
	private int itemViewType;
	private List<ActivitiesListItemStyleViewBase> data;

	public ItemStyleAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<ActivitiesListItemStyleViewBase> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ActivitiesListItemStyleViewBase base = data.get(position);
		return base.getView(position, context, convertView);
	}

	public int getItemViewType() {
		return itemViewType;
	}

	public void setItemViewType(int itemViewType) {
		this.itemViewType = itemViewType;
	}
}
