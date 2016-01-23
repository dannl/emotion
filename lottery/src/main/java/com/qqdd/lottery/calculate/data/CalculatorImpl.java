package com.qqdd.lottery.calculate.data;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class CalculatorImpl implements Calculator {

    private final String mTitle;
    private final String mDesc;

    public CalculatorImpl(final String title, final String desc) {
        mTitle = title;
        mDesc = desc;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDesc() {
        return mDesc;
    }
}
