package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryRecord;

import java.util.List;

/**
 * Created by danliu on 1/27/16.
 */
public interface NumberPicker {

    void pick(List<LotteryRecord> history, Lottery picked);

}
