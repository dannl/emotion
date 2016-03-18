package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/17/16.
 */
public class LastNMediumIncrease extends CalculatorImpl {

    public LastNMediumIncrease() {
        this(SCALE, MOVE);
    }

    public LastNMediumIncrease(float scale, float inflexion) {
        super("lastNMediumIncrease_" + scale + "_" + inflexion);
        mScale = scale;
        mInflexion = inflexion;
    }

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SPECIAL = 1;
    private static final float SCALE = 1;
    private static final float MOVE = 1;

    private final float mScale;
    private final float mInflexion;

    private static final HashMap<HistoryItem, Universe[]> CACHE = new HashMap<>();

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem record = lts.get(0);
        Universe[] cache = CACHE.get(record);
        if (cache == null) {
            cache = calculateUniverses(lts);
            CACHE.put(record, cache);
        }
        for (int i = 0; i < normalTable.size(); i++) {
            Number number = normalTable.get(i);
            final int value = number.getValue();
            int occTime = cache[TYPE_NORMAL].currentOccTime[value];
            float v = cache[TYPE_NORMAL].occRate[occTime];
            v = calculateV(cache[TYPE_NORMAL].occRate, v);
            number.setWeight(number.getWeight() * v);
        }
        for (int i = 0; i < specialTable.size(); i++) {
            Number number = specialTable.get(i);
            final int value = number.getValue();
            int occTime = cache[TYPE_SPECIAL].currentOccTime[value];
            float v = cache[TYPE_SPECIAL].occRate[occTime];
            v = calculateV(cache[TYPE_SPECIAL].occRate, v);
            number.setWeight(number.getWeight() * v);
        }

    }

    private float calculateV(float[] rates, float v) {
        float total = 0;
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < rates.length; i++) {
            total += rates[i];
            if (rates[i] > max) {
                max = rates[i];
            }
            if (rates[i] < min) {
                min = rates[i];
            }
        }
        float av = total / rates.length * mInflexion;
        if (v > av) {
            v = Math.max(max - v, min);
        }
        return (float) Math.pow(v, mScale);
    }


    public Universe[] calculateUniverses(final List<HistoryItem> history) {
        int index = 0;
        Universe[] result = new Universe[2];
        Universe universe;
        do {
            universe = calculateUniverseImpl(history, index, TYPE_NORMAL);
            index ++;
        } while (!universe.isUniverse);
        result[TYPE_NORMAL] = universe;
        index = 0;
        do {
            universe = calculateUniverseImpl(history, index, TYPE_SPECIAL);
            index ++;
        } while (!universe.isUniverse);
        result[TYPE_SPECIAL] = universe;
        return result;
    }

    public Universe calculateUniverseImpl(final List<HistoryItem> history, int lastN, int type) {
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        int total = 0;
        int totalOcc = 0;
        int[] occTimes = NumUtils.newEmptyIntArray(lastN + 1);
        final int range;
        final int size;
        if (type == TYPE_NORMAL) {
            range = configuration.getNormalRange();
            size = configuration.getNormalSize();
        } else {
            range = configuration.getSpecialRange();
            size = configuration.getSpecialSize();
        }
        int[] currentOccTimes = NumUtils.newEmptyIntArray(range + 1);
        for (int i = history.size() - lastN; i > 0; i--) {
            if (i == 1) {
                //                System.out.println("1");
            }
            if (i == 2) {
                //                System.out.println("2");
            }
            final HistoryItem record = history.get(i - 1);
            total += size;
            int[] occs = NumUtils.newEmptyIntArray(range + 1);
            for (int j = i; j < i + lastN; j++) {
                final HistoryItem item = history.get(j);
                final NumberList list;
                if (type == TYPE_NORMAL) {
                    list = item.getNormals();
                } else {
                    list = item.getSpecials();
                }
                for (int k = 0; k < list.size(); k++) {
                    occs[list.get(k)]++;
                }
            }
            final NumberList recordList;
            if (type == TYPE_NORMAL) {
                recordList = record.getNormals();
            } else {
                recordList = record.getSpecials();
            }
            for (int j = 0; j < recordList.size(); j++) {
                final int occ = occs[recordList.get(j)];
                occTimes[occ]++;
                if (occ > 0) {
                    totalOcc++;
                    //                } else {
                    //                    System.out.println("not 命中.");
                }
            }
        }
        Universe result = new Universe();
        result.isUniverse = (total == totalOcc);
        if (!result.isUniverse) {
            return result;
        }
        result.occRate = NumUtils.calculateProbability(occTimes);
        result.lastN = lastN;
        for (int i = 0; i < lastN; i++) {
            final HistoryItem item = history.get(i);
            NumberList numbers;
            if (type == TYPE_NORMAL) {
                numbers = item.getNormals();
            } else {
                numbers = item.getSpecials();
            }
            for (int j = 0; j < numbers.size(); j++) {
                currentOccTimes[numbers.get(j)] ++;
            }
        }
        result.currentOccTime = currentOccTimes;
        return result;
    }

    private static class Universe {
        boolean isUniverse;
        float[] occRate;
        int[] currentOccTime;
        int lastN;
    }
}
