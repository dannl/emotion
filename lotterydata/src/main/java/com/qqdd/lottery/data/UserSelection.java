package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by danliu on 2016/1/28.
 */
public class UserSelection extends LotteryRecord {

    private long mParentTime;
    private HistoryItem mParent;

    public UserSelection(Lottery t) {
        super(t);
        setDate(new Date(System.currentTimeMillis()));
        mParentTime = -1;
    }

    private UserSelection() {
    }

    public static UserSelection fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        final UserSelection result = new UserSelection();
        try {
            loadBaseData(result, json);
            result.mParentTime = json.optLong("parent", -1);
        } catch (JSONException ignored) {
            return null;
        }
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject object = super.toJson();
        try {
            object.put("parent", mParentTime);
        } catch (JSONException e) {
        }
        return object;
    }

    public boolean isRedeemed() {
        return mParentTime > 0;
    }

    public long getParentTime() {
        return mParentTime;
    }

    public void setParent(HistoryItem historyItem) {
        mParent = historyItem;
        if (historyItem != null) {
            mParentTime = mParent.getDate().getTime();
        }
    }

    public RewardRule.RewardDetail getRewardDetail() {
        if (mParent != null) {
            return mParent.calculateRewardDetail(this);
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "  parent: " + mParentTime;
    }
}
