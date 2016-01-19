package com.qqdd.lottery.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by danliu on 1/15/16.
 */
public abstract class LotteryData implements Lottery {

    private final Set<Integer> mNormal = new HashSet<>();
    private final Set<Integer> mSpecial = new HashSet<>();
    private final int mNormalSize;
    private final int mSpecialSize;
    private final int mNormalRange;
    private final int mSpecialRange;

    public LotteryData() {
        mNormalSize = normalNumSize();
        mSpecialSize = specialNumSize();
        mNormalRange = normalNumRange();
        mSpecialRange = specialNumRange();
    }

    protected abstract int normalNumSize();

    protected abstract int specialNumSize();

    protected abstract int normalNumRange();

    protected abstract int specialNumRange();

    public void addNormal(final int number) {
        mNormal.add(number);
    }

    public void addSpecial(final int number) {
        mSpecial.add(number);
    }

    public int getNormalRange() {
        return mNormalRange;
    }

    public int getSpecialRange() {
        return mSpecialRange;
    }

    public boolean isValid() {
        return mNormal.size() == mNormalSize && mSpecial.size() == mSpecialSize;
    }

    @Override
    public Set<Integer> getSpecials() {
        return mSpecial;
    }

    @Override
    public Set<Integer> getNormals() {
        return mNormal;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int  v: mNormal) {
            builder.append(v).append(" ");
        }
        builder.append("---- ");
        for (int v : mSpecial) {
            builder.append(v).append(" ");
        }
        return builder.toString();
    }
}
