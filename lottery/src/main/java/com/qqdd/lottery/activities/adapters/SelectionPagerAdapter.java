package com.qqdd.lottery.activities.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.SelectionHistory;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.management.UserSelectionOperationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 2/26/16.
 */
public class SelectionPagerAdapter extends PagerAdapter {

    private static final int ITEM_PER_PAGE = 5;

    private SelectionHistory mView;

    private List<List<LotteryRecord>> mData;

    public SelectionPagerAdapter(final SelectionHistory view) {
        mView = view;
    }

    public void setData(final UserSelectionOperationResult data) {
        if (data == null || data.getResultType() != UserSelectionOperationResult.ResultType.SUCCESS) {
            mData = null;
            notifyDataSetChanged();
            return;
        }
        final int size = data.size();
        mData = new ArrayList<>();
        for (
                int i = 0; i < size / ITEM_PER_PAGE + ((size % ITEM_PER_PAGE > 0) ?
                1 :
                0); i++
                ) {
            mData.add(new ArrayList<LotteryRecord>(ITEM_PER_PAGE));
        }
        for (int i = 0; i < size; i++) {
            final LotteryRecord item = data.get(i);
            int pageIndex = i / ITEM_PER_PAGE;
            mData.get(pageIndex)
                    .add(item);
        }
        notifyDataSetChanged();
    }

    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mData == null ?
                0 :
                mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        RecyclerView list = new RecyclerView(context);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setAdapter(new PageItemAdapter(mData.get(position)));
        container.addView(list, position,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    private class PageItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<LotteryRecord> mData;


        PageItemAdapter(List<LotteryRecord> records) {
            mData = records;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SelectionHistoryAdapter.SelectionItemViewHolder(mView,
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.selection_history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((SelectionHistoryAdapter.SelectionItemViewHolder) holder).bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ?
                    0 :
                    mData.size();
        }
    }
}
