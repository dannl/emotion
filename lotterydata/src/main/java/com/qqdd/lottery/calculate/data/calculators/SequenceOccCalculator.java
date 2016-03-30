package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.KeyValuePair;
import com.qqdd.lottery.data.Lottery;
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
 * Created by danliu on 3/29/16.
 */
public class SequenceOccCalculator extends CalculatorImpl {

    private static final int NORMALIZE_MAX_POWER = 3;

    public SequenceOccCalculator() {
        super("seq");
    }

    private static HashMap<HistoryItem, Prob> CACHE = new HashMap<>();

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem record = lts.get(0);
        Prob cache = CACHE.get(record);
        boolean needPrint = false;
        if (cache == null) {
            int distance = 1;
            if (record.getType() == Lottery.Type.SSQ) {
                distance = 2;
            }
            needPrint = true;
            cache = calculateImpl(lts, distance);
            CACHE.put(record, cache);
        }
        NumberList normals = record.getNormals();
        NumberList specials = record.getSpecials();
        for (int i = 0; i < normals.size(); i++) {
            int value = normals.get(i);
            float[] prob = cache.normalProb[value];
            for (int j = 1; j < prob.length; j++) {
                com.qqdd.lottery.data.Number number = normalTable.getWithNumber(j);
                number.setWeight(number.getWeight() * prob[j] * 30);
            }
        }
        for (int i = 0; i < specials.size(); i++) {
            int value = specials.get(i);
            float[] prob = cache.specialProb[value];
            for (int j = 1; j < prob.length; j++) {
                com.qqdd.lottery.data.Number number = specialTable.getWithNumber(j);
                number.setWeight(number.getWeight() * prob[j] * 10);
            }
        }
        if (needPrint) {
            float[] temp = new float[normalTable.size()];
            float total = 0;
            for (int i = 0; i < normalTable.size(); i++) {
                Number number = normalTable.get(i);
                temp[i] = number.getWeight();
                total += temp[i];
            }
            System.out.println(record.toString() + " normal weight variance: " + NumUtils.calculateVariance(temp, false));
            System.out.println(record.toString() + " normal weight av: " + (total / normalTable.size()));

            temp = new float[specialTable.size()];
            total = 0;
            for (int i = 0; i < specialTable.size(); i++) {
                Number number = specialTable.get(i);
                temp[i] = number.getWeight();
                total += temp[i];
            }
            System.out.println(record.toString() + " special weight variance: " + NumUtils.calculateVariance(temp, false));
            System.out.println(record.toString() + " special weight av: " + (total / specialTable.size()));
        }

    }

    private void normalizeNumTable(final NumberTable table) {
        if (table == null) {
            return;
        }
        final int size = table.size();
        List<KeyValuePair> origin = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            origin.add(new KeyValuePair(String.valueOf(table.get(i)
                    .getValue()), table.get(i)
                    .getWeight()));
        }
        Collections.sort(origin, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair o1, KeyValuePair o2) {
                return (int) (o1.getValue() * 10000 - o2.getValue() * 10000);
            }
        });

        writeKeyValue(origin, new File(SimpleIOUtils.getProjectRoot(), "origin" + size));


        //计算平均x, y
        float yTotal = 0;
        float xTotal = 0;
        float xYMultiTotal = 0;
        float xPower2Total = 0;
        final int n = size;
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

        float yMax = b * (size - 1) + a;
        if (yMax > NORMALIZE_MAX_POWER * a) {
            float x = (size - 1) / 2;
            float y = b * x + a;
            float aPlus = (yMax - NORMALIZE_MAX_POWER * a) / (NORMALIZE_MAX_POWER + 1);
            a += aPlus;
            b = (y - a) / x;
        }

        List<KeyValuePair> line = new ArrayList<>();
        for (int i = 0; i < origin.size(); i++) {
            final KeyValuePair item = origin.get(i);
            final KeyValuePair lineItem = new KeyValuePair(item.getKey(), i * b + a);
            line.add(lineItem);
        }

        writeKeyValue(line, new File(SimpleIOUtils.getProjectRoot(), "line" + size));

        for (int i = 0; i < line.size(); i++) {
            final KeyValuePair pair = line.get(i);
            int key = Integer.parseInt(pair.getKey());
            Number number = table.getWithNumber(key);
            number.setWeight(pair.getValue());
        }
    }

    private void writeKeyValue(final List<KeyValuePair> values, File file) {
//                try {
//                    SimpleIOUtils.saveToFile(file, KeyValuePair.toArray(values)
//                            .toString());
//                } catch (IOException e) {
//                }
    }

    private Prob calculateImpl(List<HistoryItem> items, int distance) {
        final HistoryItem current = items.get(0);
        final LotteryConfiguration configuration = current.getConfiguration();
        final int[][] normalsMap = NumUtils.newEmptyIntArray(configuration.getNormalRange() + 1,
                configuration.getNormalRange() + 1);
        final int[][] specialsMap = NumUtils.newEmptyIntArray(configuration.getSpecialRange() + 1,
                configuration.getSpecialRange() + 1);
        for (int i = items.size() - 1; i > distance - 1; i--) {
            HistoryItem item = items.get(i);
            HistoryItem testTarget = items.get(i - distance);
            for (int j = 0; j < item.getNormals()
                    .size(); j++) {
                int value = item.getNormals()
                        .get(j);
                for (int jk = 0; jk < testTarget.getNormals()
                        .size(); jk++) {
                    normalsMap[value][testTarget.getNormals()
                            .get(jk)]++;
                }
            }
            for (int j = 0; j < item.getSpecials()
                    .size(); j++) {
                int value = item.getSpecials()
                        .get(j);
                for (int jk = 0; jk < testTarget.getSpecials()
                        .size(); jk++) {
                    specialsMap[value][testTarget.getSpecials()
                            .get(jk)]++;
                }
            }
        }
        final Prob result = new Prob();
        result.normalProb = new float[configuration.getNormalRange() + 1][];
        result.specialProb = new float[configuration.getSpecialRange() + 1][];
        for (int i = 1; i < result.normalProb.length; i++) {
            result.normalProb[i] = NumUtils.calculateProbability(normalsMap[i]);
        }
        for (int i = 1; i < result.specialProb.length; i++) {
            result.specialProb[i] = NumUtils.calculateProbability(specialsMap[i]);
        }
        return result;
    }


    private static class Prob {

        float[][] normalProb;
        float[][] specialProb;

    }
}
