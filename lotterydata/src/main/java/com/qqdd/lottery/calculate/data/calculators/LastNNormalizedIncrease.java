package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.KeyValuePair;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.SimpleIOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/17/16.
 */
public class LastNNormalizedIncrease extends CalculatorImpl {

    public LastNNormalizedIncrease() {
        this(SCALE, MOVE);
    }

    public LastNNormalizedIncrease(float scale, float inflexion) {
        super("lastNNormalizedIncrease_" + scale + "_" + inflexion);
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
            com.qqdd.lottery.data.Number number = normalTable.get(i);
            final int value = number.getValue();
            int occTime = cache[TYPE_NORMAL].currentOccTime[value];
            float v = cache[TYPE_NORMAL].occRate[occTime];
//            v = calculateV(cache[TYPE_NORMAL].occRate, v);
            number.setWeight(number.getWeight() * v);
        }
        for (int i = 0; i < specialTable.size(); i++) {
            Number number = specialTable.get(i);
            final int value = number.getValue();
            int occTime = cache[TYPE_SPECIAL].currentOccTime[value];
            float v = cache[TYPE_SPECIAL].occRate[occTime];
//            v = calculateV(cache[TYPE_SPECIAL].occRate, v);
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

    public void testChangeLine(final List<HistoryItem> history) {
        Universe[] result = calculateUniverses(history);
        final Universe universe = result[TYPE_NORMAL];
        final LotteryConfiguration configuration = history.get(0).getConfiguration();
        normalizeUniverse(universe, configuration,TYPE_NORMAL);

    }

    private void normalizeUniverse(Universe universe, LotteryConfiguration configuration, int type) {
        final File dir = new File(SimpleIOUtils.getProjectRoot(), "lines");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        NumberTable table;
        if (type == TYPE_NORMAL) {
            table = new NumberTable(configuration.getNormalRange());
        } else {
            table = new NumberTable(configuration.getSpecialRange());
        }
        List<KeyValuePair> origin = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            final Number number = table.get(i);
            final int value = number.getValue();
            final float rate = universe.occRate[universe.currentOccTime[value]];
            origin.add(new KeyValuePair(String.valueOf(value), rate));
        }
        Collections.sort(origin, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair o1, KeyValuePair o2) {
                return (int) (o1.getValue() * 10000 - o2.getValue() * 10000);
            }
        });

        writeKeyValue(origin, new File(dir, "origin"));

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

        writeKeyValue(line, new File(dir, "line"));
        //降低斜率
        /**
         * 看起来线性回归的过程就是降低斜率了的..
         */
        //计算平均折线, 带入偏移
        List<KeyValuePair> brokenLine = new ArrayList<>();
        float inflexionX = ((float) n) / 2 * mInflexion;
        float inflexionY = b * inflexionX + a;
        //计算拐线的截距
        float inflexionA = inflexionY + b * inflexionX;
        for (int i = 0; i < n; i++) {
            final KeyValuePair item = line.get(i);
            final KeyValuePair inflexionItem;
            if (i > inflexionX) {
                inflexionItem = new KeyValuePair(item.getKey(), - b * i + inflexionA);
            } else {
                inflexionItem = new KeyValuePair(item.getKey(), item.getValue());
            }
            brokenLine.add(inflexionItem);
        }

        writeKeyValue(brokenLine, new File(dir, "broken"));
        //计算二次曲线
        /**
         * y=ax²+bx+c（a≠0）的顶点坐标公式是
         *（-b/2a,（4ac-b²）/4a)
         */
        List<KeyValuePair> parabola = new ArrayList<>();
        //计算a,b, 考虑scale.
        float parabolaC = brokenLine.get(0).getValue();
        float x1 = inflexionX;
        float y1 = inflexionY * mScale;
        float x2 = brokenLine.size() - 1;
        float y2 = brokenLine.get(brokenLine.size() - 1).getValue();
        float parabolaA = (x1 * y2 - x2 * y1 - parabolaC * x1 + parabolaC * x2) / (x1 * x2 * x2 - x2 * x1 * x1);
        float parabolaB = (y1 - parabolaC - parabolaA * x1 * x1) / x1;
        //        float parabolaA = (parabolaC - y1) / (x1 * x1);
        //        float parabolaB = - 2 * parabolaA * x1;
        for (int i = 0; i < brokenLine.size(); i++) {
            final KeyValuePair item = brokenLine.get(i);
            final KeyValuePair parabolaItem = new KeyValuePair(item.getKey(),
                    parabolaA * i * i + parabolaB * i + parabolaC);
            parabola.add(parabolaItem);
        }

        writeKeyValue(parabola, new File(dir, "parabola" + mInflexion + mScale));

        for (int i = 0; i < parabola.size(); i++) {
            final KeyValuePair item = parabola.get(i);
            final int key = Integer.parseInt(item.getKey());
            final float value = item.getValue();
            universe.occRate[universe.currentOccTime[key]] = value;
        }
    }

    private void writeKeyValue(final List<KeyValuePair> values, File file) {
//        try {
//            SimpleIOUtils.saveToFile(file, KeyValuePair.toArray(values).toString());
//        } catch (IOException e) {
//        }
    }

    public Universe[] calculateUniverses(final List<HistoryItem> history) {
        int index = 0;
        Universe[] result = new Universe[2];
        Universe universe;
        final LotteryConfiguration configuration = history.get(0).getConfiguration();
        do {
            universe = calculateUniverseImpl(history, index, TYPE_NORMAL);
            index ++;
        } while (!universe.isUniverse);
        System.out.println("================ normal lastN : " + universe.lastN + "=================");
        result[TYPE_NORMAL] = universe;
        normalizeUniverse(universe, configuration, TYPE_NORMAL);
        index = 0;
        do {
            universe = calculateUniverseImpl(history, index, TYPE_SPECIAL);
            index ++;
        } while (!universe.isUniverse);
        System.out.println("================ special lastN : " + universe.lastN + "=================");
        result[TYPE_SPECIAL] = universe;
        normalizeUniverse(universe, configuration, TYPE_SPECIAL);
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
