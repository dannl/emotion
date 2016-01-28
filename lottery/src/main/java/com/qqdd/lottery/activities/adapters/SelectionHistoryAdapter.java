package com.qqdd.lottery.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.ui.view.NumberLineView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by danliu on 2016/1/28.
 */
public class SelectionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SELECTION = 0;
    private static final int VIEW_TYPE_ADD = 1;
    private static final int VIEW_TYPE_HISTORY_RECORD = 2;

    private List<LotteryRecord> mData;

    public SelectionHistoryAdapter() {}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SELECTION) {

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        //加1是为了在第一项加入添加手动的button.
        return mData == null ? 0 : mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ADD;
        } else {
            final LotteryRecord record = mData.get(position);
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

    private static class AddManualSelectionViewHolder extends MyViewHolder {

        public AddManualSelectionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(LotteryRecord record) {
            //do nothing.
        }
    }

    private static class SelectionItemViewHolder extends MyViewHolder {

        private NumberLineView mNumberLineView;
        private TextView mDeleteView;
        private TextView mRewardMsgView;

        public SelectionItemViewHolder(View itemView) {
            super(itemView);
            mNumberLineView = (NumberLineView) itemView.findViewById(R.id.number_line);
            mDeleteView = (TextView) itemView.findViewById(R.id.delete);
            mRewardMsgView = (TextView) itemView.findViewById(R.id.reward_msg);
        }

        @Override
        protected void bind(LotteryRecord record) {
            final UserSelection userSelection = (UserSelection) record;
            mNumberLineView.setLottery(userSelection.getLottery());
            mDeleteView.setVisibility(userSelection.isRedeemed() ? View.GONE : View.VISIBLE);
            final RewardRule.RewardDetail rewardDetail = userSelection.getReward();
            if (rewardDetail == null) {
                mRewardMsgView.setVisibility(View.GONE);
            } else {
                mRewardMsgView.setVisibility(View.VISIBLE);
                //FIXME display it!!
                mRewardMsgView.setText(rewardDetail.getReward().getMoney());
            }
        }
    }

    private static class HistoryRecordViewHolder extends MyViewHolder {

        public HistoryRecordViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(LotteryRecord record) {
            //开奖日期
            //开奖号码
            //一等奖金额
            //二等奖金额
            //....
            //
        }
    }
}
