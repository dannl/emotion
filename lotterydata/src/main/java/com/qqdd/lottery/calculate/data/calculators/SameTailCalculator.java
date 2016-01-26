package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 2016/1/26.
 */
public class SameTailCalculator extends CalculatorImpl {

    public SameTailCalculator(String title, String desc) {
        super(title, desc);
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable) {

    }
}
