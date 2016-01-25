package com.qqdd.lottery.data;


import java.util.HashMap;

/**
 * Created by danliu on 1/20/16.
 */
public class DLTRewardRule extends RewardRule {

    private static final int NORMAL_NUM_MASK = 0xff00;
    private static final int SPECIAL_NUM_MASK = 0x00ff;

    private static class SingletonHolder {
        private static final DLTRewardRule INSTANCE = new DLTRewardRule();
    }

    public static DLTRewardRule getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Reward getReward(Lottery num, LotteryRecord record) {
        if (num.getType() != record.getLottery().getType()) {
            return Reward.NO_REWARD;
        }
        final NumberList srcNormal = num.getNormals();
        final NumberList srcSpecial = num.getSpecials();
        final NumberList destNormal = record.getNormals();
        final NumberList destSpecial = record.getSpecials();

        int normalSame = 0;
        int specialSame = 0;
        for (int v : srcNormal) {
            if (destNormal.contains(v)) {
                normalSame++;
            }
        }
        for (int v : srcSpecial) {
            if (destSpecial.contains(v)) {
                specialSame ++;
            }
        }
        int same = normalSame << 2 | specialSame;
        Reward reward = mRewards.get(same);
        if (reward == null) {
            reward = Reward.NO_REWARD;
        }
        return reward;
    }

    @Override
    public RewardDetail getRewardDetail(Lottery num, LotteryRecord record) {
        if (num.getType() != record.getLottery().getType()) {
            return new RewardDetail(Reward.NO_REWARD, new NumberList(), new NumberList());
        }
        final NumberList srcNormal = num.getNormals();
        final NumberList srcSpecial = num.getSpecials();
        final NumberList destNormal = record.getNormals();
        final NumberList destSpecial = record.getSpecials();

        final NumberList resultNormal = new NumberList();
        final NumberList resultSpecial = new NumberList();
        int normalSame = 0;
        int specialSame = 0;
        for (int v : srcNormal) {
            if (destNormal.contains(v)) {
                resultNormal.add(v);
                normalSame++;
            }
        }
        for (int v : srcSpecial) {
            if (destSpecial.contains(v)) {
                resultSpecial.add(v);
                specialSame ++;
            }
        }
        int same = normalSame << 2 | specialSame;
        Reward reward = mRewards.get(same);
        if (reward == null) {
            reward = Reward.NO_REWARD;
        }
        return new RewardDetail(reward, resultNormal, resultSpecial);
    }

    private HashMap<Integer, Reward> mRewards;

    private DLTRewardRule() {
        mRewards = new HashMap<>();
        final Reward notBad = new Reward("末等", "1+2或者0+2或者", 5);
        mRewards.put(2, notBad);
        mRewards.put(1 << 2 | 2, notBad);
        mRewards.put(3 << 2, notBad);
        mRewards.put(2 << 2 | 1, notBad);
        final Reward better = new Reward("五等", "1+2或者0+2或者", 10);
        mRewards.put(2 << 2 | 2, better);
        mRewards.put(3 << 2 | 1, better);
        mRewards.put(4 << 2, better);
        final Reward neverHadIt = new Reward("四等", "1+2或者0+2或者", 200);
        mRewards.put(3 << 2 | 2, neverHadIt);
        mRewards.put(4 << 2 | 1, neverHadIt);
        final Reward great = new Reward("三等", "1+2或者0+2或者", 3000);
        mRewards.put(4 << 2 | 2, great);
        mRewards.put(5 << 2, great);
        final Reward omg = new Reward("二等", "1+2或者0+2或者", 200000);
        mRewards.put(5 << 2 | 1, omg);
        final Reward iAmRichNow = new Reward("一等", "1+2或者0+2或者", 10000000);
        mRewards.put(5 << 2 | 2, iAmRichNow);
    }
}
