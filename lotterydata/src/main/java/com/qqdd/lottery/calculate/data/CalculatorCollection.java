package com.qqdd.lottery.calculate.data;

import java.util.ArrayList;

/**
 * Created by danliu on 2/2/16.
 */
public class CalculatorCollection extends ArrayList<Calculator> {

    private String mDesc;

    public CalculatorCollection(String desc) {
        super();
        mDesc = desc;
    }

    public String getDesc() {
        return mDesc;
    }
}
