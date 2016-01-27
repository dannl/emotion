package com.qqdd.lottery.calculate.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danliu on 1/27/16.
 */
public class TimeToGoHome extends ArrayList<Integer> {

    private int mTestCount;

    public TimeToGoHome(final int testTime) {
        super();
        mTestCount = testTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            builder.append("第").append(get(i)).append("次计算\n");
        }
        return builder.toString();
    }

    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("test_count", mTestCount);
            JSONArray indexes = new JSONArray();
            for (int i = 0; i < size(); i++) {
                indexes.put(get(i));
            }
            jsonObject.put("indexes", indexes);
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    public static TimeToGoHome fromJson(String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            final int testCount = jsonObject.getInt("test_count");
            final TimeToGoHome result = new TimeToGoHome(testCount);
            final JSONArray indexes = jsonObject.getJSONArray("indexes");
            for (int i = 0; i < indexes.length(); i++) {
                result.add(indexes.getInt(i));
            }
            return result;
        } catch (JSONException e) {
        }
        return null;
    }

    public int getTestCount() {
        return mTestCount;
    }
}
