package com.qqdd.lottery.data;

/**
 * Created by danliu on 1/20/16.
 */
public class Number {

    private int mValue;
    private float mOccurrenceProbability;

    public Number(final int numberValue) {
        mValue = numberValue;
        mOccurrenceProbability = 0f;
    }

    public float getOccurrenceProbability() {
        return mOccurrenceProbability;
    }

    public void setOccurrenceProbability(float occurrenceProbability) {
        mOccurrenceProbability = occurrenceProbability;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

}
