package com.qqdd.lottery.data;

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
}
