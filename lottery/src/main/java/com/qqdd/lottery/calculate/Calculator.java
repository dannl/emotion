package com.qqdd.lottery.calculate;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 1/20/16.
 */
public abstract class Calculator {
    public abstract void calculate(List<LotteryRecord> lts, NumberTable table);
}
