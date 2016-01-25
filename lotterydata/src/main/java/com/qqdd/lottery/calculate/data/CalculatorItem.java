package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 1/21/16.
 */
public class CalculatorItem implements Calculator {

    private boolean mEditable;
    private boolean mDeletable;
    private CalculatorImpl mCalculator;

    public CalculatorItem(final CalculatorImpl calculator) {
        mCalculator = calculator;
    }

    @Override
    public String getTitle() {
        return mCalculator.getTitle();
    }

    @Override
    public String getDesc() {
        return mCalculator.getDesc();
    }

    public boolean isEditable() {
        return mEditable;
    }

    public boolean isDeletable() {
        return mDeletable;
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
    }

    public void setDeletable(boolean deletable) {
        mDeletable = deletable;
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable) {
        if (mCalculator != null) {
            mCalculator.calculate(lts, normalTable, specialTable);
        }
    }
}