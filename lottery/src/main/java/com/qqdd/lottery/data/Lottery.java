package com.qqdd.lottery.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public class Lottery implements ILottery {

    public enum Type {
        DLT
    }

    private final Set<Integer> mNormal = new HashSet<>();
    private final Set<Integer> mSpecial = new HashSet<>();
    private final Type mType;
    private RewardRule mRewardRule;
    private Configuration mConfiguration;

    private Lottery(Type type, DLTRewardRule rewardRule,  Configuration configuration) {
        mType = type;
        mRewardRule = rewardRule;
        mConfiguration = configuration;
    }

    public RewardRule.RewardDetail getReward(final LotteryRecord record) {
        return mRewardRule.getReward(this, record);
    }

    public Type getType() {
        return mType;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public void addNormal(final int number) {
        mNormal.add(number);
    }

    public void addSpecial(final int number) {
        mSpecial.add(number);
    }

    public boolean isValid() {
        return mNormal.size() == mConfiguration.getNormalSize() && mSpecial.size() == mConfiguration.getSpecialSize();
    }

    @Override
    public Set<Integer> getSpecials() {
        return mSpecial;
    }

    @Override
    public Set<Integer> getNormals() {
        return mNormal;
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
        return new Lottery(Type.DLT, DLTRewardRule.getInstance() , Configuration.DLTConfiguration());
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
