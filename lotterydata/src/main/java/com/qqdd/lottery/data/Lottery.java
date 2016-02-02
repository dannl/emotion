package com.qqdd.lottery.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public class Lottery implements ILottery {

    public enum Type {
        SSQ, NONE, DLT
    }

    private final NumberList mNormal = new NumberList();
    private final NumberList mSpecial = new NumberList();
    private final Type mType;
    private LotteryConfiguration mLotteryConfiguration;

    private Lottery(Type type, LotteryConfiguration lotteryConfiguration) {
        mType = type;
        mLotteryConfiguration = lotteryConfiguration;
    }

    public Type getType() {
        return mType;
    }

    public LotteryConfiguration getConfiguration() {
        return mLotteryConfiguration;
    }

    public void addNormal(final int number) {
        mNormal.add(number);
    }

    public void addSpecial(final int number) {
        mSpecial.add(number);
    }

    @Override
    public void sort() {
        Collections.sort(mNormal);
        Collections.sort(mSpecial);
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

    public void replaceAllNormals(Collection<Integer> normals) {
        mNormal.clear();
        mNormal.addAll(normals);
    }

    public void replaceAllSpecials(Collection<Integer> specials) {
        mSpecial.clear();
        mSpecial.addAll(specials);
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

    public static Lottery newLotteryWithConfiguration(LotteryConfiguration configuration) {
        if (configuration == LotteryConfiguration.DLTConfiguration()) {
            return new Lottery(Type.DLT, configuration);
        } else if (configuration == LotteryConfiguration.SSQConfiguration()) {
            return new Lottery(Type.SSQ, configuration);
        }
        return new Lottery(Type.NONE, configuration);
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
                result = newLotteryWithConfiguration(LotteryConfiguration.DLTConfiguration());
                for (int i = 0; i < normals.length(); i++) {
                    result.addNormal(normals.getInt(i));
                }
                for (int i = 0; i < specials.length(); i++) {
                    result.addSpecial(specials.getInt(i));
                }
                return result;
            } else if (type == Type.SSQ) {
                result = newLotteryWithConfiguration(LotteryConfiguration.SSQConfiguration());
                for (int i = 0; i < normals.length(); i++) {
                    result.addNormal(normals.getInt(i));
                }
                for (int i = 0; i < specials.length(); i++) {
                    result.addSpecial(specials.getInt(i));
                }
                return result;
            }
            else {
                return null;
            }
        } catch (JSONException e) {
        }
        return null;
    }
}
