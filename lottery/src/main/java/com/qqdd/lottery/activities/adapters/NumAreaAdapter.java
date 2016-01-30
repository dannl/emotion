package com.qqdd.lottery.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.ui.view.NumberView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 2016/1/20.
 */
public class NumAreaAdapter extends BaseAdapter {

    private NumberView.Display mDisplayToApply;
    private NumberTableWrapper mNumberTable;

    public NumAreaAdapter(int range, final NumberView.Display display) {
        mNumberTable = new NumberTableWrapper(range);
        mDisplayToApply = display;
    }

    public void toggleSelected(final int position) {
        mNumberTable.toggleSelected(position);
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

    public List<Number> getSelections() {
        return mNumberTable.getSelections();
    }

    private static final class NumberTableWrapper extends NumberTable {

        private boolean[] mSelected;

        public NumberTableWrapper(int range) {
            super(range);
            mSelected = new boolean[range];
        }

        public void toggleSelected(final int position) {
            mSelected[position] = !mSelected[position];
        }

        public List<Number> getSelections() {
            List<Number> result = new ArrayList<>();
            for (int i = 0; i < mSelected.length; i++) {
                if (mSelected[i]) {
                    result.add(get(i));
                }
            }
            return result;
        }
    }


}
