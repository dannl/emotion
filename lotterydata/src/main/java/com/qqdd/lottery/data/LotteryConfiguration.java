package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/20/16.
 */
public class LotteryConfiguration {

    private static class DLTInstanceHolder {
        private static final LotteryConfiguration INSTANCE = new LotteryConfiguration(Lottery.Type.DLT, 5, 2, 35, 12, 2);
    }

    public static final LotteryConfiguration DLTConfiguration() {
        return DLTInstanceHolder.INSTANCE;
    }

    private static class SSQInstanceHolder {
        private static final LotteryConfiguration INSTANCE = new LotteryConfiguration(Lottery.Type.SSQ, 6, 1, 33, 16, 2);
    }

    public static final LotteryConfiguration SSQConfiguration() {
        return SSQInstanceHolder.INSTANCE;
    }

    public static LotteryConfiguration getWithType(final Lottery.Type type) {
        if (type == Lottery.Type.DLT) {
            return DLTConfiguration();
        } else if (type == Lottery.Type.SSQ) {
            return SSQConfiguration();
        }
        return null;
    }

    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;
    private final int mCost;
    private Lottery.Type mType;

    private LotteryConfiguration(Lottery.Type type, final int normalSize, final int specialSize, final int normalRange,
                                 final int specialRange, final int cost) {
        mNormalRange = normalRange;
        mSpecialRange = specialRange;
        mNormalSize = normalSize;
        mSpecialSize = specialSize;
        mCost = cost;
        mType = type;
    }




    public Lottery.Type getType() {
        return mType;
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
