package com.ltx.zc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ltx.zc.R;

import java.util.List;

/**
 * 商品类别适配器
 *
 * @author Cool
 */
public class PersonAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;

    public PersonAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> data) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_person, null, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.adapter_person_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(data.get(position));
        return convertView;
    }

    public class ViewHolder{

        public TextView title;
    }

}
