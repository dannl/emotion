package com.qqdd.lottery.data;

import java.util.ArrayList;

/**
 * Created by danliu on 1/20/16.
 */
public class NumberTable extends ArrayList<Number> {

    private int mRange;

    public NumberTable(final int range) {
        super(range);
        mRange = range;
        for (int i = 0; i < range; i++) {
            add(new Number(i + 1));
        }
    }

    public float getTotalWeight() {
        float result = 0;
        for (int i = 0; i < size(); i++) {
            result += get(i).getWeight();
        }
        return result;
    }

    public Number getWithNumber(final int number) {
        if (number > size() || number < 1) {
            return null;
        }
        return get(number - 1);
    }

    public int getRange() {
        return mRange;
    }
}
