package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by danliu on 3/15/16.
 */
public class RateList extends ArrayList<Rate> {

    private float mAverageRate;
    private float mMaxRate;
    private float mMinRate;
    private String mName;
    private int mTestCount;
    private HashMap<RewardRule.Reward, Integer> mDetail = new HashMap<>();

    public RateList(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("no name specified.");
        }
        mName = name;
        mMaxRate = 0;
        mMinRate = 1;
    }

    public void setTestCount(int testCount) {
        mTestCount = testCount;
    }

    public int getTestCount() {
        return mTestCount;
    }

    public void plusReward(RewardRule.Reward reward) {
        Integer value = mDetail.get(reward);
        if (value == null) {
            mDetail.put(reward, 1);
        } else {
            value ++;
            mDetail.put(reward, value);
        }
    }

    public String getName() {
        return mName;
    }

    public float getAverageRate() {
        return mAverageRate;
    }

    public float getMaxRate() {
        return mMaxRate;
    }

    public float getMinRate() {
        return mMinRate;
    }

    public void saveTo(File root) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("root not exists or root is not a dir.");
        }
        try {
            SimpleIOUtils.saveToFile(new File(root, mName), toJson().toString());
        } catch (IOException e) {
        }
    }

    public JSONObject toJson() {
        final int size = size();
        final JSONArray array = new JSONArray();
        for (int i = 0; i < size; i++) {
            final JSONObject json = get(i).toJson();
            if (json != null) {
                array.put(json);
            }
        }
        final JSONObject result = new JSONObject();
        try {
            result.put("data", array);
            result.put("name", mName);
            result.put("av", String.valueOf(mAverageRate));
            result.put("max", String.valueOf(mMaxRate));
            result.put("min", String.valueOf(mMinRate));
            result.put("testCount", mTestCount);
            final Set<Map.Entry<RewardRule.Reward, Integer>> entries = mDetail.entrySet();
            JSONArray detail  = new JSONArray();
            for (Map.Entry<RewardRule.Reward, Integer> entry
                    : entries) {
                final JSONObject item = new JSONObject();
                item.put("reward", entry.getKey().toJson());
                item.put("value", entry.getValue());
                detail.put(item);
            }
            result.put("detail", detail);
        } catch (JSONException e) {
        }
        return result;
    }

    public static RateList fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            final RateList rates = new RateList(json.getString("name"));
            rates.mAverageRate = Float.parseFloat(json.getString("av"));
            rates.mMaxRate = Float.parseFloat(json.getString("max"));
            rates.mMinRate = Float.parseFloat(json.getString("min"));
            rates.mTestCount = json.getInt("testCount");
            final JSONArray array = json.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                Rate rate = Rate.fromJson(array.getJSONObject(i));
                rates.superAdd(rate);
            }
            final JSONArray detail = json.getJSONArray("detail");
            for (int i = 0; i < detail.length(); i++) {
                final JSONObject item = detail.getJSONObject(i);
                rates.mDetail.put(RewardRule.Reward.fromJson(item.getJSONObject("reward")), item.getInt("value"));
            }
            return rates;
        } catch (Exception e) {
            return null;
        }
    }

    public static RateList loadFrom(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            final String content = SimpleIOUtils.loadContent(new FileInputStream(file));
            final JSONObject json = new JSONObject(content);
            return fromJson(json);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    private void superAdd(Rate rate) {
        super.add(rate);
    }

    @Override
    public boolean add(Rate rate) {
        boolean result = super.add(rate);
        if (result) {
            final int size = size();
            mAverageRate = (mAverageRate * (size - 1) + rate.getRate()) / size;
            if (rate.getRate() > mMaxRate) {
                mMaxRate = rate.getRate();
            }
            if (rate.getRate() < mMinRate) {
                mMinRate = rate.getRate();
            }
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        mAverageRate = 0;
        mMaxRate = -1;
        mMinRate = 1;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rate remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Rate element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Rate> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Rate> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public void reset(int newTestCount) {
        mAverageRate = 0;
        mMaxRate = 0;
        mMinRate = 1;
        mTestCount = newTestCount;
        mDetail.clear();
        clear();
    }

    public HashMap<RewardRule.Reward, Integer> getDetail() {
        return new HashMap<>(mDetail);
    }
}
