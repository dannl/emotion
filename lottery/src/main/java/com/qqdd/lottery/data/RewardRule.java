package com.qqdd.lottery.data;

import com.qqdd.lottery.utils.data.NumberList;

/**
 * Created by danliu on 1/20/16.
 */
public abstract class RewardRule {

    public abstract RewardDetail getRewardDetail(Lottery num, LotteryRecord record);

    public abstract Reward getReward(Lottery num, LotteryRecord record);

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

        public Reward(final String title, final String desc, final int money) {
            mTitle = title;
            mDesc = desc;
            mMoney = money;
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
            return (mTitle + mDesc + mMoney).hashCode();
        }
    }
}
