package com.qqdd.lottery.data;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by danliu on 1/20/16.
 */
public class SSQRewardRule extends RewardRule {

    @Override
    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        final JSONObject specialReward = new JSONObject();
        try {
            specialReward.put("goHome", mRewards.get(6<<2|1).toJson());
            specialReward.put("buyHouse", mRewards.get(6 << 2).toJson());
            result.put("type", Lottery.Type.SSQ.toString());
            result.put("data", specialReward);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static SSQRewardRule fromJson(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        SSQRewardRule result = new SSQRewardRule();
        final JSONObject goHome;
        try {
            final JSONObject specialReward = jsonObject.getJSONObject("data");
            goHome = specialReward.getJSONObject("goHome");
            final JSONObject buyHouse = specialReward.getJSONObject("buyHouse");
            final Reward goHomeR = Reward.fromJson(goHome);
            final Reward buyHouseR = Reward.fromJson(buyHouse);
            result.putReward(6<<2|1, goHomeR);
            result.putReward(6 << 2, buyHouseR);
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

    public SSQRewardRule() {
        mRewards = new HashMap<>();
        final Reward notBad = new Reward("末等", "2+1或者1+1或者0+1或者", 5);
        mRewards.put(1 << 2 | 1, notBad);
        mRewards.put(2 << 2 | 1, notBad);
        mRewards.put(1, notBad);
        final Reward better = new Reward("五等", "4+0或者3+1", 10);
        mRewards.put(4 << 2, better);
        mRewards.put(3 << 2 | 1, better);
        final Reward neverHadIt = new Reward("四等", "5+0或者4+1", 200);
        mRewards.put(5 << 2, neverHadIt);
        mRewards.put(4 << 2 | 1, neverHadIt);
        final Reward great = new Reward("三等", "5+1", 3000);
        mRewards.put(5 << 2 | 1, great);
        final Reward omg = new Reward("二等", "6+0", 200000);
        omg.setBuyHouse(true);
        mRewards.put(6 << 2, omg);
        final Reward iAmRichNow = new Reward("一等", "6+1", 10000000);
        iAmRichNow.setGoHome(true);
        mRewards.put(6 << 2 | 1, iAmRichNow);
    }

    public void putReward(final int key, final Reward reward) {
        mRewards.put(key, reward);
    }
}
