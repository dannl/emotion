package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/29/16.
 */
public class SequenceOccSumRatesCalculator extends CalculatorImpl {

    /**
     * NO LESS THAN 0 !!!!!!!!!
     */

    public SequenceOccSumRatesCalculator() {
        super("seqSum");
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
                Number number = normalTable.getWithNumber(j);
                number.setWeight(number.getWeight() + prob[j]);
            }
        }
        for (int i = 0; i < specials.size(); i++) {
            int value = specials.get(i);
            float[] prob = cache.specialProb[value];
            for (int j = 1; j < prob.length; j++) {
                Number number = specialTable.getWithNumber(j);
                number.setWeight(number.getWeight() + prob[j]);
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
