package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/20/16.
 */
public class LotteryConfiguration {



    private static class DLTInstanceHolder {
        private static final LotteryConfiguration INSTANCE = new LotteryConfiguration(5, 2, 35, 12, 2);
    }

    public static final LotteryConfiguration DLTConfiguration() {
        return DLTInstanceHolder.INSTANCE;
    }

    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;
    private final int mCost;

    private LotteryConfiguration(final int normalSize, final int specialSize, final int normalRange,
                                 final int specialRange, final int cost) {
        mNormalRange = normalRange;
        mSpecialRange = specialRange;
        mNormalSize = normalSize;
        mSpecialSize = specialSize;
        mCost = cost;
    }


    public int getCost() {
        return mCost;
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
