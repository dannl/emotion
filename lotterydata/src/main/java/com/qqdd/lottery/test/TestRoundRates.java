package com.qqdd.lottery.test;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 2/2/16.
 */
public class TestRoundRates implements Comparable<TestRoundRates> {

    public static final String TAIL = "_rates";

    private HistoryItem mRecord;
    private float mRate;

    public TestRoundRates(HistoryItem record, float rate) {
        mRecord = record;
        mRate = rate;
    }

    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        try {
            result.put("record", mRecord.toJson());
            result.put("rate", String.valueOf(mRate));
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static TestRoundRates fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        final HistoryItem item;
        try {
            item = HistoryItem.fromJson(json.getJSONObject("record"));
            final float rate = Float.parseFloat(json.getString("rate"));
            return new TestRoundRates(item, rate);
        } catch (JSONException e) {
        }
        return null;
    }

    public HistoryItem getRecord() {
        return mRecord;
    }

    public float getRate() {
        return mRate;
    }

    @Override
    public int compareTo(TestRoundRates o) {
        return (int) (o.mRecord.getDate().getTime() - mRecord.getDate().getTime());
    }


    public static String[] loadFiles(final File root) {
        if (root == null || !root.exists() || !root.isDirectory()) {
            return null;
        }
        return root.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(TAIL);
            }
        });
    }

    public static List<TestRoundRates> load(final File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            final String content = SimpleIOUtils.loadContent(new FileInputStream(file), "UTF-8");
            final JSONArray jsonArray = new JSONArray(content);
            List<TestRoundRates> result = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(TestRoundRates.fromJson(jsonArray.getJSONObject(i)));
            }
            return result;
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    public static void save(File root, String name, List<TestRoundRates> array) {
        try {
            final JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                jsonArray.put(array.get(i).toJson());
            }
            SimpleIOUtils.saveToFile(new File(root, name + TAIL), jsonArray.toString(), "UTF-8");
        } catch (IOException ignored) {
        }
    }

}
