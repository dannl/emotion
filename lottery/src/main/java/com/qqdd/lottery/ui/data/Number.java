package com.qqdd.lottery.ui.data;

/**
 * Created by danliu on 1/20/16.
 */
public class Number {

    private int mValue;
    private float mOccurrenceProbability;
    private int mOccurrence;

    public Number(final int numberValue) {
        mValue = numberValue;
        mOccurrenceProbability = 0f;
        mOccurrence = 0;
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

    public int getOccurrence() {
        return mOccurrence;
    }

    public void plusOccurence() {
        mOccurrence++;
    }

    public void plusOccurence(final int times) {
        mOccurrence += times;
    }
}
