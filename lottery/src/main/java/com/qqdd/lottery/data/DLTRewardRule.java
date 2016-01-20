package com.qqdd.lottery.data;

import android.util.SparseArray;

import java.util.HashSet;
import java.util.Set;

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
    public RewardDetail getReward(LotteryData lt, LotteryRecord record) {
        if (lt.getType() != record.getLottery().getType()) {
            return new RewardDetail(Reward.NO_REWARD, new HashSet<Integer>(0), new HashSet<Integer>(0));
        }
        final Set<Integer> srcNormal = lt.getNormals();
        final Set<Integer> srcSpecial = lt.getSpecials();
        final Set<Integer> destNormal = record.getNormals();
        final Set<Integer> destSpecial = record.getSpecials();

        final Set<Integer> resultNormal = new HashSet<>();
        final Set<Integer> resultSpecial = new HashSet<>();

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

    private SparseArray<Reward> mRewards;

    private DLTRewardRule() {
        mRewards = new SparseArray<>();
        mRewards.put(2, new Reward("末等","1+2或者0+2或者",5));
        mRewards.put(1 << 2 | 2, new Reward("末等","1+2或者0+2或者",5));
        mRewards.put(3 << 2, new Reward("末等","1+2或者0+2或者",5));
        mRewards.put(2 << 2 | 1, new Reward("末等","1+2或者0+2或者",5));
        mRewards.put(2 << 2 | 2, new Reward("末等","1+2或者0+2或者",10));
        mRewards.put(3 << 2 | 1, new Reward("末等","1+2或者0+2或者",10));
        mRewards.put(4 << 2, new Reward("末等","1+2或者0+2或者",10));
        mRewards.put(3 << 2 | 2, new Reward("末等","1+2或者0+2或者",200));
        mRewards.put(4 << 2 | 1, new Reward("末等","1+2或者0+2或者",200));
        mRewards.put(4 << 2 | 2, new Reward("末等","1+2或者0+2或者",3000));
        mRewards.put(5 << 2, new Reward("末等","1+2或者0+2或者",3000));
        mRewards.put(5 << 2 | 1, new Reward("末等","1+2或者0+2或者",200000));
        mRewards.put(5 << 2 | 2, new Reward("末等","1+2或者0+2或者",10000000));
    }
}
