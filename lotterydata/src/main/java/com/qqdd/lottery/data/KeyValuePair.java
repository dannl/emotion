package com.qqdd.lottery.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 3/10/16.
 */
public class KeyValuePair {

    public static final String TAIL = "_kv";
    private String mKey;
    private float mValue;

    public KeyValuePair(String key, float value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public float getValue() {
        return mValue;
    }

    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", mKey);
            jsonObject.put("value", Float.toString(mValue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static KeyValuePair fromJson(JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            KeyValuePair result = new KeyValuePair(json.getString("key"), Float.parseFloat(json.getString("value")));
            return result;
        } catch (JSONException e) {
        }
        return null;
    }

    public static List<KeyValuePair> parseArray(JSONArray array) {
        final List<KeyValuePair> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            final JSONObject jsonObject;
            try {
                jsonObject = array.getJSONObject(i);
                KeyValuePair keyValuePair = fromJson(jsonObject);
                if (keyValuePair != null) {
                    result.add(keyValuePair);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static JSONArray toArray(List<KeyValuePair> list) {
        if (list == null || list.isEmpty()) {
            return new JSONArray();
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            result.put(list.get(i).toJson());
        }
        return result;
    }
}
