package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danliu on 2016/1/28.
 */
public class UserSelection extends LotteryRecord {

    private boolean mRedeemed;

    public UserSelection(Lottery t) {
        super(t);
    }

    private UserSelection() {
    }

    public UserSelection fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        final UserSelection result = new UserSelection();
        try {
            loadBaseData(result, json);
        } catch (JSONException ignored) {
            return null;
        }
        //FIXME more data of history items.
        return result;
    }

    public boolean isRedeemed() {
        return mRedeemed;
    }

    public RewardRule.RewardDetail getReward() {
        return null;
    }
}
