package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by danliu on 3/15/16.
 */
public class RateList extends ArrayList<Rate> {

    private float mAverageRate;
    private float mMaxRate;
    private float mMinRate;
    private String mName;

    public RateList(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("no name specified.");
        }
        mName = name;
        mMaxRate = 0;
        mMinRate = 1;
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
            final JSONArray array = json.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                Rate rate = Rate.fromJson(array.getJSONObject(i));
                rates.superAdd(rate);
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

}
