package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 1/20/16.
 */
public interface Calculator {
    void calculate(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable);
}
