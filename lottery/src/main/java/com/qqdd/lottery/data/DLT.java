package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/19/16.
 */
public class DLT extends LotteryData {

    @Override
    protected int normalNumSize() {
        return 5;
    }

    @Override
    protected int specialNumSize() {
        return 2;
    }

    @Override
    protected int normalNumRange() {
        return 35;
    }

    @Override
    protected int specialNumRange() {
        return 12;
    }

}
