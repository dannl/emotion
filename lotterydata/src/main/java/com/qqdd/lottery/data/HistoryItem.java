package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by danliu on 2016/1/28.
 */
public class HistoryItem extends LotteryRecord {

    public HistoryItem(Lottery t) {
        super(t);
    }

    private HistoryItem() {
        super(null);
    }

    public static HistoryItem fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        final HistoryItem result = new HistoryItem();
        try {
            loadBaseData(result, json);
        } catch (JSONException ignored) {
            return null;
        }
        //FIXME more data of history items.
        return result;
    }

}
