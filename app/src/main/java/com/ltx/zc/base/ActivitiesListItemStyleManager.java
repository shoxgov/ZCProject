package com.ltx.zc.base;

import java.util.ArrayList;
import java.util.List;

/**
 * 将收到的数据按要求转换成不同的样式定义类
 */
public class ActivitiesListItemStyleManager {

	public List<ActivitiesListItemStyleViewBase> getListItemView(List<String> list) {
		int i = 0;
		List<ActivitiesListItemStyleViewBase> data = new ArrayList<ActivitiesListItemStyleViewBase>();
		for (String ci : list) {
			ActivitiesListItemStyleViewBase asb;
			if (i % 2 == 0 ) {// 小于10个就用9个显示排列的
				asb = new ActivitiesListItemViewStyle_1();//3*3
			} else {
				asb = new ActivitiesListItemViewStyle_2();//3*2+4
			}
			i++;
			data.add(asb);
		}
		return data;
	}
}
