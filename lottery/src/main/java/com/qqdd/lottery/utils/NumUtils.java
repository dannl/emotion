package com.qqdd.lottery.utils;

import com.qqdd.lottery.utils.data.NumberList;

import java.util.Random;

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

    public static float[] calculateProbability(final int[] weightArray) {
        if (weightArray == null) {
            return new float[0];
        }
        float[] result = new float[weightArray.length];
        float total = 0;
        for (int i = 0; i < weightArray.length; i++) {
            total += weightArray[i];
        }
        if (total == 0) {
            return result;
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = weightArray[i] / total;
        }
        return result;
    }

    public static int calculateSameCount(final NumberList src, final NumberList dest) {
        if (src == null || dest == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < src.size(); i++) {
            if (dest.contains(src.get(i))) {
                result ++;
            }
        }
        return result;
    }

    public static int calculateIndexWithWeight(final float[] weights, final Random random) {
        float rate  = random.nextFloat();
        float total = 0;
        for (int i = 0; i < weights.length; i++) {
            total+= weights[i];
        }
        float totalRate = 0;
        for (int i = 0; i < weights.length; i++) {
            if (totalRate <= rate && rate < totalRate + weights[i] / total) {
                return i;
            }
            totalRate += weights[i] / total;
        }
        return 0;
    }
}
