package com.ltx.zc.base;

import android.content.Context;
import android.view.View;

public abstract class ActivitiesListItemStyleViewBase {

    /**
     * 示样式的类型
     */
    private int mViewType;

    /**
     * 由子类实现，返回各自的View
     */
    public abstract View getView(int position, Context context, View convertView);

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int mViewType) {
        this.mViewType = mViewType;
    }
}
