package com.qqdd.lottery.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

/**
 * Created by danliu on 1/19/16.
 */
public class LotteryRecord implements Lottery {

    private LotteryData mLottery;
    private Date mDate;

    public LotteryRecord(final LotteryData t) {
        mLottery = t;
    }


    @Override
    public Set<Integer> getNormals() {
        return mLottery.getNormals();
    }

    @Override
    public Set<Integer> getSpecials() {
        return mLottery.getSpecials();
    }

    public LotteryData getLottery() {
        return mLottery;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return mDate.toString() + " " + mLottery.toString();
    }

    public JSONObject toJson() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lottery", getLottery().toJson());
            json.put("date", getDate().toString());
        } catch (JSONException e) {
        }
        return json;
    }

    public static LotteryRecord fromJson(final JSONObject json) {
        if (json == null) {
            return null;
        }
        try {
            final JSONObject lottery = json.getJSONObject("lottery");
            final LotteryData lt = LotteryData.fromJson(lottery);
            if (lt == null) {
                return null;
            }
            final LotteryRecord result = new LotteryRecord(lt);
            result.setDate(DateFormat.getDateInstance().parse(json.getString("date")));
            return result;
        } catch (JSONException e) {
        } catch (ParseException e) {
        }
        return null;
    }
}
