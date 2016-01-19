package com.qqdd.lottery.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public class LotteryData implements Lottery {

    public enum Type {
        DLT
    }

    private final Set<Integer> mNormal = new HashSet<>();
    private final Set<Integer> mSpecial = new HashSet<>();
    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;
    private final Type mType;

    private LotteryData(Type type, int normalSize, int specialSize, int normalRange, int specialRange) {
        mNormalSize = normalSize;
        mSpecialSize = specialSize;
        mNormalRange = normalRange;
        mSpecialRange = specialRange;
        mType = type;
    }

    public void addNormal(final int number) {
        mNormal.add(number);
    }

    public void addSpecial(final int number) {
        mSpecial.add(number);
    }

    public int getNormalRange() {
        return mNormalRange;
    }

    public int getSpecialRange() {
        return mSpecialRange;
    }

    public boolean isValid() {
        return mNormal.size() == mNormalSize && mSpecial.size() == mSpecialSize;
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

    public static LotteryData newDLT() {
        return new LotteryData(Type.DLT, 5, 2, 35, 12);
    }

    public static LotteryData fromJson(JSONObject lottery) {
        if (lottery == null) {
            return null;
        }
        try {
            final Type type = Type.valueOf(lottery.getString("type"));
            final JSONArray normals = lottery.getJSONArray("normals");
            final JSONArray specials = lottery.getJSONArray("specials");
            LotteryData result;
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
