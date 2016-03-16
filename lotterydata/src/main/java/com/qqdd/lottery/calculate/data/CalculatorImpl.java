package com.qqdd.lottery.calculate.data;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class CalculatorImpl implements Calculator {

    private final String mTitle;

    public CalculatorImpl(final String title) {
        mTitle = title;
        if (TextUtils.isEmpty(title)) {
            throw new IllegalArgumentException("bad title!");
        }
    }

    public String getTitle() {
        return mTitle;
    }

}
