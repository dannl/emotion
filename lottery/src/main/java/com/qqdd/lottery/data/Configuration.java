package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/20/16.
 */
public class Configuration {

    private static class DLTInstanceHolder {
        private static final Configuration INSTANCE = new Configuration(5, 2, 35, 12);
    }

    public static final Configuration DLTConfiguration() {
        return DLTInstanceHolder.INSTANCE;
    }

    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;

    private Configuration(final int normalSize, final int specialSize, final int normalRange, final int specialRange) {
        mNormalRange = normalRange;
        mSpecialRange = specialRange;
        mNormalSize = normalSize;
        mSpecialSize = specialSize;
    }

    public int getNormalRange() {
        return mNormalRange;
    }

    public int getNormalSize() {
        return mNormalSize;
    }

    public int getSpecialRange() {
        return mSpecialRange;
    }

    public int getSpecialSize() {
        return mSpecialSize;
    }
}
