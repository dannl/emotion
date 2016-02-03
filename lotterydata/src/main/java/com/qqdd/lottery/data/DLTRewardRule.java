package com.qqdd.lottery.data;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by danliu on 1/20/16.
 */
public class DLTRewardRule extends RewardRule {

    @Override
    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        final JSONObject specialReward = new JSONObject();
        try {
            specialReward.put("goHome", mRewards.get(5<<2|2).toJson());
            specialReward.put("buyHouse", mRewards.get(5<<2|1).toJson());
            result.put("type", Lottery.Type.DLT.toString());
            result.put("data", specialReward);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static DLTRewardRule fromJson(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        DLTRewardRule result = new DLTRewardRule();
        final JSONObject goHome;
        try {
            final JSONObject specialReward = jsonObject.getJSONObject("data");
            goHome = specialReward.getJSONObject("goHome");
            final JSONObject buyHouse = specialReward.getJSONObject("buyHouse");
            final Reward goHomeR = Reward.fromJson(goHome);
            final Reward buyHouseR = Reward.fromJson(buyHouse);
            result.putReward(5<<2|2, goHomeR);
            result.putReward(5<<2|1, buyHouseR);
        } catch (JSONException ignored) {
        }
        return result;
    }

    @Override
    public Reward calculateReward(ILottery num, LotteryRecord record) {
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
    public RewardDetail calculateRewardDetail(ILottery num, LotteryRecord record) {
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

    public DLTRewardRule() {
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
        omg.setBuyHouse(true);
        mRewards.put(5 << 2 | 1, omg);
        final Reward iAmRichNow = new Reward("一等", "1+2或者0+2或者", 10000000);
        iAmRichNow.setGoHome(true);
        mRewards.put(5 << 2 | 2, iAmRichNow);
    }

    public void putReward(final int key, final Reward reward) {
        mRewards.put(key, reward);
    }
}
