package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.utils.NumUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 1. 得到当前record情况下,过往第N期出现了所有的num. --> 求得N
 * 2. 统计这N期所有数字出现的概率, 并做线性回归,得到概率分布直线.
 * 3. 统计当前record在上述概率曲线的命中区间, 0.0 - 0.1 - 0.2 ... 1.0 分别对应区间0-9
 * 4. 根据历史数据的区间偏好选取.
 */
public class UniverseRateRangeCalculator extends CalculatorImpl {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SPECIAL = 1;

    public UniverseRateRangeCalculator() {
        super("urr");
    }

    private HashMap<HistoryItem, Universe> UNIVERSE_CACHE = new HashMap<>();
    private HashMap<HistoryItem, HistoryUniverse> HISTORY_UNIVERSE_CACHE = new HashMap<>();

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem item = lts.get(0);
        HistoryUniverse cache = HISTORY_UNIVERSE_CACHE.get(item);
        if (cache == null) {
            cache = calculateImpl(lts);
            HISTORY_UNIVERSE_CACHE.put(item, cache);
        }
        for (int i = 0; i < cache.universe.normalRates.size(); i++) {
            final KeyValuePair pair = cache.universe.normalRates.get(i);
            final int value = Integer.parseInt(pair.getKey());
            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(value);
            number.setWeight(number.getWeight() * cache.normalSelectionIndexRates[i]);
        }
        for (int i = 0; i < cache.universe.specialRates.size(); i++) {
            final KeyValuePair pair = cache.universe.specialRates.get(i);
            final int value = Integer.parseInt(pair.getKey());
            final com.qqdd.lottery.data.Number number = specialTable.getWithNumber(value);
            number.setWeight(number.getWeight() * cache.specialSelectionIndexRates[i]);
        }
    }

    private HistoryItem mSince = null;

    public HistoryUniverse calculateImpl(final List<HistoryItem> history) {
        final LotteryConfiguration configuration = history.get(0).getConfiguration();
        final int index = calculateSince(history, configuration);
        int[] normalSelectionsCount = new int[configuration.getNormalRange()];
        int[] specialSelectionsCount = new int[configuration.getSpecialRange()];
        HistoryUniverse result = new HistoryUniverse();
        for (int i = index - 1; i >= 0; i--) {
            final HistoryItem item = history.get(i);
            Universe cache = UNIVERSE_CACHE.get(item);
            if (cache == null) {
                cache = calculateUniverse(history, configuration, i, item);
                UNIVERSE_CACHE.put(item, cache);
            }
            for (int j = 0; j < cache.normalSelection.length; j++) {
                normalSelectionsCount[cache.normalSelection[j]] ++;
            }
            for (int j = 0; j < cache.specialSelection.length; j++) {
                specialSelectionsCount[cache.specialSelection[j]] ++;
            }
        }
        result.normalSelectionIndexRates = NumUtils.calculateProbability(normalSelectionsCount);
        result.specialSelectionIndexRates = NumUtils.calculateProbability(specialSelectionsCount);
        result.universe = UNIVERSE_CACHE.get(history.get(0));
        return result;
    }

    private int calculateSince(List<HistoryItem> history, LotteryConfiguration configuration) {
        if (mSince == null) {
            int[] occTime = NumUtils.newEmptyIntArray(configuration.getNormalRange() + 1);
            int[] specialOccTime = NumUtils.newEmptyIntArray(configuration.getSpecialRange() + 1);
            for (int i = history.size() - 1; i >= 0; i--) {
                final HistoryItem item = history.get(i);
                final NumberList normals = item.getNormals();
                final NumberList specials = item.getSpecials();
                for (int j = 0; j < normals.size(); j++) {
                    occTime[normals.get(j)] ++;
                }
                for (int j = 0; j < specials.size(); j++) {
                    specialOccTime[specials.get(j)] ++;
                }
                boolean isUniverse = true;
                for (int j = 1; j < occTime.length; j++) {
                    if (occTime[j] == 0) {
                        isUniverse = false;
                        break;
                    }
                }
                if (isUniverse) {
                    for (int j = 1; j < specialOccTime.length; j++) {
                        if (specialOccTime[j] == 0) {
                            isUniverse = false;
                            break;
                        }
                    }
                }
                if (isUniverse) {
                    mSince = item;
                    break;
                }
            }
        }
        final int index = history.indexOf(mSince);
        if (index < 0 || index >= history.size()) {
            throw new IllegalStateException("history is far too less.");
        }
        return index;
    }

    private Universe calculateUniverse(List<HistoryItem> history,
                                       LotteryConfiguration configuration, int i,
                                       HistoryItem item) {
        final Universe cache;
        final List<HistoryItem> sub = history.subList(i + 1, history.size());
        cache = calculateUniverseRates(sub);
        //what does it choose?
        cache.normalSelection = new int[configuration.getNormalSize()];
        cache.specialSelection = new int[configuration.getSpecialSize()];
        final NumberList normals = item.getNormals();
        final NumberList specials = item.getSpecials();
        for (int j = 0; j < normals.size(); j++) {
            for (int k = 0; k < cache.normalRates.size(); k++) {
                if (normals.get(j) == Integer.parseInt(cache.normalRates.get(k).getKey())) {
                    cache.normalSelection[j] = k;
                }
            }
        }
        for (int j = 0; j < specials.size(); j++) {
            for (int k = 0; k < cache.specialRates.size(); k++) {
                if (specials.get(j) == Integer.parseInt(cache.specialRates.get(k).getKey())) {
                    cache.specialSelection[j] = k;
                }
            }
        }
        Arrays.sort(cache.normalSelection);
        Arrays.sort(cache.specialSelection);
        return cache;
    }

    private Universe calculateUniverseRates(List<HistoryItem> history) {
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        int[] normalOcc = NumUtils.newEmptyIntArray(configuration.getNormalRange() + 1);
        float[] normalOccRate = new float[configuration.getNormalRange() + 1];
        for (int i = 0; i < history.size(); i++) {
            HistoryItem item = history.get(i);
            for (int j = 0; j < item.getNormals().size(); j++) {
                normalOcc[item.getNormals().get(j)] ++;
            }
            boolean isUniverse = true;
            for (int j = 1; j < normalOcc.length; j++) {
                if (normalOcc[j] == 0) {
                    isUniverse = false;
                    break;
                }
            }
            if (isUniverse) {
                normalOccRate = NumUtils.calculateProbability(normalOcc);
                break;
            }
        }
        int[] specialOcc = NumUtils.newEmptyIntArray(configuration.getSpecialRange() + 1);
        float[] specialOccRate = new float[configuration.getSpecialRange() + 1];
        for (int i = 0; i < history.size(); i++) {
            HistoryItem item = history.get(i);
            for (int j = 0; j < item.getSpecials().size(); j++) {
                specialOcc[item.getSpecials().get(j)] ++;
            }
            boolean isUniverse = true;
            for (int j = 1; j < specialOcc.length; j++) {
                if (specialOcc[j] == 0) {
                    isUniverse = false;
                    break;
                }
            }
            if (isUniverse) {
                specialOccRate = NumUtils.calculateProbability(specialOcc);
                break;
            }
        }
        if (normalOccRate == null || specialOccRate == null) {
            throw new IllegalStateException("history too less, no fucking universe!");
        }
        List<KeyValuePair> normalRates = new ArrayList<>();
        List<KeyValuePair> specialRates = new ArrayList<>();
        for (int i = 1; i < normalOccRate.length; i++) {
            normalRates.add(new KeyValuePair(String.valueOf(i), normalOccRate[i]));
        }
        for (int i = 1; i < specialOccRate.length; i++) {
            specialRates.add(new KeyValuePair(String.valueOf(i), specialOccRate[i]));
        }

        Collections.sort(normalRates, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair o1, KeyValuePair o2) {
                return (int) (o1.getValue() * 10000 - o2.getValue() * 10000);
            }
        });
        Collections.sort(specialRates, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair o1, KeyValuePair o2) {
                return (int) (o1.getValue() * 10000 - o2.getValue() * 10000);
            }
        });

        normalRates = linearRegression(normalRates);
        specialRates = linearRegression(specialRates);

        Universe result = new Universe();
        result.normalRates = normalRates;
        result.specialRates = specialRates;

        return result;
    }


    private List<KeyValuePair> linearRegression(List<KeyValuePair> origin) {
        //计算平均x, y
        float yTotal = 0;
        float xTotal = 0;
        float xYMultiTotal = 0;
        float xPower2Total = 0;
        final int n = origin.size();
        for (int i = 0; i < n; i++) {
            yTotal += origin.get(i).getValue();
            xTotal += i;
            xYMultiTotal += i * origin.get(i).getValue();
            xPower2Total += Math.pow(i, 2);
        }
        float yAv = yTotal / n;
        float xAv = xTotal / n;
        //计算拟合线
        /**
         * 线性回归公式.
         * b = (∑xy - n * xAv * yAv) / (∑(x^2) - n * (xAv^2))
         * a = yAv - b * xAv
         * y = b * x + a
         */
        float b = (float) ((xYMultiTotal - n * xAv * yAv) / (xPower2Total - n * Math.pow(xAv, 2)));
        float a = yAv - b * xAv;

        List<KeyValuePair> line = new ArrayList<>();
        for (int i = 0; i < origin.size(); i++) {
            final KeyValuePair item = origin.get(i);
            final KeyValuePair lineItem = new KeyValuePair(item.getKey(), i * b + a);
            line.add(lineItem);
        }
        return line;
    }

    private static class HistoryUniverse {
        Universe universe;
        float[] normalSelectionIndexRates;
        float[] specialSelectionIndexRates;
    }

    private static class Universe {
        List<KeyValuePair> normalRates;
        List<KeyValuePair> specialRates;
        int[] normalSelection;
        int[] specialSelection;
    }
}
