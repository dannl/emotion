package com.qqdd.lottery.activities.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.SelectionHistory;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.UserSelectionOperationResult;
import com.qqdd.lottery.ui.view.NumberLineView;

/**
 * Created by danliu on 2016/1/28.
 */
public class SelectionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SELECTION = 0;
    private static final int VIEW_TYPE_ADD = 1;
    private static final int VIEW_TYPE_HISTORY_RECORD = 2;
    private static final int VIEW_TYPE_MORE = 4;

    private UserSelectionOperationResult mData;
    private boolean mIsLoading = false;
    private SelectionHistory mActivity;

    public SelectionHistoryAdapter(SelectionHistory activity) {
        mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SELECTION) {
            return new SelectionItemViewHolder(mActivity, inflater.inflate(R.layout.selection_history_item, parent, false));
        } else if (viewType == VIEW_TYPE_ADD) {
            return new AddManualSelectionViewHolder(mActivity, inflater.inflate(R.layout.add_record_item, parent, false));
        } else if (viewType == VIEW_TYPE_HISTORY_RECORD) {
            return new HistoryRecordViewHolder(inflater.inflate(R.layout.history_record_item, parent, false));
        } else {
            return new LoadMoreViewHolder(mActivity, inflater.inflate(R.layout.load_more_item, parent, false));
        }
    }

    public void setData(final UserSelectionOperationResult result) {
        mData = result;
        notifyDataSetChanged();
    }

    public void setIsLoading(final boolean loading) {
        mIsLoading = loading;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            //do nothing.
        } else if (position == mData.size() + 1) {
            final LoadMoreViewHolder loadMoreHolder = (LoadMoreViewHolder) holder;
            loadMoreHolder.setIsLoading(mIsLoading);
            loadMoreHolder.setHasMore(mData.hasMore());
            loadMoreHolder.setData(mData);
        } else {
            ((MyViewHolder) holder).bind(mData.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        //加1是为了在第一项加入添加手动的button.
        if (mData == null) {
            return 0;
        } else {
            //添加的button, 以及LoadMore的button
            if (mData.size() == 0) {
                return 1;
            } else {
                return mData.size() + 2;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ADD;
        } else if (position == mData.size() + 1) {
            return VIEW_TYPE_MORE;
        } else {
            final LotteryRecord record = mData.get(position - 1);
            if (record instanceof HistoryItem) {
                return VIEW_TYPE_HISTORY_RECORD;
            }
            return VIEW_TYPE_SELECTION;
        }
    }

    private static abstract class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void bind(LotteryRecord record);
    }

    private static class AddManualSelectionViewHolder extends RecyclerView.ViewHolder {

        private SelectionHistory mActivity;

        public AddManualSelectionViewHolder(SelectionHistory activity, View itemView) {
            super(itemView);
            mActivity = activity;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.addUserSelection();
                }
            });
        }

    }

    private static class SelectionItemViewHolder extends MyViewHolder {

        private NumberLineView mNumberLineView;
        private TextView mDeleteView;
        private TextView mRewardMsgView;
        private SelectionHistory mActivity;

        public SelectionItemViewHolder(SelectionHistory activity, View itemView) {
            super(itemView);
            this.mActivity = activity;
            mNumberLineView = (NumberLineView) itemView.findViewById(R.id.number_line);
            mDeleteView = (TextView) itemView.findViewById(R.id.delete);
            mRewardMsgView = (TextView) itemView.findViewById(R.id.reward_msg);
        }

        @Override
        protected void bind(final LotteryRecord record) {
            final UserSelection userSelection = (UserSelection) record;
            mNumberLineView.setLottery(userSelection);
            mDeleteView.setVisibility(userSelection.isRedeemed() ? View.GONE : View.VISIBLE);
            mDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.deleteUserSelection(userSelection);
                }
            });
            final RewardRule.RewardDetail rewardDetail = userSelection.getRewardDetail();
            if (rewardDetail == null || rewardDetail.getReward().getMoney() <= 0) {
                mRewardMsgView.setVisibility(View.GONE);
            } else {
                mRewardMsgView.setVisibility(View.VISIBLE);
                final Resources resources = mRewardMsgView.getResources();
                mRewardMsgView.setText(resources.getString(R.string.reward_hint_format, rewardDetail.getReward().getMoney()));
            }
            if (rewardDetail == null) {
                mNumberLineView.setAlpha(1.0f);
            } else {
                mNumberLineView.setAlpha(0.5f);
            }
        }

    }


    private static class HistoryRecordViewHolder extends MyViewHolder {

        private TextView contentView;

        public HistoryRecordViewHolder(View itemView) {
            super(itemView);
            contentView = (TextView) itemView.findViewById(R.id.history_record_content);
        }

        @Override
        protected void bind(LotteryRecord record) {
            final Resources res = contentView.getResources();
            contentView.setText(res.getString(R.string.history_item_text_format, record.getDateDisplay(), record.getLottery().toString()));
        }
    }

    private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        private View progress;
        private TextView textView;
        private SelectionHistory mActivity;

        public LoadMoreViewHolder(SelectionHistory activity, View itemView) {
            super(itemView);
            mActivity = activity;
            progress = itemView.findViewById(R.id.progress);
            textView = (TextView) itemView.findViewById(R.id.load_more_text);

        }

        public void setHasMore(boolean hasMore) {
            final Resources res = textView.getContext().getResources();
            textView.setText(hasMore ? res.getString(R.string.load_more) : res.getString(R.string.no_more));
        }


        public void setIsLoading(boolean isLoading) {
            if (isLoading) {
                progress.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            } else {
                progress.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }

        }

        public void setData(final UserSelectionOperationResult data) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data != null && data.hasMore()) {
                        final LotteryRecord last = data.get(data.size() - 1);
                        mActivity.loadMore(last);
                    }
                }
            });
        }
    }
}
