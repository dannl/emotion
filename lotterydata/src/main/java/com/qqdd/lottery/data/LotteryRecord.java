package com.qqdd.lottery.data;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by danliu on 1/19/16.
 */
public abstract class LotteryRecord implements ILottery {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

    public static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    private Lottery mLottery;
    private Date mDate;

    public LotteryRecord(final Lottery t) {
        mLottery = t;
    }

    protected LotteryRecord() {}

    public RewardRule.RewardDetail calculateRewardDetail(final LotteryRecord record) {
        return mLottery.calculateRewardDetail(record);
    }

    @Override
    public LotteryConfiguration getConfiguration() {
        return mLottery.getConfiguration();
    }

    @Override
    public NumberList getNormals() {
        return mLottery.getNormals();
    }

    @Override
    public NumberList getSpecials() {
        return mLottery.getSpecials();
    }

    public Lottery getLottery() {
        return mLottery;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDateDisplay() {
        return DISPLAY_DATE_FORMAT.format(mDate);
    }

    @Override
    public boolean isValid() {
        return mLottery.isValid();
    }

    @Override
    public void sort() {
        mLottery.sort();
    }

    @Override
    public String toString() {
        return DISPLAY_DATE_FORMAT.format(mDate) + " " + mLottery.toString();
    }

    public JSONObject toJson() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lottery", getLottery().toJson());
            json.put("date", DATE_FORMAT.format(mDate));
        } catch (JSONException e) {
        }
        return json;
    }

    @Override
    public int hashCode() {
        if (mDate == null) {
            return super
                    .hashCode();
        } else {
            return DATE_FORMAT.format(mDate).hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else {
            return hashCode() == o.hashCode();
        }
    }

    @Override
    public Lottery.Type getType() {
        return mLottery.getType();
    }

    public static void loadBaseData(final LotteryRecord record, final JSONObject json) throws JSONException {
        if (record == null || json
                 == null) {
            return;
        }
        try {
            final JSONObject lottery = json.getJSONObject("lottery");
            final Lottery lt = Lottery.fromJson(lottery);
            if (lt == null) {
                throw new JSONException("failed to parse lottery.");
            }
            record.mLottery = lt;
            record.setDate(DATE_FORMAT.parse(json.getString("date")));
        } catch (ParseException ignored) {
        }
    }
}
