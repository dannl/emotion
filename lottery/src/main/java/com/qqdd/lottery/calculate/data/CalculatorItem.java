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
    private String mTitle;
    private String mDesc;

    public CalculatorItem(final String title, final String desc, final CalculatorImpl calculator) {
        mTitle = title;
        mDesc = desc;
        mCalculator = calculator;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDesc() {
        return mDesc;
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
