package com.qqdd.lottery.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public class Lottery implements ILottery {

    public enum Type {
        DLT
    }

    private final NumberList mNormal = new NumberList();
    private final NumberList mSpecial = new NumberList();
    private final Type mType;
    private RewardRule mRewardRule;
    private LotteryConfiguration mLotteryConfiguration;

    private Lottery(Type type, DLTRewardRule rewardRule,  LotteryConfiguration lotteryConfiguration) {
        mType = type;
        mRewardRule = rewardRule;
        mLotteryConfiguration = lotteryConfiguration;
    }

    public RewardRule.RewardDetail getRewardDetail(final LotteryRecord record) {
        return mRewardRule.getRewardDetail(this, record);
    }

    public RewardRule.Reward getReward(final LotteryRecord record) {
        return mRewardRule.getReward(this, record);
    }

    public Type getType() {
        return mType;
    }

    public LotteryConfiguration getLotteryConfiguration() {
        return mLotteryConfiguration;
    }

    public void addNormal(final int number) {
        mNormal.add(number);
    }

    public void addSpecial(final int number) {
        mSpecial.add(number);
    }

    public boolean isValid() {
        return mNormal.size() == mLotteryConfiguration.getNormalSize() && mSpecial.size() == mLotteryConfiguration.getSpecialSize();
    }

    @Override
    public NumberList getSpecials() {
        return mSpecial;
    }

    @Override
    public NumberList getNormals() {
        return mNormal;
    }

    public void addNormals(final Set<Integer> values) {
        if (!mNormal.isEmpty()) {
            return;
        }
        mNormal.addAll(values);
    }

    public void addSpecials(final Set<Integer> values) {
        if (!mSpecial.isEmpty()) {
            return;
        }
        mSpecial.addAll(values);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int v : mNormal) {
            builder.append(v).append(" ");
        }
        builder.append("---- ");
        for (int v : mSpecial) {
            builder.append(v).append(" ");
        }
        return builder.toString();
    }

    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        final JSONArray normals = new JSONArray();
        for (int v :
                mNormal) {
            normals.put(v);
        }
        final JSONArray specials = new JSONArray();
        for (int v :
                mSpecial) {
            specials.put(v);
        }
        try {
            result.put("type", mType.toString());
            result.put("normals", normals);
            result.put("specials", specials);
        } catch (JSONException e) {
        }
        return result;
    }

    public static Lottery newDLT() {
        return new Lottery(Type.DLT, DLTRewardRule.getInstance(), LotteryConfiguration.DLTConfiguration());
    }


    public static Lottery newLotteryWithConfiguration(LotteryConfiguration configuration) {
        if (configuration == LotteryConfiguration.DLTConfiguration()) {
            return new Lottery(Type.DLT, DLTRewardRule.getInstance(), configuration);
        }
        return null;
    }



    public static Lottery fromJson(JSONObject lottery) {
        if (lottery == null) {
            return null;
        }
        try {
            final Type type = Type.valueOf(lottery.getString("type"));
            final JSONArray normals = lottery.getJSONArray("normals");
            final JSONArray specials = lottery.getJSONArray("specials");
            Lottery result;
            if (type == Type.DLT) {
                result = newDLT();
                for (int i = 0; i < normals.length(); i++) {
                    result.addNormal(normals.getInt(i));
                }
                for (int i = 0; i < specials.length(); i++) {
                    result.addSpecial(specials.getInt(i));
                }
                return result;
            } else {
                return null;
            }
        } catch (JSONException e) {
        }
        return null;
    }
}