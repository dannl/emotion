package com.qqdd.lottery.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public abstract class Lottery {

    private final Set<Integer> mNormal = new HashSet<>();
    private final Set<Integer> mSpecial = new HashSet<>();
    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;

    public Lottery() {
        mNormalSize = getNormalNumSize();
        mSpecialSize = getSpecialNumSize();
        mNormalRange = getNormalNumRange();
        mSpecialRange = getSpecialNumRange();
    }

    protected abstract int getNormalNumSize();

    protected abstract int getSpecialNumSize();

    protected abstract int getNormalNumRange();

    protected abstract int getSpecialNumRange();

}
