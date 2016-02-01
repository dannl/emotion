package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danliu on 1/20/16.
 */
public abstract class RewardRule {

    public abstract RewardDetail calculateRewardDetail(ILottery num, LotteryRecord record);

    public abstract Reward calculateReward(ILottery num, LotteryRecord record);

    public abstract JSONObject toJson();

    public static RewardRule fromJson(JSONObject rewards) {
        try {
            final Lottery.Type type = Lottery.Type.valueOf(rewards.getString("type"));
            if (type == Lottery.Type.DLT) {
                return DLTRewardRule.fromJson(rewards);
            }
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static class RewardDetail {
        private Reward mReward;
        private NumberList mNormals;
        private NumberList mSpecials;

        public RewardDetail(Reward reward, NumberList normals, NumberList specials) {
            mReward = reward;
            mNormals = normals;
            mSpecials = specials;
        }

        public NumberList getNormals() {
            return mNormals;
        }

        public NumberList getSpecials() {
            return mSpecials;
        }

        public Reward getReward() {
            return mReward;
        }

    }

    public static class Reward {

        public static final Reward NO_REWARD = new Reward("没中", "没中", 0);

        private String mTitle;
        private String mDesc;
        private int mMoney;
        private boolean mGoHome;
        private boolean mBuyHouse;

        public Reward(final String title, final String desc, final int money) {
            mTitle = title;
            mDesc = desc;
            mMoney = money;
            mGoHome = false;
            mBuyHouse = false;
        }

        public boolean isGoHome() {
            return mGoHome;
        }

        public boolean isBuyHouse() {
            return mBuyHouse;
        }

        public void setGoHome(boolean goHome) {
            mGoHome = goHome;
        }

        public void setBuyHouse(boolean buyHouse) {
            mBuyHouse = buyHouse;
        }

        public int getMoney() {
            return mMoney;
        }

        public String getDesc() {
            return mDesc;
        }

        public String getTitle() {
            return mTitle;
        }

        @Override
        public int hashCode() {
            return mTitle.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            } else {
                if (obj instanceof Reward) {
                    return mTitle.equals(((Reward) obj).mTitle);
                }
                return false;
            }
        }

        public JSONObject toJson() {
            final JSONObject json = new JSONObject();
            try {
                json.put("title", mTitle);
                json.put("desc", mDesc);
                json.put("money", mMoney);
                json.put("goHome", mGoHome);
                json.put("buyHouse", mBuyHouse);
            } catch (JSONException ignored) {
            }
            return json;
        }

        public static Reward fromJson(final JSONObject json) {
            if (json == null) {
                return null;
            }
            try {
                final Reward result = new Reward(json.getString("title"), json.getString("desc"), json.getInt(
                        "money"));
                result.setBuyHouse(json.optBoolean("buyHouse", false));
                result.setGoHome(json.optBoolean("goHome", false));
                return result;
            } catch (JSONException ignored) {
            }
            return null;
        }
    }
}
