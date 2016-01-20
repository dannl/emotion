package com.qqdd.lottery.data;

import java.util.ArrayList;

/**
 * Created by danliu on 1/20/16.
 */
public class NumberTable extends ArrayList<Number> {

    private int mRange;

    public NumberTable(final int range) {
        super(range + 1);
        mRange = range;
    }

    public int getRange() {
        return mRange;
    }
}
