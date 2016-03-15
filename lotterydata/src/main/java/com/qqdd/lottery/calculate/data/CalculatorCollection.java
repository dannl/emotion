package com.qqdd.lottery.calculate.data;

import java.util.ArrayList;

/**
 * Created by danliu on 2/2/16.
 */
public class CalculatorCollection extends ArrayList<Calculator> {

    private String mTitle;

    public CalculatorCollection(String title) {
        super();
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
