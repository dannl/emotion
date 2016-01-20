package com.qqdd.lottery.utils;

/**
 * Created by danliu on 1/20/16.
 */
public class NumUtils {

    public static int[] newEmptyIntArray(final int cap) {
        final int[] result = new int[cap];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }
        return result;
    }

    public static int calculateTotalInIntArray(final int[] array) {
        int result = 0;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result;
    }
}
