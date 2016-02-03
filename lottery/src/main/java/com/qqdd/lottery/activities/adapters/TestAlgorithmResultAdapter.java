package com.qqdd.lottery.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.SelectionHistory;
import com.qqdd.lottery.data.LotteryRecord;

import java.util.List;

public class TestAlgorithmResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LotteryRecord> mData;
    private SelectionHistory mActivity;

    public TestAlgorithmResultAdapter(final SelectionHistory activity) {
        mActivity = activity;
    }

    public void setData(List<LotteryRecord> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectionHistoryAdapter.SelectionItemViewHolder(mActivity, LayoutInflater.from(parent.getContext()).inflate(
                R.layout.selection_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SelectionHistoryAdapter.SelectionItemViewHolder) holder).bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

}
