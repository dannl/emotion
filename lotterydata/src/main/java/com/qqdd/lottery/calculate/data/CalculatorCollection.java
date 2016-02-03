package com.qqdd.lottery.calculate.data;

import java.util.ArrayList;

/**
 * Created by danliu on 2/2/16.
 */
public class CalculatorCollection extends ArrayList<Calculator> {

    private final String mDesc;
    private String mTitle;

    public CalculatorCollection(String title, String desc) {
        super();
        mTitle = title;
        mDesc = desc;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getTitle() {
        return mTitle;
    }
}
