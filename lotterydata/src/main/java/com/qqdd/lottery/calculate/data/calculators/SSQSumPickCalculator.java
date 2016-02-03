package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 2/3/16.
 */
public class SSQSumPickCalculator extends CalculatorImpl {
    public SSQSumPickCalculator() {
        super("网上看到的和值选", "");
    }

    private static class Selections {
        NumberList selections;
        float power;
    }

    private static HashMap<HistoryItem, Selections> CACHE = new HashMap<>();

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem record = lts.get(0);
        Selections cache = CACHE.get(record);
        if (cache == null) {
            cache = calculateImpl(lts);
            CACHE.put(record, cache);
        }
        final NumberList selections = cache.selections;
        for (int i = 0; i < selections.size(); i++) {
            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(selections.get(i));
            number.setWeight(number.getWeight() * cache.power);
        }
    }

    private Selections calculateImpl(List<HistoryItem> history) {
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        final int range = configuration.getNormalRange();
        int[] sameCounts = NumUtils.newEmptyIntArray(configuration.getNormalSize() + 1);
        int totalRightCount = 0;
        int totalCalculateLoop = 0;
        for (int i = 0; i < history.size() - 1; i++) {
            totalCalculateLoop ++;
            final HistoryItem record = history.get(i);
            final HistoryItem older = history.get(i + 1);

            final int total = getTotal(older);
            NumberList calculated = new NumberList();
            final NumberList olderNormals = older.getNormals();
            for (int j = 0; j < olderNormals.size(); j++) {
                int tail = total / olderNormals.get(j) - 1;
                int indexer = 1;
                while (tail <= range) {
                    if (tail > 0) {
                        calculated.add(tail);
                    }
                    tail += indexer * 10;
                }
            }
            final NumberList normals = record.getNormals();
            int sameCount =0;
            for (int j = 0; j < normals.size(); j++) {
                final Integer value = normals.get(j);
                if (calculated.contains(value)) {
                    sameCount ++;
                }
            }
            if (sameCount > 0) {
                totalRightCount ++;
            }
            sameCounts[sameCount] ++;
        }

        final float totalRate = ((float) totalRightCount) / totalCalculateLoop;
        float[] sameRates = NumUtils.calculateProbability(sameCounts);
        final Selections selections = new Selections();
        selections.power = (1 + totalRate) * (2 - sameRates[0]);
        NumberList calculated = new NumberList();

        final NumberList recordNormals = history.get(0).getNormals();
        final int total = getTotal(history.get(0));
        for (int j = 0; j < recordNormals.size(); j++) {
            int tail = total / recordNormals.get(j) - 1;
            int indexer = 1;
            while (tail <= range) {
                if (tail > 0) {
                    calculated.add(tail);
                }
                tail += indexer * 10;
            }
        }
        selections.selections = calculated;
        return selections;
    }


    public void test(List<HistoryItem> history) {
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        final int range = configuration.getNormalRange();
        int[] sameCounts = NumUtils.newEmptyIntArray(configuration.getNormalSize() + 1);
        int totalRightCount = 0;
        int totalCalculateLoop = 0;
        for (int i = 0; i < history.size() - 1; i++) {
            totalCalculateLoop ++;
            final HistoryItem record = history.get(i);
            final HistoryItem older = history.get(i + 1);

            final int total = getTotal(older);
            NumberList calculated = new NumberList();
            final NumberList olderNormals = older.getNormals();
            for (int j = 0; j < olderNormals.size(); j++) {
                int tail = total / olderNormals.get(j) - 1;
                int indexer = 1;
                while (tail <= range) {
                    if (tail > 0) {
                        calculated.add(tail);
                    }
                    tail += indexer * 10;
                }
            }
            if (calculated.size() != 16) {
                System.out.println("not 16 + " + older);
            }
            final NumberList normals = record.getNormals();
            int sameCount =0;
            for (int j = 0; j < normals.size(); j++) {
                final Integer value = normals.get(j);
                if (calculated.contains(value)) {
                    sameCount ++;
                }
            }
            if (sameCount != 3) {
                System.out.println("same count not 3");
            }
            if (sameCount > 0) {
                totalRightCount ++;
            }
            sameCounts[sameCount] ++;
        }

        System.out.println("total right count: " + totalRightCount);
        System.out.println("total calculate count: " + totalCalculateLoop);
        System.out.println("right rate: " + ((float) totalRightCount) / totalCalculateLoop);
    }

    private int getTotal(HistoryItem item) {
        final NumberList normals = item.getNormals();
        int total = 0;
        for (int i = 0; i < normals.size(); i++) {
            total += normals.get(i);
        }
        return total;
    }
}
