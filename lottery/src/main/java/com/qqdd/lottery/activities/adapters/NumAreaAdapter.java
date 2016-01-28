package com.qqdd.lottery.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qqdd.lottery.data.*;
import com.qqdd.lottery.ui.view.NumberView;

/**
 * Created by danliu on 2016/1/20.
 */
public class NumAreaAdapter extends BaseAdapter {

    private NumberTable mNumberTable;
    private NumberView.Display mDisplayToApply;

    public NumAreaAdapter(NumberTable numbers, final NumberView.Display display) {
        mNumberTable = numbers;
        mDisplayToApply = display;
    }

    @Override
    public int getCount() {
        return mNumberTable == null ? 0 : mNumberTable.size();
    }

    @Override
    public com.qqdd.lottery.data.Number getItem(int position) {
        return mNumberTable == null ? null : mNumberTable.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new NumberView(parent.getContext(), mDisplayToApply);
        }
        ((NumberView) convertView).setNumber(getItem(position).getValue());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
