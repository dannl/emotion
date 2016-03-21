package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danliu on 2016/1/28.
 */
public class HistoryItem extends LotteryRecord {

    private RewardRule mRewardRule;

    public HistoryItem(Lottery t, final RewardRule rewardRule) {
        super(t);
        mRewardRule = rewardRule;
    }

    private HistoryItem() {
        super(null);
    }

    @Override
    public JSONObject toJson() {
        final JSONObject result = super.toJson();
        if (result == null) {
            return null;
        }
        try {
            result.put("rewards", mRewardRule.toJson());
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static HistoryItem fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        final HistoryItem result = new HistoryItem();
        try {
            loadBaseData(result, json);
            result.mRewardRule = RewardRule.fromJson(json.getJSONObject("rewards"));
        } catch (JSONException ignored) {
            return null;
        }
        return result;
    }

    public RewardRule.RewardDetail calculateRewardDetail(ILottery recordOther) {
        return mRewardRule.calculateRewardDetail(recordOther, this);
    }

    public RewardRule.Reward calculateReward(ILottery tempResult) {
        return mRewardRule.calculateReward(tempResult, this);
    }

}
