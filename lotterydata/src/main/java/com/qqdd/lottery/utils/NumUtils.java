package com.qqdd.lottery.utils;


import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        for (int i = 0; i < weights.length; i++) {
            System.out.println(weights[i]);
        }
        throw new IllegalArgumentException("bad weight!");
    }

    public static List<Integer> calculateIndexesWithWeight(final float[] weights, final
                                                   Random random, final int resultCount) {
        if (resultCount == 0) {
            return Collections.emptyList();
        }
        NumberList result = new NumberList(resultCount);
        while(result.size() < resultCount) {
            result.add(calculateIndexWithWeight(weights, random));
        }
        return result;
    }

    public static float[] newEmptyFloatArray(int i) {
        float[] result = new float[i];
        for (int j = 0; j < i; j++) {
            result[j] = 1;
        }
        return result;
    }

    public static double C(int range, int index) {
        double result = 1;
        for (int i = 0; i < index; i++) {
            result *= range - i;
        }
        for (int i = 1; i <= index; i++) {
            result /= i;
        }
        return result;
    }

    public static int calculateTotalInList(List<Integer> list) {
        int result = 0;
        if (list == null) {
            return result;
        }
        for (int i = 0; i < list.size(); i++) {
            result += list.get(i);
        }
        return result;
    }

    private static final HashMap<Lottery.Type, float[]> NORMALIZED_PROB = new HashMap<>();

    public static float[] getNormalizedProbability(Lottery.Type type) {
        float[] result = NORMALIZED_PROB.get(type);
        if (result == null) {
            LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
            if (configuration == null) {
                return null;
            }
            float normalizedProbNormal = (float) (NumUtils.C(configuration.getNormalRange() - 1,
                    configuration.getNormalSize() - 1) / NumUtils.C(configuration.getNormalRange(),
                    configuration.getNormalSize()));
            float normalizedProbSpecial = (float) (NumUtils.C(configuration.getSpecialRange() - 1,
                    configuration.getSpecialSize() - 1) / NumUtils.C(configuration.getSpecialRange(),
                    configuration.getSpecialSize()));
            result = new float[]{normalizedProbNormal, normalizedProbSpecial};
            NORMALIZED_PROB.put(type, result);
        }
        return result;
    }
}
