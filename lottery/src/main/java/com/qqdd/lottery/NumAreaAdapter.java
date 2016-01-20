package com.qqdd.lottery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.ui.view.NumberView;

/**
 * Created by danliu on 2016/1/20.
 */
public class NumAreaAdapter extends RecyclerView.Adapter<NumAreaAdapter.NumItemViewHolder> {

    private NumberTable mNumberTable;
    private NumberView.Display mDisplayToApply;

    public NumAreaAdapter(NumberTable numbers, final NumberView.Display display) {
        mNumberTable = numbers;
        mDisplayToApply = display;
    }

    @Override
    public NumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NumItemViewHolder(new NumberView(parent.getContext(), mDisplayToApply));
    }

    @Override
    public void onBindViewHolder(NumItemViewHolder holder, int position) {
        final Number number = mNumberTable.get(position);
        holder.numberView.setNumber(number);
    }

    @Override
    public int getItemCount() {
        return mNumberTable == null ? 0 : mNumberTable.size();
    }

    public static class NumItemViewHolder extends RecyclerView.ViewHolder {

        private NumberView numberView;

        public NumItemViewHolder(View itemView) {
            super(itemView);
            numberView = (NumberView) itemView;
        }
    }

}
