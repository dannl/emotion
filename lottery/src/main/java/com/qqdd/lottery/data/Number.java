package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/20/16.
 */
public class Number {

    private int mValue;
    private float mWeight;

    public Number(final int numberValue) {
        mValue = numberValue;
        mWeight = 0f;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float occurrenceProbability) {
        mWeight = occurrenceProbability;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

}
