package com.qqdd.lottery.data;

import java.util.Set;

/**
 * Created by danliu on 1/20/16.
 */
public abstract class RewardRule {

    public abstract RewardDetail getReward(Lottery num, LotteryRecord record);

    public static class RewardDetail {
        private Reward mReward;
        private Set<Integer> mNormals;
        private Set<Integer> mSpecials;

        public RewardDetail(Reward reward, Set<Integer> normals, Set<Integer> specials) {
            mReward = reward;
            mNormals = normals;
            mSpecials = specials;
        }

        public Set<Integer> getNormals() {
            return mNormals;
        }

        public Set<Integer> getSpecials() {
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
    }
}
